# ArnoldC Language Evaluation

## What is ArnoldC?

ArnoldC is an esoteric programming language where all keywords are Arnold Schwarzenegger movie quotes. It compiles to Java bytecode and runs on the JVM. While humorous, it's actually Turing-complete.

## What ArnoldC Supports

| Feature | Syntax | Notes |
|---------|--------|-------|
| Variables | `HEY CHRISTMAS TREE var` | Integer only |
| Assignment | `GET TO THE CHOPPER var` | Multi-step syntax |
| Arithmetic | `GET UP`, `GET DOWN`, `YOU'RE FIRED`, `HE HAD TO SPLIT`, `I LET HIM GO` | +, -, *, /, % |
| Comparison | `YOU ARE NOT YOU YOU ARE ME`, `LET OFF SOME STEAM BENNET` | == and > only |
| Logic | `KNOCK KNOCK`, `CONSIDER THAT A DIVORCE` | AND, OR |
| If/Else | `BECAUSE I'M GOING TO SAY PLEASE` | With else support |
| While | `STICK AROUND` | Basic loop |
| Functions | `LISTEN TO ME VERY CAREFULLY` | With params/returns |
| Print | `TALK TO THE HAND` | Strings or integers |
| Input | `I WANT TO ASK YOU A BUNCH OF QUESTIONS...` | Integer only |

---

## Shortcomings and Limitations

### 1. Data Types - Severely Limited

- **No arrays or lists** - Cannot store collections of values
- **No floating-point numbers** - Only 32-bit integers
- **No string variables** - Strings can only be printed, not stored or manipulated
- **No custom types/structs** - No way to define composite data
- **No null/option types** - Cannot represent absence of value
- **Booleans are just integers** - 1=true, 0=false (no real boolean type)

### 2. Missing Operators

- **No less-than operator** (`<`)
- **No less-than-or-equal** (`<=`)
- **No greater-than-or-equal** (`>=`)
- **No not-equal operator** (`!=`)
- **No logical NOT operator** (`!`)
- **No bitwise operators** (AND, OR, XOR, shifts)
- **No increment/decrement** (`++`, `--`)
- **No compound assignment** (`+=`, `-=`, etc.)
- **No ternary operator** (`? :`)

### 3. Missing Control Structures

- **No for loops** - Must use while loops for all iteration
- **No do-while loops**
- **No switch/case statements**
- **No break statement** - Cannot exit loops early
- **No continue statement** - Cannot skip iterations
- **No goto** - Limited control flow

### 4. Function Limitations

- **No function overloading**
- **No default parameters**
- **No variable-length arguments** (varargs)
- **No lambdas/anonymous functions**
- **No higher-order functions** - Cannot pass functions as arguments
- **No closures**
- **No nested functions**

### 5. No Object-Oriented Features

- **No classes**
- **No objects**
- **No inheritance**
- **No polymorphism**
- **No encapsulation** (public/private)
- **No interfaces**

### 6. I/O Limitations

- **No file I/O** - Cannot read/write files
- **No string input** - Can only read integers
- **No formatted output** - No printf-style formatting
- **No character-level I/O**

### 7. No Standard Library

- **No math functions** (sqrt, sin, cos, abs, pow, etc.)
- **No string functions** (length, substring, concat, etc.)
- **No random number generation**
- **No date/time functions**
- **No sorting/searching algorithms**

### 8. No Error Handling

- **No try-catch-finally**
- **No custom exceptions**
- **No assertions**
- **No error recovery mechanisms**

### 9. No Advanced Features

- **No modules/namespaces**
- **No generics/templates**
- **No macros**
- **No reflection**
- **No async/concurrency primitives**
- **No pattern matching**

### 10. Syntax/Usability Issues

- **No comments** - Cannot document code within the language
- **No operator precedence** - Left-to-right evaluation only
- **No parentheses for grouping** - Cannot control evaluation order
- **Extremely verbose** - Simple operations require many lines
- **Hard-coded limits** - Max stack/locals set to 100 in compiler

---

## Summary

ArnoldC is a fun novelty language for learning and entertainment, but has severe practical limitations:

| Category | Status |
|----------|--------|
| Arrays/Collections | Not supported |
| Floating-point math | Not supported |
| String manipulation | Not supported |
| File I/O | Not supported |
| OOP | Not supported |
| Error handling | Not supported |
| Standard library | None |
| Comparison operators | Only `==` and `>` |
| Loop control | No break/continue |
| Comments | Not supported |

The language is essentially a minimal procedural language with only integers, basic arithmetic, if/else, while loops, and functions - wrapped in Arnold Schwarzenegger quotes.

---

## ActionC Improvements

**ActionC addresses** these limitations — and as of 2026-06-09 the improvements
below are **implemented and tested** (175 passing tests; verified end-to-end through
the compiled `ActionC.jar`).

| Category | ArnoldC | ActionC | Built? |
|----------|---------|---------|--------|
| Arrays/Collections | Not supported | Integer arrays | ✅ |
| Floating-point math | Not supported | Float type (declare/init/print) | ✅ |
| String manipulation | Not supported | String type + functions | ✅ |
| File I/O | Not supported | Read/write/delete/exists | ✅ |
| OOP | Not supported | Classes, fields, constructors, inheritance, methods | ✅ |
| Error handling | Not supported | Try/catch/finally/throw + assert | ✅ |
| Standard library | None | Math, string, time functions | ✅ |
| Comparison operators | Only `==` and `>` | All operators (!=, <, >=, <=) | ✅ |
| Loop control | No break/continue | Break and continue | ✅ |
| Comments | Not supported | Single-line and block comments | ✅ |
| For loops | Not supported | For loops with range | ✅ |
| Switch/case | Not supported | Switch statements | ✅ |
| Bitwise operators | Not supported | AND, OR, XOR, shifts | ✅ |
| Lambdas / async | Not supported | Lambdas, function refs, async blocks | ✅ |

A few spec items are deliberately deferred or adjusted (trig, explicit boolean/null
types, inline comparisons in conditions, float arithmetic) — see the "Deliberately
deferred" notes in [CLAUDE.md](CLAUDE.md).
