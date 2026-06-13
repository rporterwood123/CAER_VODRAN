# CLAUDE.md

Guidance for working in this repository — and for writing ActionC programs anywhere.

ActionC is an esoteric language (a superset of [ArnoldC](https://github.com/lhartikk/ArnoldC))
whose keywords are action-movie one-liners. The compiler is written in Scala, parses
with parboiled, emits JVM bytecode with ASM, and produces runnable `.class` files.

This file has two parts:

- **Part 1 — Working on the compiler.** For an agent hacking the Scala/ASM internals
  of *this* repo.
- **Part 2 — Writing ActionC programs.** Portable; self-contained enough to copy into
  any project that compiles `.actionc` source.

---

## Toolchain

The build needs **JDK 21** and **sbt** (any JDK 21 + sbt 1.x works; `build.sbt` pins
Scala 2.12.18). If they are not on your default `PATH`, export them first:

```bash
export JAVA_HOME=/path/to/your/jdk-21
export PATH="$JAVA_HOME/bin:/path/to/your/sbt/bin:$PATH"
```

## Commands

```bash
sbt test                                          # run the suite — 247 tests, ~2s
sbt assembly                                      # build target/scala-2.12/ActionC.jar
java -jar target/scala-2.12/ActionC.jar prog.actionc   # compile prog.actionc -> prog.class
java -jar target/scala-2.12/ActionC.jar -run prog.actionc   # compile AND run
java prog                                         # run a class compiled separately
```

`sbt test` is fast (compilation dominates). Tests run **serially**
(`Test / parallelExecution := false`) because file-I/O and async suites touch shared
state — keep it that way.

---

# Part 1 — Working on the compiler

## Status (ground truth, verified 2026-06-11)

All language tiers are **implemented and tested**: comments, the full
comparison/bitwise/logical operator set, for/break/continue/switch, strings, floats,
int arrays, error handling, the math/string/time/file stdlib, OOP with inheritance and
instance methods, lambdas + function references, and async. Float **arithmetic**
(`+ − × ÷ %` with int→float promotion), `floor`/`ceil`/`round`, and numeric↔string
conversions landed in the Tier 1 numerics pass; Tier 2 added the string toolkit
(`replace`, `startsWith`, `endsWith`, `charAt`, `reverse`); Tier 3 added typed arrays
(float & string) and `split`; a follow-up let `TALK TO THE HAND` print string-returning
functions directly. A robustness pass (2026-06-11) fixed stdin reads to share one
program-wide `Scanner` (multiple reads now survive piped input), gave int literals the
full 32-bit range (`SIPUSH` used to truncate anything past ±32767), made comparisons
type-aware (string content equality via `String.equals`, float comparisons via
`FCMPL`/`FCMPG` — see `ComparisonCodegen.scala`), and made async blocks
exception-safe (`done` is set in a finally so `HOLD THE LINE` can't spin forever) on
daemon threads. A second pass the same day fixed try/catch/finally (the finally now
runs when the catch body throws, and nested try blocks dispatch to the innermost
handler), let `GET OUT` break out of a switch, scoped main's locals into their own
frame table (so async blocks/lambdas referencing outer variables fail at compile time
instead of miscompiling), enforced int-only method arguments/returns at compile time,
made a value-returning method that ends without `I'LL BE BACK` throw instead of
silently returning a default, and fixed the CLI: class names come from the source
file's basename, all `.class` files land next to the source, `-run` works from
subdirectories, parse errors print one line (exit 1) instead of a stack trace, and
runtime stack traces blame `<program>.actionc` instead of a hardcoded `Hello.java`.
A later pass (2026-06-13) added backslash escape sequences to string literals
(`\n \t \r \" \\ \0` and `\uXXXX`): the `String` grammar rule now consumes an escape
as a unit (so `\"` no longer terminates the literal) and `StringNode.generate` decodes
the captured raw text, throwing `ParsingException` on an invalid/malformed escape.
**`sbt test` →
247 passing, 0 failing, 38 suites.** `sbt assembly` produces a runnable jar.

The implementation roadmap (`TODO.md`) has been removed now that all tiers are done;
treat the verified test run as ground truth, and this file as the live guide.

## Architecture: the pipeline

Source flows through four stages, all under package `org.arnoldc` (the package name
was never renamed from ArnoldC — keep it):

```
.actionc text
   │
   ▼  ArnoldParser.scala         parboiled PEG grammar; keyword strings are vals
AST (org.arnoldc/ast/*.scala)    55 case-class nodes; each knows how to emit itself
   │
   ▼  node.generate(mv, symbolTable)   walks the tree, calling ASM MethodVisitor
JVM bytecode                     RootNode.generateByteCode -> Map[className -> bytes]
   │
   ▼  ArnoldC.main                writes one .class per entry
prog.class (+ one .class per ActionC class / lambda / async block)
```

Core files (`src/main/scala/org/arnoldc/`):

| File | Role |
|------|------|
| `ArnoldC.scala` | CLI entry. Reads source, drives generation, writes `.class` files, handles `-run`/`-declaim`. |
| `ArnoldParser.scala` | The grammar (~535 lines). Keyword strings as `val`s up top; `def Xxx: Rule…` rules below. `Statement` and `Root` are the top-level rules. |
| `ArnoldGenerator.scala` | Thin glue: parse → `rootNode.generateByteCode(filename)`. |
| `SymbolTable.scala` | Per-method variable slots + per-variable `VariableType` (int/float/string/array/object). |
| `VariableType.scala` | The type tags used during codegen. |
| `Executor.scala` | `-run`: loads and invokes the compiled main class. |
| `Declaimer.scala` | `-declaim`: speaks the program. Uses a **console fallback** (native TTS libs unavailable) — don't reintroduce FreeTTS/JSAPI. |
| `ast/*.scala` | One file per construct (`OopNodes`, `LambdaNodes`, `AsyncNodes`, `MathNodes`, `FileNodes`, `StringFunctionNodes`, …). Each node is a `case class` with a `generate` method. |

Bytecode uses ASM with **`COMPUTE_FRAMES`** — never hand-write stack-map frames or
`visitMaxs` bookkeeping; let ASM compute them. Multi-class constructs (classes,
lambdas-as-static-methods, async-blocks-as-`Runnable`s) are why `generateByteCode`
returns a *map* of classes, not a single byte array.

## The recipe: adding a keyword / language feature

This is the repeated task in this repo. Work **test-first** (TDD), mirroring how every
tier was built:

1. **Write the failing test.** Add a suite (or case) under `src/test/scala/org/arnoldc/`
   extending `ArnoldGeneratorTest`. Build a source string and assert on `getOutput(code)`
   — tests compile bytecode in-memory and capture stdout. See `CommentTest.scala` for
   the pattern.
2. **Add the keyword string** as a `val` near the top of `ArnoldParser.scala`
   (e.g. `val Break = "GET OUT"`). Keywords are exact phrases; mind apostrophes/commas.
3. **Wire the grammar rule.** Add a `def …Statement` (or expression) rule and reference
   it from the `Statement` rule (line ~200) — or from `Operand`/`Expression` for a value
   form. parboiled combinators: `~` sequence, `|` choice, `~>` capture-string,
   `~~>` build-AST-node. Conclude statement rules with `EOL`.
4. **Create the AST node** in `ast/` (or extend an existing grouped file). It's a
   `case class … extends StatementNode` / `ExpressionNode` with
   `def generate(mv: MethodVisitor, symbolTable: SymbolTable)`. Emit instructions via
   `mv.visit*`. `NotEqualNode.scala` is a clean, small reference.
5. **Run `sbt test`** until green. Add the keyword to `README.md`'s tables and, if it
   changes program-author behavior, to Part 2 below.

## Conventions & gotchas (codebase)

- Package stays `org.arnoldc`; assembly `mainClass` is `org.arnoldc.ArnoldC`.
- The fat-jar merge strategy **discards `module-info.class`** (the ASM jars each ship
  one) — see `build.sbt`. Don't remove that or `sbt assembly` breaks.
- Conditions (`if`/`while`/`for`) consume a **single pre-computed operand**, not an
  inline comparison — this is a grammar constraint inherited from ArnoldC, not a bug.
  See Part 2; don't "fix" it without changing the grammar deliberately.
- Fields are **int-only** by design (OOP-lite). Lambdas compile to **static methods**;
  async blocks to synthetic **`Runnable`** classes on real threads with spin-wait await.
- Keep `Test / parallelExecution := false`.

## Deliberately deferred (don't assume these exist)

Trig (`IT'S ALL IN THE REFLEXES` — ambiguous one-keyword→sin/cos/tan mapping), an
explicit boolean type, null/`@THERE IS NO SPOON`, file modes/handles, and
`setTimeout`/`elapsed`. The author-facing subset is restated in Part 2 below.

Float arithmetic and `floor`/`ceil`/`round` are **no longer deferred** — see the Tier 1
numerics work. `floor`/`ceil`/`round` take a float and return an int (they double as
float→int truncation); `SPELL IT OUT` stringifies an int or float; `DO THE MATH` parses
a string to an int. Mixed int/float arithmetic promotes the int side via `I2F`
(`TypeInference.scala`).

String `replace` (`GET A NEW ONE`), `startsWith` (`FIRST BLOOD`), `endsWith` (`LAST MAN
STANDING`), `charAt` (`SHOW ME THE ONE AT`, returns a 1-char string), and `reverse`
(`PUT IT IN REVERSE`) are **no longer deferred** — Tier 2.

Typed arrays are **no longer deferred** — Tier 3. Float arrays declare with `LOCK AND
LOAD … WITH <n> UGLY MOTHERFUCKERS` (full read/write, int→float coercion on write);
string arrays are produced by `split` = `DIVIDE AND CONQUER <name> <str> <delim>` (literal
delimiter via `Pattern.quote`). `GET IN LINE … AT`, `HOW MANY OF THEM`, and (for floats)
element writes dispatch on the array's element type via `ArrayVariableType`
(`VariableType.scala`). Manual *string*-array element writes are deferred — `split` is the
producer; reading elements and length are supported.

---

# Part 2 — Writing ActionC programs

Portable reference for authoring `.actionc` source. Full keyword tables live in
[`README.md`](README.md); [`ACTIONC_SPEC.md`](ACTIONC_SPEC.md) has the grammar. This
section is the working subset plus the gotchas that separate programs that *run* from
spec examples that don't parse.

## Skeleton, compile, run

```actionc
IT'S SHOWTIME
    TALK TO THE HAND "HELLO BOYS I'M BACK"
YOU HAVE BEEN TERMINATED
```

```bash
java -jar ActionC.jar -run hello.actionc      # compile + run in one step
# or: java -jar ActionC.jar hello.actionc  &&  java hello
```

Both `.actionc` and `.arnoldc` extensions are accepted; all ArnoldC programs run
unchanged.

## Essential idioms

**Declare & init an int** (declaration and initial value are two lines):

```actionc
HEY CHRISTMAS TREE x
YOU SET US UP 21
```

**Reassign** a variable — an assignment *block*, not a single statement. Open with
`GET TO THE CHOPPER`, seed with `HERE IS MY INVITATION`, apply infix operators, close
with `ENOUGH TALK`:

```actionc
GET TO THE CHOPPER x
HERE IS MY INVITATION x
YOU'RE FIRED 2            I'M BATMAN  x = x * 2
ENOUGH TALK
```

`GET UP` = +, `GET DOWN` = −, `YOU'RE FIRED` = ×, `HE HAD TO SPLIT` = ÷,
`I LET HIM GO` = %.

**Print:** `TALK TO THE HAND <int-var | "string literal">`.

**Comments:** `I'M BATMAN …` (rest of line); `GATHER ROUND` / `DISMISSED` (block).

## Conditions need a PRE-COMPUTED boolean — the #1 gotcha

`if`/`while`/`for` take **one already-computed operand**, never an inline comparison.
Compute the comparison into a variable first, then branch on it. The spec's
inline-comparison examples (the FizzBuzz one) **do not parse as written.**

```actionc
I'M BATMAN  WRONG — does not compile:
I'M BATMAN  BECAUSE I'M GOING TO SAY PLEASE x YOU ARE NOT YOU YOU ARE ME 5 ...

I'M BATMAN  RIGHT — compute the boolean, then branch on it:
HEY CHRISTMAS TREE isFive
YOU SET US UP x
YOU ARE NOT YOU YOU ARE ME 5      I'M BATMAN  isFive = (x == 5)
BECAUSE I'M GOING TO SAY PLEASE isFive
    TALK TO THE HAND "five!"
YOU HAVE NO RESPECT FOR LOGIC
```

(`BECAUSE I'M GOING TO SAY PLEASE` = if, `BULLSHIT` = else,
`YOU HAVE NO RESPECT FOR LOGIC` = endif.)

Booleans are ints: `@NO PROBLEMO` = true, `@I LIED` = false.

## What's available

Loops (`LET'S ROCK … FROM … TO … / GAME OVER MAN GAME OVER`, `STICK AROUND … CHILL`,
`GET OUT` break, `KEEP MOVING` continue), switch (`CHOOSE YOUR DESTINY … FINISH HIM`,
no fall-through), strings (declare/concat/length/upper/lower/trim/substring/contains/
indexOf/replace/startsWith/endsWith/charAt/reverse, plus backslash escapes), int/float/string arrays (string
arrays via `split`), try/catch/finally +
throw + assert, the math/string/time/file
stdlib, classes with constructors and inheritance, instance methods + `this`
(`LOOK AT ME`), lambdas + function refs, and async (`COVER ME … MISSION COMPLETE`,
`HOLD THE LINE` await). See `README.md` for the full examples per feature.

## Author-facing gotchas

- **Conditions take a pre-computed boolean** (above) — the big one.
- **Object fields are int-only.** No string/float/object fields.
- **Methods are int-only too.** Arguments and `I'LL BE BACK` values must be integers —
  passing or returning a string/float is a compile error. A value-returning method
  that reaches its end without `I'LL BE BACK` throws at runtime.
- **No closure capture.** Lambdas and async blocks run in their own JVM frame:
  referencing a variable declared outside them is a compile error
  (`VARIABLE: x NOT DECLARED!`) — pass values through parameters or declare inside.
- **Lambdas** are top-level (declared like functions, not nested in `IT'S SHOWTIME`),
  and their body uses **infix** arithmetic: `CALL ME SNAKE double (x) => x YOU'RE FIRED 2`.
- **`GET OUT` in a switch exits the switch** (cases never fall through anyway, so it's
  only needed for an early exit); `KEEP MOVING` inside a switch continues the
  enclosing loop. `finally` runs even when the catch body throws, and nested
  try blocks dispatch to the innermost handler.
- **Floats** support full arithmetic now (`GET UP`/`GET DOWN`/`YOU'RE FIRED`/`HE HAD TO
  SPLIT`/`I LET HIM GO` in an assignment block). Mixing an int into a float expression
  promotes the int automatically; a pure-int expression assigned into a float variable
  is coerced. `HIT THE FLOOR` / `THROUGH THE ROOF` / `ROUND THEM UP` take a float and
  return an int (also the way to truncate float→int).
- **Float division gotcha:** an operand is promoted to float only when *some leaf in
  that expression* is already a float — promotion happens per-expression, not at the
  destination. So `7 HE HAD TO SPLIT 2` is **integer** division (`= 3`) even when stored
  into a float variable; it only coerces to `3.0` at the store, never `3.5`. Write a
  float literal (`7.0` or `2.0`) to force float division. This matches C/Java semantics.
- **Comparisons are type-aware.** Strings compare by *content* with
  `YOU ARE NOT YOU YOU ARE ME` (==) and `IT'S JUST BEEN REVOKED` (!=); the four
  ordering operators reject strings at compile time, as does comparing a string with
  a number. Floats work with all six operators (an int side is promoted; NaN compares
  false, like Java).
- **Async blocks are exception-safe and run on daemon threads.** If the body throws,
  the thread reports the exception, `HOLD THE LINE` still completes, and `.result`
  stays 0. Because the threads are daemons, the JVM exits when main ends — await a
  block if you need it to finish.
- **Convert numbers and strings:** `SPELL IT OUT <n>` turns an int or float into a
  string (for printing/concatenation); `DO THE MATH <str>` parses a string to an int.
- **String literals take backslash escapes** (C/Java style): `\n \t \r \" \\ \0` and
  `\uXXXX` (four hex digits). Use `\"` to embed a quote and `\\` for a literal
  backslash (so a Windows path is `"C:\\temp"`). An unrecognized escape (`\q`) or a
  malformed `\uXXXX` is a compile-time error.
- **Arrays come in three element types.** Int (`I AIN'T GOT TIME TO BLEED`) and float
  (`LOCK AND LOAD`) arrays declare `WITH <n> UGLY MOTHERFUCKERS` and support read/write/
  length. String arrays are produced by `split` (`DIVIDE AND CONQUER <name> <str>
  <delim>`, literal delimiter) — read elements with `GET IN LINE … AT` and length with
  `HOW MANY OF THEM`; writing individual string elements isn't supported yet.
- **Not implemented:** explicit boolean type, null, trig, file modes/handles. If you
  reach for one and it won't parse, that's why.
