# ActionC Language Specification

> *"I ain't got time to bleed."* — Blain, Predator (1987)

**Version 2.0** | The 80s/90s Action Movie Programming Language

> ✅ **Implementation status (2026-06-09): implemented.** The features in this spec
> compile to JVM bytecode and run (175 passing tests). The "(PLANNED — NOT BUILT)"
> markers below are now historical — treat them as "implemented" except for the
> deliberately deferred/adjusted items listed under "Deliberately deferred" in
> [CLAUDE.md](CLAUDE.md) (notably: conditions take a single pre-computed boolean rather
> than an inline comparison; lambda bodies use infix arithmetic; fields are int;
> trig and explicit boolean/null types are not wired).

---

## Table of Contents

1. [Language Overview](#1-language-overview)
2. [Lexical Structure](#2-lexical-structure)
3. [Data Types](#3-data-types)
4. [Variables](#4-variables)
5. [Operators](#5-operators)
6. [Control Flow](#6-control-flow)
7. [Functions](#7-functions) (includes Lambdas and Async)
8. [Error Handling](#8-error-handling)
9. [Input/Output](#9-inputoutput)
10. [Standard Library](#10-standard-library)
11. [Object-Oriented Features](#11-object-oriented-features) (includes Inheritance and Instance Methods)
12. [Example Programs](#12-example-programs)
13. [Quick Reference](#13-quick-reference)
14. [Movie Sources](#14-movie-sources)

---

## 1. Language Overview

### 1.1 What is ActionC?

ActionC is an esoteric programming language that extends ArnoldC by incorporating iconic one-liners from action movies. While ArnoldC uses only Arnold Schwarzenegger quotes, ActionC expands the vocabulary to include quotes from Die Hard, Lethal Weapon, Robocop, Aliens, The Matrix, and many more classic action films.

ActionC compiles to Java bytecode and runs on the JVM.

### 1.2 Design Philosophy

- **Every keyword is an action movie quote** — Programming should feel like writing a screenplay
- **Quotes match their programming purpose** — `IT'S JUST BEEN REVOKED` for "not equal" (revoking equality!)
- **Backwards compatible with ArnoldC** — All valid ArnoldC programs are valid ActionC programs
- **Practical improvements** — Adds arrays, floats, strings, error handling, and more

### 1.3 Hello World

```actionc
IT'S SHOWTIME
    TALK TO THE HAND "HELLO BOYS I'M BACK"
YOU HAVE BEEN TERMINATED
```

---

## 2. Lexical Structure

### 2.1 Program Structure

Every ActionC program requires a main method:

```
IT'S SHOWTIME
    <statements>
YOU HAVE BEEN TERMINATED
```

### 2.2 Comments

```actionc
I'M BATMAN This is a single-line comment (ignored by compiler)

GATHER ROUND
    This is a block comment.
    Multiple lines are supported.
    Useful for documentation.
DISMISSED
```

| Syntax | Purpose | Source |
|--------|---------|--------|
| `I'M BATMAN` | Single-line comment | Batman (1989) |
| `GATHER ROUND` | Start block comment | Various |
| `DISMISSED` | End block comment | Various military |

### 2.3 Literals

#### Integer Literals
```actionc
123
-456
0
```

#### Float Literals (NEW)
```actionc
3.14159
-0.001
2.0
```

#### String Literals
```actionc
"Hello, World!"
"It's just been revoked."
```

#### Boolean Literals
| Value | Keyword | Source |
|-------|---------|--------|
| true | `@NO PROBLEMO` | Terminator 2 |
| false | `@I LIED` | Terminator 2 |
| null | `@THERE IS NO SPOON` | The Matrix |

---

## 3. Data Types

### 3.1 Integer (Existing from ArnoldC)

32-bit signed integer.

```actionc
HEY CHRISTMAS TREE myNumber
YOU SET US UP 42
```

### 3.2 Float (NEW)

64-bit floating-point number.

```actionc
NOW I HAVE A MACHINE GUN myFloat
HO HO HO 3.14159
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `NOW I HAVE A MACHINE GUN` | Declare float variable | John McClane - Die Hard |
| `HO HO HO` | Initialize float value | Die Hard (written on sweater) |

### 3.3 String (NEW)

Variable-length string that can be stored and manipulated.

```actionc
I HAVE COME HERE TO CHEW BUBBLEGUM myString
AND KICK ASS "Hello World"
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `I HAVE COME HERE TO CHEW BUBBLEGUM` | Declare string variable | Nada - They Live |
| `AND KICK ASS` | Initialize/assign string | Nada - They Live |
| `AND I'M ALL OUT OF BUBBLEGUM` | Empty string literal | They Live (continuation) |

### 3.4 Boolean (Enhanced)

Explicit boolean type (still represented as 1/0 internally).

```actionc
DO YOU FEEL LUCKY isReady
WELL DO YA PUNK @NO PROBLEMO
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `DO YOU FEEL LUCKY` | Declare boolean | Dirty Harry |
| `WELL DO YA PUNK` | Initialize boolean | Dirty Harry |

### 3.5 Array (NEW)

Fixed-size array of any type.

```actionc
I AIN'T GOT TIME TO BLEED numbers WITH 10 UGLY MOTHERF***ERS
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `I AIN'T GOT TIME TO BLEED` | Declare array | Blain - Predator |
| `WITH X UGLY MOTHERF***ERS` | Array size | Predator |
| `GET IN LINE` | Array index access | Various |
| `HOW MANY OF THEM` | Array length | Various |

#### Array Example
```actionc
I'M BATMAN Declare an array of 5 integers
I AIN'T GOT TIME TO BLEED scores WITH 5 UGLY MOTHERF***ERS

I'M BATMAN Set element at index 0
GET IN LINE scores AT 0
HERE IS MY INVITATION 100
ENOUGH TALK

I'M BATMAN Get array length
HEY CHRISTMAS TREE len
YOU SET US UP HOW MANY OF THEM scores
```

### 3.6 Null (NEW)

Represents absence of value.

```actionc
HEY CHRISTMAS TREE nothing
YOU SET US UP @THERE IS NO SPOON
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `@THERE IS NO SPOON` | Null literal | The Matrix |
| `ARE YOU STILL THERE` | Null check (returns boolean) | Various |

---

## 4. Variables

### 4.1 Declaration and Assignment (Existing)

```actionc
HEY CHRISTMAS TREE variableName
YOU SET US UP initialValue
```

### 4.2 Assignment Expression

```actionc
GET TO THE CHOPPER variableName
HERE IS MY INVITATION value
<operations>
ENOUGH TALK
```

### 4.3 Increment/Decrement (NEW)

```actionc
ONE MORE TIME counter          I'M BATMAN counter++
COUNTDOWN counter              I'M BATMAN counter--
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `ONE MORE TIME` | Increment (++) | Various |
| `COUNTDOWN` | Decrement (--) | Various |

---

## 5. Operators

### 5.1 Arithmetic Operators (Existing + Enhanced)

| Operator | Keyword | Example |
|----------|---------|---------|
| + | `GET UP` | `GET UP 5` |
| - | `GET DOWN` | `GET DOWN 3` |
| * | `YOU'RE FIRED` | `YOU'RE FIRED 4` |
| / | `HE HAD TO SPLIT` | `HE HAD TO SPLIT 2` |
| % | `I LET HIM GO` | `I LET HIM GO 7` |

### 5.2 Comparison Operators (Existing + NEW)

| Operator | Keyword | Source |
|----------|---------|--------|
| == | `YOU ARE NOT YOU YOU ARE ME` | Existing ArnoldC |
| != | `IT'S JUST BEEN REVOKED` | Murtaugh - Lethal Weapon 2 |
| > | `LET OFF SOME STEAM BENNET` | Existing ArnoldC |
| < | `YOU'RE THE DISEASE AND I'M THE CURE` | Cobra (1986) |
| >= | `I'M GETTING TOO OLD FOR THIS` | Murtaugh - Lethal Weapon |
| <= | `BENEATH YOU` | Various |

### 5.3 Logical Operators (Existing + NEW)

| Operator | Keyword | Source |
|----------|---------|--------|
| AND | `KNOCK KNOCK` | Existing ArnoldC |
| OR | `CONSIDER THAT A DIVORCE` | Existing ArnoldC |
| NOT | `NEGATIVE` | Various military |

### 5.4 Bitwise Operators (NEW)

| Operator | Keyword | Source |
|----------|---------|--------|
| & (AND) | `WINNERS GO HOME AND DATE THE PROM QUEEN` | The Rock |
| \| (OR) | `DEAD OR ALIVE YOU'RE COMING WITH ME` | Robocop |
| ^ (XOR) | `FRIEND OR FOE` | Various |
| << (Left Shift) | `MOVE IT` | Various |
| >> (Right Shift) | `FALL BACK` | Various military |

### 5.5 Operator Precedence

From highest to lowest:

1. `NEGATIVE` (NOT)
2. `YOU'RE FIRED`, `HE HAD TO SPLIT`, `I LET HIM GO` (*, /, %)
3. `GET UP`, `GET DOWN` (+, -)
4. `MOVE IT`, `FALL BACK` (<<, >>)
5. Comparison operators (<, >, <=, >=)
6. `YOU ARE NOT YOU YOU ARE ME`, `IT'S JUST BEEN REVOKED` (==, !=)
7. `WINNERS GO HOME AND DATE THE PROM QUEEN` (&)
8. `FRIEND OR FOE` (^)
9. `DEAD OR ALIVE YOU'RE COMING WITH ME` (|)
10. `KNOCK KNOCK` (AND)
11. `CONSIDER THAT A DIVORCE` (OR)

---

## 6. Control Flow

### 6.1 If/Else Statement (Existing)

```actionc
BECAUSE I'M GOING TO SAY PLEASE condition
    I'M BATMAN true branch
BULLSHIT
    I'M BATMAN false branch
YOU HAVE NO RESPECT FOR LOGIC
```

### 6.2 While Loop (Existing)

```actionc
STICK AROUND condition
    I'M BATMAN loop body
CHILL
```

### 6.3 For Loop (NEW)

```actionc
LET'S ROCK counter FROM start TO end
    I'M BATMAN loop body
GAME OVER MAN GAME OVER
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `LET'S ROCK` | Start for loop | Hicks - Aliens |
| `FROM X TO Y` | Range specification | — |
| `GAME OVER MAN GAME OVER` | End for loop | Hudson - Aliens |

#### For Loop Example
```actionc
LET'S ROCK i FROM 1 TO 10
    TALK TO THE HAND i
GAME OVER MAN GAME OVER
```

### 6.4 Break and Continue (NEW)

```actionc
STICK AROUND @NO PROBLEMO
    BECAUSE I'M GOING TO SAY PLEASE shouldExit
        GET OUT                     I'M BATMAN break
    YOU HAVE NO RESPECT FOR LOGIC

    BECAUSE I'M GOING TO SAY PLEASE shouldSkip
        KEEP MOVING                 I'M BATMAN continue
    YOU HAVE NO RESPECT FOR LOGIC
CHILL
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `GET OUT` | Break from loop | Various |
| `KEEP MOVING` | Continue to next iteration | Various |

### 6.5 Switch/Case (NEW)

```actionc
CHOOSE YOUR DESTINY value
    WHAT IF I TOLD YOU 1
        TALK TO THE HAND "One"
    WHAT IF I TOLD YOU 2
        TALK TO THE HAND "Two"
    SAME OLD SAME OLD
        TALK TO THE HAND "Default"
FINISH HIM
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `CHOOSE YOUR DESTINY` | Start switch | Mortal Kombat |
| `WHAT IF I TOLD YOU` | Case clause | Morpheus - The Matrix |
| `SAME OLD SAME OLD` | Default clause | Various |
| `FINISH HIM` | End switch | Mortal Kombat |

---

## 7. Functions

### 7.1 Function Declaration (Existing)

```actionc
LISTEN TO ME VERY CAREFULLY functionName
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE arg1
I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE arg2
GIVE THESE PEOPLE AIR
    I'M BATMAN function body (non-void)
    I'LL BE BACK returnValue
HASTA LA VISTA, BABY
```

### 7.2 Function Call (Existing)

```actionc
DO IT NOW functionName arg1 arg2
```

### 7.3 Return Value Assignment (Existing)

```actionc
GET YOUR ASS TO MARS result
DO IT NOW functionName arg1
```

### 7.4 Lambda Functions (PLANNED — NOT BUILT)

Inline function definitions that compile to static methods.

```actionc
I'M BATMAN Define a lambda that doubles a value
CALL ME SNAKE double (x) => YOU'RE FIRED x 2

I'M BATMAN Define a lambda with two parameters
CALL ME SNAKE add (x y) => GET UP x y

I'M BATMAN Call lambda directly
HEY CHRISTMAS TREE result
YOU SET US UP 0
GET YOUR ASS TO MARS result
DO IT NOW double 21
TALK TO THE HAND result     I'M BATMAN prints 42

I'M BATMAN Store function reference in variable
HEY CHRISTMAS TREE myFunc
YOU SET US UP THE NAME'S PLISSKEN add

I'M BATMAN Call via function reference
HEY CHRISTMAS TREE sum
YOU SET US UP 0
GET YOUR ASS TO MARS sum
DO IT NOW myFunc 10 20
TALK TO THE HAND sum        I'M BATMAN prints 30
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `CALL ME SNAKE` | Define lambda | Snake Plissken - Escape from NY |
| `THE NAME'S PLISSKEN` | Reference lambda by name | Snake Plissken - Escape from NY |
| `=>` | Lambda body separator | Standard |

#### Lambda Syntax

```
CALL ME SNAKE name (param1 param2 ...) => expression
```

- Parameters are space-separated in parentheses
- Body is a single expression using arithmetic operators
- Lambdas always return a value (non-void)

### 7.5 Async/Concurrency (PLANNED — NOT BUILT)

Async blocks run in separate threads, allowing concurrent execution.

```actionc
IT'S SHOWTIME
    I'M BATMAN Start async computation
    COVER ME calculation
        HEY CHRISTMAS TREE x
        YOU SET US UP 21
        HEY CHRISTMAS TREE result
        YOU SET US UP 0
        GET TO THE CHOPPER result
        HERE IS MY INVITATION x
        YOU'RE FIRED 2
        ENOUGH TALK
        I'LL BE BACK result      I'M BATMAN Return value to future
    MISSION COMPLETE

    TALK TO THE HAND "Computing..."

    I'M BATMAN Wait for task to complete
    HOLD THE LINE calculation

    I'M BATMAN Access the result
    HEY CHRISTMAS TREE answer
    YOU SET US UP calculation.result
    TALK TO THE HAND answer          I'M BATMAN prints 42
YOU HAVE BEEN TERMINATED
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `COVER ME` | Start async block | Various military |
| `MISSION COMPLETE` | End async block | Various military |
| `HOLD THE LINE` | Await task completion | 300 (Leonidas) |
| `task.result` | Access async result | Standard notation |
| `task.done` | Check if task completed | Standard notation |

#### Async Implementation Details

- Each async block generates a synthetic `Runnable` class
- `I'LL BE BACK value` in async context stores to the `result` field
- `HOLD THE LINE` spin-waits on the `done` field with `Thread.yield()`
- Async classes have `volatile boolean done` and `int result` fields

---

## 8. Error Handling

### 8.1 Try/Catch/Finally (NEW)

```actionc
LET'S SEE WHAT YOU'VE GOT
    I'M BATMAN risky code
    WELCOME TO THE PARTY PAL "Error message"    I'M BATMAN throw
GOTCHA errorVar
    I'M BATMAN handle error
CLEAN UP ON AISLE FIVE
    I'M BATMAN always runs
THAT'S A WRAP
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `LET'S SEE WHAT YOU'VE GOT` | Try block | Various |
| `WELCOME TO THE PARTY PAL` | Throw exception | McClane - Die Hard |
| `GOTCHA` | Catch block | Various |
| `CLEAN UP ON AISLE FIVE` | Finally block | Various |
| `THAT'S A WRAP` | End try/catch | Film terminology |

### 8.2 Assertions (NEW)

```actionc
I AM THE LAW condition "Assertion message"
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `I AM THE LAW` | Assert condition | Judge Dredd |

---

## 9. Input/Output

### 9.1 Console Output (Existing)

```actionc
TALK TO THE HAND "Hello World"
TALK TO THE HAND myVariable
```

### 9.2 Console Input (Existing + Enhanced)

```actionc
I'M BATMAN Read integer
GET YOUR ASS TO MARS intResult
I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY

I'M BATMAN Read string (NEW)
I HAVE COME HERE TO CHEW BUBBLEGUM stringResult
AND KICK ASS WHAT'S YOUR NAME SOLDIER
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `WHAT'S YOUR NAME SOLDIER` | Read string from input | Various military |

### 9.3 File I/O (NEW)

```actionc
I'M BATMAN Open file for reading
OPEN THE DOOR myFile "data.txt" FOR READING

I'M BATMAN Read contents
I HAVE COME HERE TO CHEW BUBBLEGUM contents
AND KICK ASS WHAT'S IN THE BOX myFile

I'M BATMAN Write to file
OPEN THE DOOR outFile "output.txt" FOR WRITING
WRITE THAT DOWN outFile "Data to write"

I'M BATMAN Close file
SEAL THE EXITS myFile
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `OPEN THE DOOR` | Open file | Various |
| `FOR READING` | Read mode | — |
| `FOR WRITING` | Write mode | — |
| `WHAT'S IN THE BOX` | Read file contents | Se7en |
| `WRITE THAT DOWN` | Write to file | Various |
| `SEAL THE EXITS` | Close file | Various |
| `HONEY I'M HOME` | Check if file exists | The Shining |

---

## 10. Standard Library

### 10.1 Math Functions (NEW)

| Function | Keyword | Source |
|----------|---------|--------|
| random() | `GO AHEAD MAKE MY DAY` | Dirty Harry |
| abs() | `NO MORE HALF MEASURES` | Various |
| sqrt() | `GET TO THE ROOT OF` | Pun |
| pow() | `UNLIMITED POWER OF` | Star Wars (crossover!) |
| max() | `MAXIMUM EFFORT OF` | Deadpool |
| min() | `MINIMAL CASUALTIES OF` | Various military |
| floor() | `HIT THE FLOOR` | Various |
| ceil() | `THROUGH THE ROOF` | Various |
| round() | `ROUND IT UP` | Various |
| sin/cos/tan | `IT'S ALL IN THE REFLEXES` | Jack Burton - Big Trouble |

#### Math Examples
```actionc
HEY CHRISTMAS TREE randomNum
YOU SET US UP GO AHEAD MAKE MY DAY         I'M BATMAN random 0-1

HEY CHRISTMAS TREE absValue
YOU SET US UP NO MORE HALF MEASURES -42    I'M BATMAN abs(-42) = 42

HEY CHRISTMAS TREE sqrtValue
YOU SET US UP GET TO THE ROOT OF 16        I'M BATMAN sqrt(16) = 4
```

### 10.2 String Functions (NEW)

| Function | Keyword | Source |
|----------|---------|--------|
| length | `HOW LONG IS THIS THING` | Generic |
| substring | `GIVE ME A PIECE OF` | Various |
| indexOf | `WHERE IS IT IN` | Various |
| toUpper | `SAY IT LOUDER` | Various |
| toLower | `KEEP YOUR VOICE DOWN` | Various |
| trim | `CUT THE FAT FROM` | Various |
| split | `DIVIDE AND CONQUER` | Various |
| replace | `GET A NEW ONE` | Various |
| contains | `YOU TALKING TO ME ABOUT` | Taxi Driver |

#### String Examples
```actionc
I HAVE COME HERE TO CHEW BUBBLEGUM greeting
AND KICK ASS "  Hello World  "

HEY CHRISTMAS TREE length
YOU SET US UP HOW LONG IS THIS THING greeting    I'M BATMAN 14

I HAVE COME HERE TO CHEW BUBBLEGUM trimmed
AND KICK ASS CUT THE FAT FROM greeting           I'M BATMAN "Hello World"

I HAVE COME HERE TO CHEW BUBBLEGUM upper
AND KICK ASS SAY IT LOUDER greeting              I'M BATMAN "  HELLO WORLD  "
```

### 10.3 Time Functions (NEW)

| Function | Keyword | Source |
|----------|---------|--------|
| currentTime | `WHAT TIME IS IT` | Various |
| sleep | `CHILL OUT FOR` | Mr. Freeze - Batman |
| setTimeout | `THE CLOCK IS TICKING` | Speed |
| elapsed | `TIMES UP` | Various |

#### Time Examples
```actionc
HEY CHRISTMAS TREE now
YOU SET US UP WHAT TIME IS IT

CHILL OUT FOR 1000    I'M BATMAN sleep 1000ms

TALK TO THE HAND TIMES UP    I'M BATMAN print elapsed time
```

---

## 11. Object-Oriented Features

ActionC supports full OOP including classes, fields, constructors, inheritance, instance methods, and this reference.

### 11.1 Class Definition (PLANNED — NOT BUILT)

```actionc
MY NAME IS MAXIMUS className
    THAT'S CLASSIFIED privateField          I'M BATMAN private int field
    OPEN TO THE PUBLIC publicField          I'M BATMAN public int field

    IT'S ALIVE
        I'M BATMAN constructor body - initialize fields
        GET TO THE CHOPPER publicField
        HERE IS MY INVITATION 100
        ENOUGH TALK
    BIRTH COMPLETE
STRENGTH AND HONOR
```

| Keyword | Purpose | Source | Status |
|---------|---------|--------|--------|
| `MY NAME IS MAXIMUS` | Class declaration | Gladiator (2000) | ❌ |
| `THAT'S CLASSIFIED` | Private field | Various | ❌ |
| `OPEN TO THE PUBLIC` | Public field | Various | ❌ |
| `IT'S ALIVE` | Constructor start | Frankenstein reference | ❌ |
| `BIRTH COMPLETE` | Constructor end | Frankenstein reference | ❌ |
| `STRENGTH AND HONOR` | End class | Gladiator (2000) | ❌ |

### 11.2 Object Instantiation (PLANNED — NOT BUILT)

```actionc
WELCOME TO EARTH myObject AS MyClass
```

| Keyword | Purpose | Source | Status |
|---------|---------|--------|--------|
| `WELCOME TO EARTH` | Create new instance | Will Smith - Independence Day | ❌ |
| `AS` | Type specifier | Standard | ❌ |

### 11.3 Field Access (PLANNED — NOT BUILT)

```actionc
I'M BATMAN Read field value
TALK TO THE HAND myObject.fieldName

I'M BATMAN Write to field
GET TO THE CHOPPER myObject.fieldName
HERE IS MY INVITATION 42
ENOUGH TALK
```

Field access uses standard dot notation (`object.field`).

### 11.4 Complete OOP Example

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
    I'M BATMAN Create two players
    WELCOME TO EARTH hero AS Player
    WELCOME TO EARTH villain AS Player

    I'M BATMAN Update hero's score
    GET TO THE CHOPPER hero.score
    HERE IS MY INVITATION 500
    ENOUGH TALK

    I'M BATMAN Print values
    TALK TO THE HAND hero.health
    TALK TO THE HAND hero.score
    TALK TO THE HAND villain.score
YOU HAVE BEEN TERMINATED
```

Output:
```
100
500
0
```

### 11.5 Multiple Classes

Multiple class definitions can appear before the main method:

```actionc
MY NAME IS MAXIMUS Player
    OPEN TO THE PUBLIC health
STRENGTH AND HONOR

MY NAME IS MAXIMUS Enemy
    OPEN TO THE PUBLIC damage
STRENGTH AND HONOR

IT'S SHOWTIME
    WELCOME TO EARTH hero AS Player
    WELCOME TO EARTH monster AS Enemy
    TALK TO THE HAND hero.health
    TALK TO THE HAND monster.damage
YOU HAVE BEEN TERMINATED
```

### 11.6 Inheritance (PLANNED — NOT BUILT)

Classes can extend other classes using `LIKE FATHER LIKE SON`.

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

    IT'S ALIVE
        GET TO THE CHOPPER gear
        HERE IS MY INVITATION 1
        ENOUGH TALK
    BIRTH COMPLETE
STRENGTH AND HONOR

IT'S SHOWTIME
    WELCOME TO EARTH myCar AS Car
    TALK TO THE HAND myCar.speed    I'M BATMAN inherited field
    TALK TO THE HAND myCar.gear     I'M BATMAN own field
YOU HAVE BEEN TERMINATED
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `LIKE FATHER LIKE SON` | Extend parent class | Various |

### 11.7 This Reference (PLANNED — NOT BUILT)

Access the current instance using `LOOK AT ME`.

```actionc
MY NAME IS MAXIMUS Counter
    OPEN TO THE PUBLIC value

    IT'S ALIVE
        GET TO THE CHOPPER LOOK AT ME.value
        HERE IS MY INVITATION 100
        ENOUGH TALK
    BIRTH COMPLETE
STRENGTH AND HONOR
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `LOOK AT ME` | This/self reference | Predator (Dutch) |
| `LOOK AT ME.field` | Access field on this | — |

### 11.8 Instance Methods (PLANNED — NOT BUILT)

Define methods on classes that can access instance fields.

```actionc
MY NAME IS MAXIMUS Player
    OPEN TO THE PUBLIC health

    IT'S ALIVE
        GET TO THE CHOPPER LOOK AT ME.health
        HERE IS MY INVITATION 100
        ENOUGH TALK
    BIRTH COMPLETE

    COMMANDER IN CHIEF takeDamage
    I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE amount
    GIVE THESE PEOPLE AIR
        GET TO THE CHOPPER LOOK AT ME.health
        HERE IS MY INVITATION LOOK AT ME.health
        GET DOWN amount
        ENOUGH TALK
        I'LL BE BACK LOOK AT ME.health
    DISMISSED SOLDIER

    COMMANDER IN CHIEF heal
    I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE amount
    GIVE THESE PEOPLE AIR
        GET TO THE CHOPPER LOOK AT ME.health
        HERE IS MY INVITATION LOOK AT ME.health
        GET UP amount
        ENOUGH TALK
        I'LL BE BACK LOOK AT ME.health
    DISMISSED SOLDIER
STRENGTH AND HONOR

IT'S SHOWTIME
    WELCOME TO EARTH hero AS Player

    I'M BATMAN Call instance method
    HEY CHRISTMAS TREE newHealth
    YOU SET US UP 0
    GET YOUR ASS TO MARS newHealth
    DO IT NOW hero.takeDamage 25

    TALK TO THE HAND newHealth    I'M BATMAN prints 75
YOU HAVE BEEN TERMINATED
```

| Keyword | Purpose | Source |
|---------|---------|--------|
| `COMMANDER IN CHIEF` | Start instance method | Various military |
| `DISMISSED SOLDIER` | End instance method | Various military |
| `DO IT NOW obj.method args` | Call instance method | Total Recall |

### 11.9 Implementation Notes

- Each class generates a separate `.class` file
- Fields default to integer type (INT)
- Constructors are no-argument only
- Objects are stored as JVM references (ALOAD/ASTORE)
- Field access uses GETFIELD/PUTFIELD bytecode
- Inheritance uses JVM's native class extension
- Instance methods use INVOKEVIRTUAL for dispatch
- Field resolution walks the class hierarchy for inherited fields

---

## 12. Example Programs

### 12.1 Hello World

```actionc
IT'S SHOWTIME
    TALK TO THE HAND "HELLO BOYS I'M BACK"
YOU HAVE BEEN TERMINATED
```

### 12.2 FizzBuzz

```actionc
IT'S SHOWTIME
    LET'S ROCK i FROM 1 TO 100
        HEY CHRISTMAS TREE fizz
        YOU SET US UP i
        GET TO THE CHOPPER fizz
        HERE IS MY INVITATION fizz
        I LET HIM GO 15
        ENOUGH TALK

        BECAUSE I'M GOING TO SAY PLEASE fizz YOU ARE NOT YOU YOU ARE ME 0
            TALK TO THE HAND "FizzBuzz"
        BULLSHIT
            HEY CHRISTMAS TREE fizz3
            YOU SET US UP i
            GET TO THE CHOPPER fizz3
            HERE IS MY INVITATION fizz3
            I LET HIM GO 3
            ENOUGH TALK

            BECAUSE I'M GOING TO SAY PLEASE fizz3 YOU ARE NOT YOU YOU ARE ME 0
                TALK TO THE HAND "Fizz"
            BULLSHIT
                HEY CHRISTMAS TREE fizz5
                YOU SET US UP i
                GET TO THE CHOPPER fizz5
                HERE IS MY INVITATION fizz5
                I LET HIM GO 5
                ENOUGH TALK

                BECAUSE I'M GOING TO SAY PLEASE fizz5 YOU ARE NOT YOU YOU ARE ME 0
                    TALK TO THE HAND "Buzz"
                BULLSHIT
                    TALK TO THE HAND i
                YOU HAVE NO RESPECT FOR LOGIC
            YOU HAVE NO RESPECT FOR LOGIC
        YOU HAVE NO RESPECT FOR LOGIC
    GAME OVER MAN GAME OVER
YOU HAVE BEEN TERMINATED
```

### 12.3 Fibonacci with Arrays

```actionc
IT'S SHOWTIME
    I'M BATMAN Declare array for first 20 Fibonacci numbers
    I AIN'T GOT TIME TO BLEED fib WITH 20 UGLY MOTHERF***ERS

    I'M BATMAN Initialize first two values
    GET IN LINE fib AT 0
    HERE IS MY INVITATION 0
    ENOUGH TALK

    GET IN LINE fib AT 1
    HERE IS MY INVITATION 1
    ENOUGH TALK

    I'M BATMAN Calculate rest using for loop
    LET'S ROCK i FROM 2 TO 19
        HEY CHRISTMAS TREE prev1
        YOU SET US UP GET IN LINE fib AT i GET DOWN 1

        HEY CHRISTMAS TREE prev2
        YOU SET US UP GET IN LINE fib AT i GET DOWN 2

        GET IN LINE fib AT i
        HERE IS MY INVITATION prev1
        GET UP prev2
        ENOUGH TALK
    GAME OVER MAN GAME OVER

    I'M BATMAN Print results
    LET'S ROCK i FROM 0 TO 19
        TALK TO THE HAND GET IN LINE fib AT i
    GAME OVER MAN GAME OVER
YOU HAVE BEEN TERMINATED
```

### 12.4 Error Handling Example

```actionc
IT'S SHOWTIME
    LET'S SEE WHAT YOU'VE GOT
        HEY CHRISTMAS TREE divisor
        YOU SET US UP 0

        BECAUSE I'M GOING TO SAY PLEASE divisor YOU ARE NOT YOU YOU ARE ME 0
            WELCOME TO THE PARTY PAL "Division by zero!"
        YOU HAVE NO RESPECT FOR LOGIC

        HEY CHRISTMAS TREE result
        YOU SET US UP 100
        GET TO THE CHOPPER result
        HERE IS MY INVITATION result
        HE HAD TO SPLIT divisor
        ENOUGH TALK

        TALK TO THE HAND result
    GOTCHA error
        TALK TO THE HAND "Caught error:"
        TALK TO THE HAND error
    CLEAN UP ON AISLE FIVE
        TALK TO THE HAND "Cleanup complete"
    THAT'S A WRAP
YOU HAVE BEEN TERMINATED
```

### 12.5 String Manipulation

```actionc
IT'S SHOWTIME
    I HAVE COME HERE TO CHEW BUBBLEGUM message
    AND KICK ASS "  yippee ki yay  "

    I'M BATMAN Trim whitespace
    I HAVE COME HERE TO CHEW BUBBLEGUM trimmed
    AND KICK ASS CUT THE FAT FROM message

    I'M BATMAN Convert to uppercase
    I HAVE COME HERE TO CHEW BUBBLEGUM shouted
    AND KICK ASS SAY IT LOUDER trimmed

    TALK TO THE HAND shouted    I'M BATMAN Prints: YIPPEE KI YAY

    I'M BATMAN Check if contains a word
    DO YOU FEEL LUCKY hasYippee
    WELL DO YA PUNK YOU TALKING TO ME ABOUT message "yippee"

    BECAUSE I'M GOING TO SAY PLEASE hasYippee
        TALK TO THE HAND "Found it!"
    YOU HAVE NO RESPECT FOR LOGIC
YOU HAVE BEEN TERMINATED
```

---

## 13. Quick Reference

### 13.1 All Keywords by Category

#### Program Structure
| Keyword | Purpose |
|---------|---------|
| `IT'S SHOWTIME` | Begin main |
| `YOU HAVE BEEN TERMINATED` | End main |
| `LISTEN TO ME VERY CAREFULLY` | Declare function |
| `HASTA LA VISTA, BABY` | End function |

#### Variables
| Keyword | Purpose |
|---------|---------|
| `HEY CHRISTMAS TREE` | Declare integer |
| `YOU SET US UP` | Initialize variable |
| `NOW I HAVE A MACHINE GUN` | Declare float |
| `I HAVE COME HERE TO CHEW BUBBLEGUM` | Declare string |
| `I AIN'T GOT TIME TO BLEED` | Declare array |
| `DO YOU FEEL LUCKY` | Declare boolean |

#### Assignment
| Keyword | Purpose |
|---------|---------|
| `GET TO THE CHOPPER` | Begin assignment |
| `HERE IS MY INVITATION` | Set value |
| `ENOUGH TALK` | End assignment |
| `ONE MORE TIME` | Increment |
| `COUNTDOWN` | Decrement |

#### Arithmetic
| Keyword | Operation |
|---------|-----------|
| `GET UP` | + |
| `GET DOWN` | - |
| `YOU'RE FIRED` | * |
| `HE HAD TO SPLIT` | / |
| `I LET HIM GO` | % |

#### Comparison
| Keyword | Operation |
|---------|-----------|
| `YOU ARE NOT YOU YOU ARE ME` | == |
| `IT'S JUST BEEN REVOKED` | != |
| `LET OFF SOME STEAM BENNET` | > |
| `YOU'RE THE DISEASE AND I'M THE CURE` | < |
| `I'M GETTING TOO OLD FOR THIS` | >= |
| `BENEATH YOU` | <= |

#### Logical
| Keyword | Operation |
|---------|-----------|
| `KNOCK KNOCK` | AND |
| `CONSIDER THAT A DIVORCE` | OR |
| `NEGATIVE` | NOT |

#### Bitwise
| Keyword | Operation |
|---------|-----------|
| `WINNERS GO HOME AND DATE THE PROM QUEEN` | & |
| `DEAD OR ALIVE YOU'RE COMING WITH ME` | \| |
| `FRIEND OR FOE` | ^ |
| `MOVE IT` | << |
| `FALL BACK` | >> |

#### Control Flow
| Keyword | Purpose |
|---------|---------|
| `BECAUSE I'M GOING TO SAY PLEASE` | If |
| `BULLSHIT` | Else |
| `YOU HAVE NO RESPECT FOR LOGIC` | End if |
| `STICK AROUND` | While |
| `CHILL` | End while |
| `LET'S ROCK` | For loop |
| `GAME OVER MAN GAME OVER` | End for |
| `GET OUT` | Break |
| `KEEP MOVING` | Continue |
| `CHOOSE YOUR DESTINY` | Switch |
| `WHAT IF I TOLD YOU` | Case |
| `SAME OLD SAME OLD` | Default |
| `FINISH HIM` | End switch |

#### Functions
| Keyword | Purpose |
|---------|---------|
| `I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE` | Parameter |
| `GIVE THESE PEOPLE AIR` | Mark as non-void |
| `I'LL BE BACK` | Return |
| `DO IT NOW` | Call function |
| `GET YOUR ASS TO MARS` | Assign return value |

#### Error Handling
| Keyword | Purpose |
|---------|---------|
| `LET'S SEE WHAT YOU'VE GOT` | Try |
| `WELCOME TO THE PARTY PAL` | Throw |
| `GOTCHA` | Catch |
| `CLEAN UP ON AISLE FIVE` | Finally |
| `I AM THE LAW` | Assert |

#### I/O
| Keyword | Purpose |
|---------|---------|
| `TALK TO THE HAND` | Print |
| `I WANT TO ASK YOU A BUNCH OF QUESTIONS...` | Read int |
| `OPEN THE DOOR` | Open file |
| `WHAT'S IN THE BOX` | Read file |
| `WRITE THAT DOWN` | Write file |
| `SEAL THE EXITS` | Close file |

#### Comments
| Keyword | Purpose |
|---------|---------|
| `I'M BATMAN` | Single-line comment |
| `GATHER ROUND` | Start block comment |
| `DISMISSED` | End block comment |

#### OOP (Classes and Objects)
| Keyword | Purpose |
|---------|---------|
| `MY NAME IS MAXIMUS` | Class declaration |
| `STRENGTH AND HONOR` | End class |
| `OPEN TO THE PUBLIC` | Public field |
| `THAT'S CLASSIFIED` | Private field |
| `IT'S ALIVE` | Constructor start |
| `BIRTH COMPLETE` | Constructor end |
| `WELCOME TO EARTH ... AS` | Create instance |
| `LIKE FATHER LIKE SON` | Inheritance (extends) |
| `LOOK AT ME` | This/self reference |
| `COMMANDER IN CHIEF` | Instance method start |
| `DISMISSED SOLDIER` | Instance method end |

#### Lambda Functions
| Keyword | Purpose |
|---------|---------|
| `CALL ME SNAKE` | Define lambda |
| `THE NAME'S PLISSKEN` | Function reference |
| `=>` | Lambda body separator |

#### Async/Concurrency
| Keyword | Purpose |
|---------|---------|
| `COVER ME` | Start async block |
| `MISSION COMPLETE` | End async block |
| `HOLD THE LINE` | Await task completion |
| `task.result` | Access async result |
| `task.done` | Check task completion |

#### Literals
| Keyword | Value |
|---------|-------|
| `@NO PROBLEMO` | true |
| `@I LIED` | false |
| `@THERE IS NO SPOON` | null |

---

## 14. Movie Sources

### Films Referenced

| Film | Year | Characters |
|------|------|------------|
| 300 | 2006 | King Leonidas |
| Aliens | 1986 | Ripley, Hudson, Hicks |
| Batman | 1989 | Batman/Bruce Wayne |
| Big Trouble in Little China | 1986 | Jack Burton |
| Cobra | 1986 | Marion Cobretti |
| Die Hard | 1988 | John McClane |
| Dirty Harry | 1971 | Harry Callahan |
| Escape from New York | 1981 | Snake Plissken |
| Gladiator | 2000 | Maximus |
| Independence Day | 1996 | President Whitmore, Capt. Hiller |
| Judge Dredd | 1995 | Judge Dredd |
| Lethal Weapon | 1987 | Riggs, Murtaugh |
| Lethal Weapon 2 | 1989 | Riggs, Murtaugh |
| Mortal Kombat | 1995 | Shang Tsung |
| Predator | 1987 | Dutch, Blain |
| Robocop | 1987 | Robocop/Murphy |
| Se7en | 1995 | Detective Mills |
| Speed | 1994 | Jack Traven |
| Sudden Impact | 1983 | Harry Callahan |
| Taxi Driver | 1976 | Travis Bickle |
| Terminator 2 | 1991 | T-800, Sarah Connor |
| The Matrix | 1999 | Neo, Morpheus |
| The Rock | 1996 | John Mason, Stanley Goodspeed |
| They Live | 1988 | Nada |
| Total Recall | 1990 | Douglas Quaid |

### Quote Attribution

Every keyword in ActionC comes from or is inspired by iconic moments in action cinema. The language is a tribute to the one-liners that defined a generation of filmmaking.

---

## Appendix A: Error Messages

| Error | Message Quote | Source |
|-------|---------------|--------|
| Syntax Error | `YOU HAD ONE JOB` | Modern |
| Division by Zero | `YOU JUST MADE A BIG MISTAKE` | Various |
| Null Pointer | `NOBODY'S HOME` | Various |
| Stack Overflow | `IT'S A TRAP` | Star Wars (crossover!) |
| Array Out of Bounds | `YOU'VE GONE TOO FAR` | Various |
| Type Mismatch | `WHAT ARE YOU` | Dutch - Predator |
| Undefined Variable | `WHO ARE YOU` | Various |
| File Not Found | `WHERE IS IT` | Various |
| Success | `MISSION ACCOMPLISHED` | Various |
| Compilation Complete | `CONSIDER THAT A DIVORCE` | Total Recall |

---

## Appendix B: Grammar (EBNF)

```ebnf
program         = { class_definition }, main_block, { method_declaration } ;
main_block      = "IT'S SHOWTIME", { statement }, "YOU HAVE BEEN TERMINATED" ;

(* Class definitions *)
class_definition = "MY NAME IS MAXIMUS", identifier, [ inheritance ],
                   { field_declaration },
                   [ constructor ],
                   { instance_method },
                   "STRENGTH AND HONOR" ;

inheritance     = "LIKE FATHER LIKE SON", identifier ;

field_declaration = ( "OPEN TO THE PUBLIC" | "THAT'S CLASSIFIED" ), identifier ;

constructor     = "IT'S ALIVE", { statement }, "BIRTH COMPLETE" ;

instance_method = "COMMANDER IN CHIEF", identifier,
                  { parameter },
                  [ "GIVE THESE PEOPLE AIR" ],
                  { statement },
                  "DISMISSED SOLDIER" ;

(* Function declarations *)
method_declaration = "LISTEN TO ME VERY CAREFULLY", identifier,
                     { parameter },
                     [ "GIVE THESE PEOPLE AIR" ],
                     { statement },
                     "HASTA LA VISTA, BABY" ;

parameter       = "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE", identifier ;

(* Lambda functions *)
lambda_def      = "CALL ME SNAKE", identifier, "(", { identifier }, ")", "=>", lambda_expr ;
lambda_expr     = arith_op_chain ;
func_ref        = "THE NAME'S PLISSKEN", identifier ;

(* Async/concurrency *)
async_block     = "COVER ME", identifier, { statement }, "MISSION COMPLETE" ;
await_stmt      = "HOLD THE LINE", identifier ;

(* Statements *)
statement       = declaration | assignment | print_stmt | if_stmt |
                  while_stmt | for_stmt | switch_stmt | return_stmt |
                  method_call | try_stmt | break_stmt | continue_stmt |
                  lambda_def | async_block | await_stmt | new_instance ;

declaration     = int_decl | float_decl | string_decl | array_decl | bool_decl ;
int_decl        = "HEY CHRISTMAS TREE", identifier, "YOU SET US UP", expression ;
float_decl      = "NOW I HAVE A MACHINE GUN", identifier, "HO HO HO", float_literal ;
string_decl     = "I HAVE COME HERE TO CHEW BUBBLEGUM", identifier,
                  "AND KICK ASS", string_expression ;
array_decl      = "I AIN'T GOT TIME TO BLEED", identifier,
                  "WITH", integer, "UGLY MOTHERF***ERS" ;
bool_decl       = "DO YOU FEEL LUCKY", identifier, "WELL DO YA PUNK", bool_literal ;

new_instance    = "WELCOME TO EARTH", identifier, "AS", identifier ;

assignment      = "GET TO THE CHOPPER", ( identifier | field_access ),
                  "HERE IS MY INVITATION", expression,
                  { operation },
                  "ENOUGH TALK" ;

field_access    = identifier, ".", identifier ;
this_reference  = "LOOK AT ME" ;
this_field      = "LOOK AT ME", ".", identifier ;

operation       = arith_op | compare_op | logical_op | bitwise_op ;
arith_op        = ( "GET UP" | "GET DOWN" | "YOU'RE FIRED" |
                    "HE HAD TO SPLIT" | "I LET HIM GO" ), operand ;
compare_op      = ( "YOU ARE NOT YOU YOU ARE ME" | "IT'S JUST BEEN REVOKED" |
                    "LET OFF SOME STEAM BENNET" | "YOU'RE THE DISEASE AND I'M THE CURE" |
                    "I'M GETTING TOO OLD FOR THIS" | "BENEATH YOU" ), operand ;
logical_op      = ( "KNOCK KNOCK" | "CONSIDER THAT A DIVORCE" ), operand ;
bitwise_op      = ( "WINNERS GO HOME AND DATE THE PROM QUEEN" |
                    "DEAD OR ALIVE YOU'RE COMING WITH ME" |
                    "FRIEND OR FOE" | "MOVE IT" | "FALL BACK" ), operand ;

print_stmt      = "TALK TO THE HAND", ( string_literal | expression ) ;

if_stmt         = "BECAUSE I'M GOING TO SAY PLEASE", expression,
                  { statement },
                  [ "BULLSHIT", { statement } ],
                  "YOU HAVE NO RESPECT FOR LOGIC" ;

while_stmt      = "STICK AROUND", expression,
                  { statement },
                  "CHILL" ;

for_stmt        = "LET'S ROCK", identifier, "FROM", expression, "TO", expression,
                  { statement },
                  "GAME OVER MAN GAME OVER" ;

switch_stmt     = "CHOOSE YOUR DESTINY", expression,
                  { case_clause },
                  [ default_clause ],
                  "FINISH HIM" ;
case_clause     = "WHAT IF I TOLD YOU", literal, { statement } ;
default_clause  = "SAME OLD SAME OLD", { statement } ;

try_stmt        = "LET'S SEE WHAT YOU'VE GOT",
                  { statement },
                  "GOTCHA", identifier,
                  { statement },
                  [ "CLEAN UP ON AISLE FIVE", { statement } ],
                  "THAT'S A WRAP" ;

return_stmt     = "I'LL BE BACK", [ expression ] ;
break_stmt      = "GET OUT" ;
continue_stmt   = "KEEP MOVING" ;

method_call     = [ "GET YOUR ASS TO MARS", identifier ],
                  "DO IT NOW", ( identifier | field_access ), { operand } ;

operand         = identifier | integer | float_literal | bool_literal |
                  string_literal | field_access | this_field | this_reference |
                  func_ref | "(@NO PROBLEMO)" | "(@I LIED)" | "(@THERE IS NO SPOON)" ;

identifier      = letter, { letter | digit | "_" } ;
integer         = [ "-" ], digit, { digit } ;
float_literal   = [ "-" ], digit, { digit }, ".", digit, { digit } ;
string_literal  = '"', { character }, '"' ;
bool_literal    = "@NO PROBLEMO" | "@I LIED" ;

comment         = single_comment | block_comment ;
single_comment  = "I'M BATMAN", { character }, newline ;
block_comment   = "GATHER ROUND", { character }, "DISMISSED" ;
```

---

*"Call me Snake."*

**ActionC v2.0** — Where every line of code is a one-liner. Now with OOP, lambdas, and async!
