# CAER VODRAN — The Sunken Crown

A text-only roguelike, **written in [ActionC](https://github.com/rporterwood123/ActionC)** — the
esoteric "action-movie one-liner" programming language (a superset of ArnoldC that compiles to
JVM bytecode).

> Long ago a star fell on the kingdom of Aelthmoor, and the court built **Caer Vodran** to cage
> its light. The mages called the shard the **Sunken Crown**, and tried to wear it. It wore them
> instead. The castle sank into the **Mirewood**, and its halls filled with the changed.
> You are a **Delver**. The whisper has begun to reach the waking world. Go down. End it.

```
  ================================================================
          C A E R   V O D R A N
                 -- The Sunken Crown --
  ================================================================
```

---

## What it is

A complete, menu-driven dungeon crawl: descend through a forest-swallowed castle across **10 acts /
30 floors**, fighting your way to the star at the bottom. Everything is numbers-and-text in your
terminal.

| | |
|---|---|
| **Classes** | 3 — Fighter, Mage, Rogue — each with **3 attacks** (one unique signature) |
| **Weapons** | **54** (18 per class) across *normal / magic / unique* tiers and iron/leather/mythril/eclipse/voidglass/star materials |
| **Armor & trinkets** | 20 armors + 14 trinkets, all tiered — some grant passive **effects** (thorns reflect, evasion dodge) |
| **Enemies** | **~98 named NPCs** across 18 races (goblins, hollow knights, wraiths, voidspawn, constructs, crystal-horrors, mirror-selves, …) |
| **Bosses** | **10 unique**, each multi-phase, with their own lore and a signature drop |
| **Rooms** | combat, elite, **ambush**, events, **random chests** (incl. mimics), treasure, **shrines**, **hazards**, **lore vaults**, and rest |
| **Systems** | leveling (1→~25-30) with per-class growth, a camp hub, a wandering merchant/shop, equipment, consumables, varied rooms & events, and **save / continue** |
| **Length** | a careful first run is roughly **5–7 hours** |

---

## Requirements

- **JDK 21** and **sbt** (to build the ActionC compiler).
  On the dev machine these live at `/home/pwood/tools/jdk-21.0.11+10` and `/home/pwood/tools/sbt`.
  Any JDK 21 + sbt 1.x works.

## Build & run

```bash
# 1) Build the ActionC compiler (one time)
cd ActionC && sbt assembly && cd ..

# 2) Build the game + play it
./build.sh run
```

`build.sh` (re)generates the ActionC source, compiles it to `Game.class` + `caer_vodran.class`,
and—with `run`—launches it. To just compile: `./build.sh`. To run an already-built game:

```bash
export JAVA_HOME=/home/pwood/tools/jdk-21.0.11+10
java caer_vodran
```

The game writes a few `vodran_*.dat` data files on first launch (its content tables) and a
`vodran_save.dat` when you save. These are regenerated/owned by the game; delete them freely.

> **Controls:** the game reads **numbers only**. Every screen is a numbered menu — type the number
> and press Enter. (Typing a non-number will end the program, a limitation of the language's stdin.)

---

## How to play

You begin at a **Camp** before each floor. From here you can **Descend**, **Rest** (full heal),
visit the **Shop**, manage **Equipment**, use **Inventory** items, **Save**, or save-and-quit.

**Descending** takes you through a handful of rooms — fights, treasure, shrines, traps, and
branching **events** (drink from a whispering font? force the bound chest?). Every 4th floor ends
in a **boss**. Clear it to open the next act.

**In combat** you choose each turn:

```
  [1] attack        a reliable basic strike (free)
  [2] signature     your class's unique move (costs your resource)
  [3] skill         a utility move (stun / weaken / poison / shield)
  [4] item          heal, restore resource, cure poison, bomb, or elixir
  [5] flee          escape a normal fight (you cannot flee a boss — [5] braces)
```

Crit chance scales with DEX; resource regenerates a little each turn. **Death** sends you back to
your **last save** (so save at camp often).

### The three paths

| Class | Stats | Resource | Attacks (★ = unique signature) |
|-------|-------|----------|-------------------------------|
| **Fighter** | high HP / STR / DEF | Stamina | Strike · ★**Cleave** (heavy, staggers) · Shield Bash |
| **Mage** | high INT / Mana | Mana | Firebolt · ★**Arcane Surge** (scales with INT) · Hex (weaken) |
| **Rogue** | high DEX / crit | Energy | Slash · ★**Backstab** (huge crit + bleed) · Poison Strike |

### The descent

Ten acts, three floors each (two to explore, one boss), down from the bog to the star's heart:

- **Act I — The Drowned Mire** → **The Mire-Warden**
- **Act II — The Broken Barbican** → **The Gate-Golem**
- **Act III — The Flooded Wards** → **Sir Caedric, the Drowned**
- **Act IV — The Hollow Halls** → **The Echo**
- **Act V — The Sunken Archive** → **The Ink-Wraith**
- **Act VI — The Ash Barracks** → **Captain Vurm, Unfallen**
- **Act VII — The Chapel of the Choir** → **Maerith of the Choir**
- **Act VIII — The Star-Crypt** → **The Crystalline Horror**
- **Act IX — The Whispering Deep** → **The Mirror** (a copy of you)
- **Act X — The Vault of the Crown** → **Vodran, the Sunken Crown**

Each boss shifts behavior as its health falls (growing fiercer, mending itself, raging)
and drops a unique piece of gear. Enemies and bosses hit ~20% harder than their base tuning.

---

## How it's built (and one compiler fix)

ActionC has hard constraints that shape the whole design: **stdin is integer-only**, methods take
and return **only ints**, object fields are **int-only**, there are **no globals**, conditions need
a **pre-computed boolean**, and `print` always adds a newline. So:

- All game state lives in the **int fields of a single `Game` object**.
- `main` is a thin **conductor**: it owns the dispatch loop and is the only place that can call the
  `Game`'s instance-method "screens" (instance methods can't call each other), each of which sets
  `this.mode` for the next.
- Static methods are pure int math; **all text tables** (monster/weapon/armor names, etc.) live in
  data files the game writes at boot and reads back with `split` + parse, since strings can't be
  returned or stored in fields.
- The ActionC source (`caer_vodran.actionc`, ~9.6k lines) is **generated** from clean Python data
  (`build/content.py`) by `build/gen.py` via a small emitter (`build/emit.py`). The committed
  `.actionc` is real ActionC that compiles and runs on the stock compiler.

**The vendored compiler is now stock upstream — no local patch.** This game once needed a
local fix: ActionC's `Read` created a *new* `Scanner` on every read, which under piped/redirected
input makes the first read swallow the whole stream and the next read hit EOF — breaking any
interactive (multi-read) program. That fix — a **single shared `Scanner`** (a static `$scanner`
field initialised once and reused by every `Read`) — has since been merged **upstream** (ActionC
`master`) as part of a broader robustness/correctness pass (typed comparisons, switch `break`,
try/finally, frame scoping, int-only method validation, CLI fixes). So `ActionC/` now tracks stock
upstream master with no local changes, and the game compiles and runs on it unmodified.

---

## Files

```
caer_vodran.actionc     the game (generated ActionC source)
build/content.py        all content as plain data (classes, weapons, NPCs, bosses, floors, lore)
build/gen.py            generator: content -> ActionC
build/emit.py           the ActionC emitter (idioms, control flow, strings, bitops)
build.sh                regenerate + compile (+ run)
ActionC/                the ActionC compiler (stock upstream master)
```

*"Hasta la vista, Delver."*
