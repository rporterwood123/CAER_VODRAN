# ActionC

> *"I ain't got time to bleed."* — Blain, Predator (1987)

**The Action Movie Programming Language**

ActionC is an esoteric programming language based on iconic one-liners from action movies. It extends the original [ArnoldC](https://github.com/lhartikk/ArnoldC) with quotes from Die Hard, Aliens, Lethal Weapon, Robocop, The Matrix, and many more classics.

> ✅ **Status (2026-06-09): implemented and tested.** All tiers (comments, full
> comparison/bitwise/logical operators, for/break/continue/switch, strings, floats,
> arrays, error handling, the math/string/time/file stdlib, OOP with inheritance and
> instance methods, lambdas, and async) compile to JVM bytecode and run. **175 tests
> pass** and `sbt assembly` builds a runnable `ActionC.jar`. A few spec items are
> deliberately deferred or adjusted — see the "Deliberately deferred" notes in
> [CLAUDE.md](CLAUDE.md) (e.g. conditions take a pre-computed boolean, not an inline
> comparison; lambda bodies use infix arithmetic; fields are int).

## Build & run

```bash
sbt assembly                                   # build target/scala-2.12/ActionC.jar
java -jar target/scala-2.12/ActionC.jar -run Demo.actionc
```

---

## Motivation

While ArnoldC brilliantly explored the semantics of Arnold Schwarzenegger's utterances, the true depth of action cinema remained untapped. ActionC expands the vocabulary to the entire action movie universe—because sometimes you need more than one hero to save the day.

**ActionC is a superset of ArnoldC** — all existing ArnoldC programs work unchanged.

---

## Hello World

```actionc
IT'S SHOWTIME
    TALK TO THE HAND "HELLO BOYS I'M BACK"
YOU HAVE BEEN TERMINATED
```

---

## Quick Start

### Prerequisites
- Java 8 or higher
- [sbt](https://www.scala-sbt.org/) (Scala Build Tool)

### Build from Source

```bash
# Clone the repository
git clone https://github.com/rporterwood123/ActionC.git
cd ActionC

# Build the JAR
sbt assembly

# The JAR is created at target/scala-2.12/ActionC.jar
```

### Write Your First Program

Create a file called `hello.actionc`:

```actionc
IT'S SHOWTIME
    TALK TO THE HAND "HELLO BOYS I'M BACK"
YOU HAVE BEEN TERMINATED
```

### Compile and Run

```bash
# Compile to bytecode
java -jar target/scala-2.12/ActionC.jar hello.actionc

# Run the compiled program
java hello
```

Output:
```
HELLO BOYS I'M BACK
```

### Run Tests

```bash
sbt test
```

---

## What's New in ActionC

### Control Flow
| Feature | Keyword | Movie |
|---------|---------|-------|
| **For Loops** | `LET'S ROCK` / `GAME OVER MAN GAME OVER` | Aliens |
| **Break** | `GET OUT` | Various |
| **Continue** | `KEEP MOVING` | Various |
| **Switch/Case** | `CHOOSE YOUR DESTINY` / `FINISH HIM` | Mortal Kombat |

`GET OUT` breaks out of the innermost loop *or* switch; `KEEP MOVING` always continues
the innermost loop, even from inside a switch.

### Data Types
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Strings** | `I HAVE COME HERE TO CHEW BUBBLEGUM` | They Live |
| **String Concat** | `AND KICK ASS` (chained) | They Live |
| **Arrays** | `I AIN'T GOT TIME TO BLEED` | Predator |
| **Floats** | `NOW I HAVE A MACHINE GUN` / `HO HO HO` | Die Hard |

### Operators
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Not Equal (!=)** | `IT'S JUST BEEN REVOKED` | Lethal Weapon 2 |
| **Less Than (<)** | `YOU'RE THE DISEASE AND I'M THE CURE` | Cobra |
| **Greater or Equal (>=)** | `I'M GETTING TOO OLD FOR THIS` | Lethal Weapon |
| **Less or Equal (<=)** | `BENEATH YOU` | Blade |
| **Logical NOT** | `NEGATIVE` | Predator 2 |
| **Bitwise AND** | `WINNERS GO HOME AND DATE THE PROM QUEEN` | The Rock |
| **Bitwise OR** | `DEAD OR ALIVE YOU'RE COMING WITH ME` | Robocop |
| **Bitwise XOR** | `FRIEND OR FOE` | Various |
| **Left Shift** | `MOVE IT` | Various |
| **Right Shift** | `FALL BACK` | Various |

Comparisons are type-aware: strings compare by content with `YOU ARE NOT YOU YOU ARE
ME` (==) and `IT'S JUST BEEN REVOKED` (!=) — ordering operators reject strings at
compile time — and floats work with all six operators (an int side is promoted
automatically).

### Comments
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Single-line** | `I'M BATMAN` | Batman (1989) |
| **Block comments** | `GATHER ROUND` / `DISMISSED` | Various |

### Math Functions
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Random** | `GO AHEAD MAKE MY DAY` | Dirty Harry |
| **Absolute Value** | `NO MORE HALF MEASURES` | Breaking Bad |
| **Square Root** | `GET TO THE ROOT OF` | Pun |
| **Floor** (float→int) | `HIT THE FLOOR` | Various |
| **Ceiling** (float→int) | `THROUGH THE ROOF` | Various |
| **Round** (float→int) | `ROUND THEM UP` | Various |
| **Sin/Cos/Tan** | `IT'S ALL IN THE REFLEXES` | Big Trouble in Little China |

Floats support full arithmetic (`GET UP`/`GET DOWN`/`YOU'RE FIRED`/`HE HAD TO SPLIT`/`I
LET HIM GO`) inside an assignment block; mixing an int into a float expression promotes
it automatically.

### Conversions
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Number → String** | `SPELL IT OUT` | Various |
| **String → Int** | `DO THE MATH` | Various |

### String Functions
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Length** | `HOW LONG IS THIS THING` | Various |
| **Substring** | `GIVE ME A PIECE OF ... FROM ... TO` | Various |
| **Uppercase** | `SAY IT LOUDER` | Various |
| **Lowercase** | `KEEP YOUR VOICE DOWN` | Various |
| **Trim** | `CUT THE FAT FROM` | Various |
| **Contains** | `YOU TALKING TO ME ABOUT` | Taxi Driver |
| **Replace** | `GET A NEW ONE` | Various |
| **Starts With** | `FIRST BLOOD` | Rambo |
| **Ends With** | `LAST MAN STANDING` | Various |
| **Char At** (→ 1-char string) | `SHOW ME THE ONE AT` | Various |
| **Reverse** | `PUT IT IN REVERSE` | Various |

### Time Functions
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Current Time** | `WHAT TIME IS IT` | Various |
| **Sleep** | `CHILL OUT FOR` | Mr. Freeze - Batman |

### File I/O
| Feature | Keyword | Movie |
|---------|---------|-------|
| **File Exists** | `HONEY I'M HOME` | The Shining |
| **Read File** | `WHAT'S IN THE BOX` | Se7en |
| **Write File** | `WRITE THAT DOWN ... TO` | Various |
| **Delete File** | `SEAL THE EXITS` | Various |

### Error Handling
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Try** | `LET'S SEE WHAT YOU'VE GOT` | Various |
| **Throw** | `WELCOME TO THE PARTY PAL` | Die Hard |
| **Catch** | `GOTCHA` | Various |
| **Finally** | `CLEAN UP ON AISLE FIVE` | Various |
| **Assert** | `I AM THE LAW` | Judge Dredd |

### Utility
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Increment (++)** | `ONE MORE TIME` | Various |
| **Decrement (--)** | `COUNTDOWN` | Various |

### Object-Oriented Programming (OOP Lite)
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Class Definition** | `MY NAME IS MAXIMUS` / `STRENGTH AND HONOR` | Gladiator |
| **Public Field** | `OPEN TO THE PUBLIC` | Various |
| **Private Field** | `THAT'S CLASSIFIED` | Various |
| **Constructor** | `IT'S ALIVE` / `BIRTH COMPLETE` | Frankenstein |
| **Create Instance** | `WELCOME TO EARTH ... AS` | Independence Day |
| **Field Access** | `object.field` | Standard notation |

### OOP Advanced
| Feature | Keyword | Movie |
|---------|---------|-------|
| **This Reference** | `LOOK AT ME` | Predator |
| **Inheritance** | `LIKE FATHER LIKE SON` | Various |
| **Instance Method Start** | `COMMANDER IN CHIEF` | Various |
| **Instance Method End** | `DISMISSED SOLDIER` | Various |
| **Method Call** | `DO IT NOW object.method args` | Total Recall |

### Lambda Functions
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Lambda Definition** | `CALL ME SNAKE (params) => expr` | Escape from New York |
| **Function Reference** | `THE NAME'S PLISSKEN` | Escape from New York |

### Async/Concurrency
| Feature | Keyword | Movie |
|---------|---------|-------|
| **Async Block** | `COVER ME` / `MISSION COMPLETE` | Various |
| **Await Result** | `HOLD THE LINE` | 300 |
| **Future Access** | `task.result` | Standard notation |

See [ACTIONC_SPEC.md](ACTIONC_SPEC.md) for the complete language specification.

---

## Examples

### For Loop with Break

```actionc
IT'S SHOWTIME
    LET'S ROCK i FROM 1 TO 10
        BECAUSE I'M GOING TO SAY PLEASE i YOU ARE NOT YOU YOU ARE ME 5
            TALK TO THE HAND "Found 5!"
            GET OUT
        YOU HAVE NO RESPECT FOR LOGIC
        TALK TO THE HAND i
    GAME OVER MAN GAME OVER
YOU HAVE BEEN TERMINATED
```

### String Manipulation

```actionc
IT'S SHOWTIME
    I HAVE COME HERE TO CHEW BUBBLEGUM greeting
    AND KICK ASS "  hello world  "

    I'M BATMAN Trim and uppercase
    I HAVE COME HERE TO CHEW BUBBLEGUM result
    AND KICK ASS SAY IT LOUDER CUT THE FAT FROM greeting

    TALK TO THE HAND result
    I'M BATMAN Output: HELLO WORLD

    I'M BATMAN Get length
    HEY CHRISTMAS TREE len
    YOU SET US UP HOW LONG IS THIS THING result
    TALK TO THE HAND len
YOU HAVE BEEN TERMINATED
```

### Arrays

```actionc
IT'S SHOWTIME
    I'M BATMAN Declare array of 5 integers
    I AIN'T GOT TIME TO BLEED scores WITH 5 UGLY MOTHERFUCKERS

    I'M BATMAN Set elements
    PUT 100 IN LINE scores AT 0
    PUT 95 IN LINE scores AT 1
    PUT 87 IN LINE scores AT 2

    I'M BATMAN Print array length
    TALK TO THE HAND HOW MANY OF THEM scores

    I'M BATMAN Access element
    TALK TO THE HAND GET IN LINE scores AT 0
YOU HAVE BEEN TERMINATED
```

**Float arrays** declare the same way with `LOCK AND LOAD` and support read/write/length
(an int written into a float array is promoted automatically):

```actionc
LOCK AND LOAD temps WITH 3 UGLY MOTHERFUCKERS
GET IN LINE temps AT 0
HERE IS MY INVITATION 98.6
ENOUGH TALK
TALK TO THE HAND GET IN LINE temps AT 0      I'M BATMAN 98.6
```

**String arrays** are produced by `split` (`DIVIDE AND CONQUER <name> <str> <delim>`, the
delimiter is literal). Read elements with `GET IN LINE … AT` and length with `HOW MANY OF
THEM`:

```actionc
I HAVE COME HERE TO CHEW BUBBLEGUM csv
AND KICK ASS "alpha,beta,gamma"
DIVIDE AND CONQUER parts csv ","
TALK TO THE HAND HOW MANY OF THEM parts       I'M BATMAN 3
TALK TO THE HAND GET IN LINE parts AT 1       I'M BATMAN beta
```

### Math Functions

```actionc
IT'S SHOWTIME
    I'M BATMAN Absolute value
    HEY CHRISTMAS TREE absVal
    YOU SET US UP NO MORE HALF MEASURES -42
    TALK TO THE HAND absVal
    I'M BATMAN Output: 42

    I'M BATMAN Square root
    HEY CHRISTMAS TREE sqrtVal
    YOU SET US UP GET TO THE ROOT OF 16
    TALK TO THE HAND sqrtVal
    I'M BATMAN Output: 4

    I'M BATMAN Trig (returns value * 1000 for precision)
    HEY CHRISTMAS TREE cosVal
    YOU SET US UP IT'S ALL IN THE REFLEXES COS 0
    TALK TO THE HAND cosVal
    I'M BATMAN Output: 1000 (cos(0) = 1.0)
YOU HAVE BEEN TERMINATED
```

### File I/O

```actionc
IT'S SHOWTIME
    I'M BATMAN Write to file
    WRITE THAT DOWN "Hello from ActionC!" TO "test.txt"

    I'M BATMAN Check if file exists
    HEY CHRISTMAS TREE exists
    YOU SET US UP HONEY I'M HOME "test.txt"
    TALK TO THE HAND exists
    I'M BATMAN Output: 1

    I'M BATMAN Read file contents
    I HAVE COME HERE TO CHEW BUBBLEGUM content
    AND KICK ASS WHAT'S IN THE BOX "test.txt"
    TALK TO THE HAND content

    I'M BATMAN Delete file
    SEAL THE EXITS "test.txt"
YOU HAVE BEEN TERMINATED
```

### Error Handling

```actionc
IT'S SHOWTIME
    LET'S SEE WHAT YOU'VE GOT
        TALK TO THE HAND "In try block"
        WELCOME TO THE PARTY PAL "Oops!"
        TALK TO THE HAND "This won't print"
    GOTCHA
        TALK TO THE HAND "Caught exception!"
    THAT'S A WRAP
YOU HAVE BEEN TERMINATED
```

### Block Comments

```actionc
IT'S SHOWTIME
    GATHER ROUND
        This is a multi-line comment.
        It can span as many lines as needed.
        Perfect for documentation!
    DISMISSED

    TALK TO THE HAND "Hello World"
YOU HAVE BEEN TERMINATED
```

### OOP - Classes and Objects

```actionc
MY NAME IS MAXIMUS Player
    OPEN TO THE PUBLIC health
    OPEN TO THE PUBLIC score

    IT'S ALIVE
        GET TO THE CHOPPER health
        HERE IS MY INVITATION 100
        ENOUGH TALK
        GET TO THE CHOPPER score
        HERE IS MY INVITATION 0
        ENOUGH TALK
    BIRTH COMPLETE
STRENGTH AND HONOR

IT'S SHOWTIME
    I'M BATMAN Create two player instances
    WELCOME TO EARTH p1 AS Player
    WELCOME TO EARTH p2 AS Player

    I'M BATMAN Modify p1's score
    GET TO THE CHOPPER p1.score
    HERE IS MY INVITATION 500
    ENOUGH TALK

    I'M BATMAN Print both players' scores
    TALK TO THE HAND p1.score
    TALK TO THE HAND p2.score
YOU HAVE BEEN TERMINATED
```

Output:
```
500
0
```

### Inheritance and Instance Methods

```actionc
MY NAME IS MAXIMUS Vehicle
    OPEN TO THE PUBLIC speed
    IT'S ALIVE
        GET TO THE CHOPPER speed
        HERE IS MY INVITATION 0
        ENOUGH TALK
    BIRTH COMPLETE
STRENGTH AND HONOR

MY NAME IS MAXIMUS Car LIKE FATHER LIKE SON Vehicle
    OPEN TO THE PUBLIC gear

    COMMANDER IN CHIEF accelerate
    I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE amount
    GIVE THESE PEOPLE AIR
        GET TO THE CHOPPER LOOK AT ME.speed
        HERE IS MY INVITATION LOOK AT ME.speed
        GET UP amount
        ENOUGH TALK
        I'LL BE BACK LOOK AT ME.speed
    DISMISSED SOLDIER
STRENGTH AND HONOR

IT'S SHOWTIME
    WELCOME TO EARTH myCar AS Car

    I'M BATMAN Call instance method
    HEY CHRISTMAS TREE newSpeed
    YOU SET US UP 0
    GET YOUR ASS TO MARS newSpeed
    DO IT NOW myCar.accelerate 50

    TALK TO THE HAND newSpeed
YOU HAVE BEEN TERMINATED
```

Output:
```
50
```

### Lambda Functions

```actionc
IT'S SHOWTIME
    I'M BATMAN Define lambdas
    CALL ME SNAKE double (x) => YOU'RE FIRED x 2
    CALL ME SNAKE add (x y) => GET UP x y

    I'M BATMAN Call lambda directly
    HEY CHRISTMAS TREE result
    YOU SET US UP 0
    GET YOUR ASS TO MARS result
    DO IT NOW double 21
    TALK TO THE HAND result

    I'M BATMAN Store function reference in variable
    HEY CHRISTMAS TREE myFunc
    YOU SET US UP THE NAME'S PLISSKEN add

    I'M BATMAN Call via function reference
    HEY CHRISTMAS TREE sum
    YOU SET US UP 0
    GET YOUR ASS TO MARS sum
    DO IT NOW myFunc 10 20
    TALK TO THE HAND sum
YOU HAVE BEEN TERMINATED
```

Output:
```
42
30
```

### Async/Concurrency

```actionc
IT'S SHOWTIME
    I'M BATMAN Start an async task
    COVER ME calculation
        HEY CHRISTMAS TREE x
        YOU SET US UP 21
        HEY CHRISTMAS TREE result
        YOU SET US UP 0
        GET TO THE CHOPPER result
        HERE IS MY INVITATION x
        YOU'RE FIRED 2
        ENOUGH TALK
        I'LL BE BACK result
    MISSION COMPLETE

    TALK TO THE HAND "Task started"

    I'M BATMAN Wait for the task to complete
    HOLD THE LINE calculation

    I'M BATMAN Get the result
    HEY CHRISTMAS TREE answer
    YOU SET US UP calculation.result
    TALK TO THE HAND answer
YOU HAVE BEEN TERMINATED
```

Async blocks run on daemon threads, so the JVM exits when main ends — await a block
with `HOLD THE LINE` if you need it to finish. If the body throws, the exception is
reported on stderr, the await still completes, and `.result` stays 0.

Output:
```
Task started
42
```

---

## Keywords by Movie

### Terminator / Terminator 2 (Arnold Schwarzenegger)

| Keyword | Purpose |
|---------|---------|
| `@I LIED` | False |
| `@NO PROBLEMO` | True |
| `I'LL BE BACK` | Return |
| `HASTA LA VISTA, BABY` | End Method |
| `I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE` | Method Arguments |
| `GET TO THE CHOPPER` | Begin Assignment |
| `YOU SET US UP` | Set Initial Value |

### Die Hard (John McClane)

| Keyword | Purpose |
|---------|---------|
| `NOW I HAVE A MACHINE GUN` | Declare Float |
| `HO HO HO` | Initialize Float |
| `WELCOME TO THE PARTY PAL` | Throw Exception |

### Aliens (Hudson, Hicks, Ripley)

| Keyword | Purpose |
|---------|---------|
| `LET'S ROCK` | For Loop Start |
| `GAME OVER MAN GAME OVER` | For Loop End |

### Predator

| Keyword | Purpose |
|---------|---------|
| `STICK AROUND` | While Loop |
| `I AIN'T GOT TIME TO BLEED` | Declare Array |
| `LOCK AND LOAD` | Declare Float Array |
| `DIVIDE AND CONQUER` | Split String → Array |
| `NEGATIVE` | Logical NOT |
| `LOOK AT ME` | This Reference |

### Lethal Weapon (Riggs & Murtaugh)

| Keyword | Purpose |
|---------|---------|
| `IT'S JUST BEEN REVOKED` | Not Equal (!=) |
| `I'M GETTING TOO OLD FOR THIS` | Greater Than or Equal (>=) |

### Robocop

| Keyword | Purpose |
|---------|---------|
| `DEAD OR ALIVE YOU'RE COMING WITH ME` | Bitwise OR |

### The Matrix

| Keyword | Purpose |
|---------|---------|
| `@THERE IS NO SPOON` | Null Value |
| `WHAT IF I TOLD YOU` | Switch Case |

### Dirty Harry

| Keyword | Purpose |
|---------|---------|
| `GO AHEAD MAKE MY DAY` | Random() |

### Batman (1989)

| Keyword | Purpose |
|---------|---------|
| `I'M BATMAN` | Single-line Comment |

### They Live

| Keyword | Purpose |
|---------|---------|
| `I HAVE COME HERE TO CHEW BUBBLEGUM` | Declare String |
| `AND KICK ASS` | Initialize/Concat String |
| `AND I'M ALL OUT OF BUBBLEGUM` | Empty String |

### Cobra

| Keyword | Purpose |
|---------|---------|
| `YOU'RE THE DISEASE AND I'M THE CURE` | Less Than (<) |

### Judge Dredd

| Keyword | Purpose |
|---------|---------|
| `I AM THE LAW` | Assert |

### The Rock

| Keyword | Purpose |
|---------|---------|
| `WINNERS GO HOME AND DATE THE PROM QUEEN` | Bitwise AND |

### Big Trouble in Little China

| Keyword | Purpose |
|---------|---------|
| `IT'S ALL IN THE REFLEXES` | Trig Functions (SIN/COS/TAN) |

### Mortal Kombat

| Keyword | Purpose |
|---------|---------|
| `CHOOSE YOUR DESTINY` | Switch Start |
| `FINISH HIM` | Switch End |

### Se7en

| Keyword | Purpose |
|---------|---------|
| `WHAT'S IN THE BOX` | Read File |

### The Shining

| Keyword | Purpose |
|---------|---------|
| `HONEY I'M HOME` | File Exists |

### Gladiator (OOP)

| Keyword | Purpose |
|---------|---------|
| `MY NAME IS MAXIMUS` | Class Definition |
| `STRENGTH AND HONOR` | End Class |

### Independence Day

| Keyword | Purpose |
|---------|---------|
| `WELCOME TO EARTH` | Create New Instance |

### Frankenstein Reference

| Keyword | Purpose |
|---------|---------|
| `IT'S ALIVE` | Constructor Start |
| `BIRTH COMPLETE` | Constructor End |

### Escape from New York (Snake Plissken)

| Keyword | Purpose |
|---------|---------|
| `CALL ME SNAKE` | Lambda Definition |
| `THE NAME'S PLISSKEN` | Function Reference |

### 300 (Leonidas)

| Keyword | Purpose |
|---------|---------|
| `HOLD THE LINE` | Await Async Task |

### Various (OOP & Async)

| Keyword | Purpose |
|---------|---------|
| `OPEN TO THE PUBLIC` | Public Field |
| `THAT'S CLASSIFIED` | Private Field |
| `LIKE FATHER LIKE SON` | Inheritance |
| `COMMANDER IN CHIEF` | Instance Method Start |
| `DISMISSED SOLDIER` | Instance Method End |
| `COVER ME` | Async Block Start |
| `MISSION COMPLETE` | Async Block End |

---

## Backwards Compatibility

ActionC is fully backwards compatible with ArnoldC. All existing `.arnoldc` programs will run unchanged. The compiler accepts both `.arnoldc` and `.actionc` file extensions.

---

## Documentation

- **[ACTIONC_SPEC.md](ACTIONC_SPEC.md)** — Complete language specification with grammar
- **[CLAUDE.md](CLAUDE.md)** — Compiler-internals guide + portable guide to writing ActionC programs
- **[EVALUATION.md](EVALUATION.md)** — Analysis of original ArnoldC limitations

---

## Contributing

Found a perfect action movie quote for a missing feature? Open an issue or PR!

Requirements for new quotes:
1. Must be from an action movie (preferably 80s/90s era)
2. Must thematically match the programming concept
3. Bonus points if it's instantly recognizable

---

## Credits

- Original ArnoldC by [Lauri Hartikka](https://github.com/lhartikk)
- ActionC expansion inspired by the greatest era of action cinema

---

*"Hasta la vista, baby."*
