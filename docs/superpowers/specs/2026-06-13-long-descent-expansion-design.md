# CAER VODRAN — "The Long Descent" expansion

**Status:** approved design (2026-06-13)
**Goal:** Expand the dungeon and story to a 10-boss / 30-floor campaign, add room-type
variety + random chests, add passive gear effects (thorns, evasion), and raise difficulty ~20%.

All gameplay is generated from `build/content.py` (data) + `build/gen.py` (emitter) into
`caer_vodran.actionc`, compiled by the vendored ActionC compiler. ActionC constraints still
hold: int-only fields/methods/stdin, no globals, strings live in data files, `print` adds a
newline, conditions take a pre-computed boolean. String literals now support escapes
(`\n \t \uXXXX`), already used for ANSI color.

---

## A. Structure (3 acts → 10 acts, 12 → 30 floors)

- **30 floors, 10 acts × 3 floors.** Floors `3,6,9,…,30` are boss floors; floors 1,2 of each
  act are explore floors.
- `gen.py` generalizations (replace hardcoded-3 logic):
  - `floor_to_act_expr`: `act = (floor-1)/3 + 1`.
  - `gen_explore` floor-done check: boss when `floor % 3 == 0` (was `==4/8/12`).
  - `bossid = act-1` (0–9). All per-boss switches already iterate `C.BOSSES`, so they scale.
  - Final-boss check: `act == 10` (`bossid == 9`) → victory; else advance.
  - `ACT_INTRO`: 10 entries. `FLOORS`: 30 entries (roomcount already data-driven).
- **Boss-defeated flags:** the write-only `b0/b1/b2` (set on defeat, saved, never read) collapse
  into a single `bossmask` int — one bit per boss, set via `bit_set` on defeat.
- **Save format** (`SAVE_FIELDS`): replace `b0,b1,b2` with `bossmask`. (Old saves become
  incompatible; acceptable — saves are per-player and disposable.)

## B. Story — full 10-chapter rewrite

Keeps the premise (a fallen star caged beneath Caer Vodran in the Mirewood; the Crown; the
whisper) but re-chapters the descent into 10 regions, each ending in a new boss. Surface → heart:

| Act | Region | Boss (working name) | Theme |
|----|--------|--------------------|-------|
| 1 | The Drowned Mire | Bog-Warden of Thorns | drowned approach, thorn/bog |
| 2 | The Broken Barbican | The Gate-Golem | shattered outerworks, construct |
| 3 | The Flooded Wards | Sir Caedric, the Drowned | flooded courtyards, knight-commander |
| 4 | The Hollow Halls | The Echo | hollow soldiery, sound/echo |
| 5 | The Sunken Archive | The Ink-Wraith | drowned library, lore/ink |
| 6 | The Ash Barracks | Captain Vurm, Unfallen | garrison that never stood down |
| 7 | The Chapel of the Choir | Maerith of the Choir | cult chapel, stolen voices |
| 8 | The Star-Crypt | The Crystalline Horror | black-crystal caverns |
| 9 | The Whispering Deep | The Mirror (a copy of you) | the whisper wears your shape |
| 10 | The Vault of the Crown | Vodran, the Sunken Crown | the star's heart (final) |

Each boss: fresh intro + defeat lore, 3 phase behaviors (reuse the existing phase tags:
harden/heal/enrage/drain/void), and a unique gear drop. Prose written during implementation,
matching the established voice. New `INTRO`/`ENDING` retained or lightly revised.

**Boss stat curve (base, pre-×1.2), tuned in impl:**

| Act | HP | ATK | DEF | XP | Gold |
|----|----|-----|-----|----|----|
| 1 | 110 | 11 | 5 | 120 | 110 |
| 2 | 155 | 13 | 6 | 170 | 150 |
| 3 | 215 | 15 | 7 | 240 | 200 |
| 4 | 290 | 17 | 8 | 320 | 260 |
| 5 | 375 | 19 | 9 | 420 | 320 |
| 6 | 460 | 22 | 10 | 540 | 390 |
| 7 | 560 | 25 | 11 | 680 | 470 |
| 8 | 650 | 28 | 12 | 820 | 560 |
| 9 | 730 | 31 | 13 | 980 | 660 |
| 10 | 820 | 34 | 15 | 1500 | 800 |

## C. Bestiary & gear scaled to 30 floors

- **Monsters:** keep the existing ~50 (re-themed to acts 1–4 floor ranges) and **add ~50 new**
  covering floors 13–30, grouped by region. Each monster keeps `(…, minfloor, maxfloor)`; the
  per-floor pool auto-derives from those ranges (`floor_pool`). New races as needed (e.g.
  "Construct", "Crystal", "Mirror", "Drowned").
  - Stat guideline by floor band: hp/atk/def/xp/gold rise ~linearly with floor; deeper monsters
    favor the new ability tags. (Concrete table built in impl; difficulty ×1.2 applied globally.)
- **Gear extension + re-gating across 10 acts:**
  - **Schema change:** every weapon/armor/trinket gains an explicit **`gate`** (unlock act 1–10).
    Shop lists an item when `act >= gate` (replaces the tier→act mapping).
  - Weapons: 12 → **18 per class** (add late tiers: power ~32/35/38/42/46/50, materials
    `eclipse`/`star`/`voidglass`), gates spread over acts 4–10.
  - Armor: 13 → **~20**; Trinkets: 8 → **~14**. Higher stat tiers for late acts; some carry the
    new effects (§F).
- **Leveling:** unchanged curve (`xpnext = 30 + 4·level²`); scales indefinitely, characters reach
  ~lvl 25–30 over a full run. No cap needed.

## D. New room types

`gen_explore` room roll re-weighted (0–99), tunable:

| Range | Type | Notes |
|------|------|------|
| 0–35 | combat | normal pool fight |
| 36–42 | ambush | elite-tier surprise + bonus loot |
| 43–48 | elite | existing elite |
| 49–60 | event | existing branching events |
| 61–70 | chest | random loot (§E) |
| 71–77 | treasure | existing gold/potion cache |
| 78–83 | shrine | choose a blessing |
| 84–89 | hazard | DEX/luck check, damage or reward |
| 90–94 | lore | story fragment + small reward |
| 95–99 | rest | existing partial heal |

- **Shrine/Altar:** a choice room granting a temporary blessing: (1) brand charge (`brandbuff`,
  reuse), (2) a one-fight shield (`nextshield` field, applied at next combat start), or (3) a
  full/partial heal — sometimes at a gold/HP cost.
- **Hazard:** a `rnd`-vs-DEX check; pass clean ("you slip past") or take floor-scaled damage;
  some hazards guard a small reward on success.
- **Ambush:** reuse the elite combat path (`isboss=2` boost) flagged for **bonus loot** (extra
  gold + a guaranteed consumable on win).
- **Lore/Memory vault:** prints a region-specific story fragment + small XP/gold (occasionally a
  minor gear find). Pure flavor + minor reward; deepens the rewritten world.

## E. Random chests with random loot

New chest room rolls one outcome (weights tunable):

| Weight | Outcome |
|------|---------|
| 35% | gold, floor-scaled (`base + rnd + floor·k`) |
| 25% | a random consumable (potHeal/potRes/potCure/bomb/elixir) |
| 20% | a piece of gear available at the current act (random weapon/armor/trinket, marked owned) |
| 12% | a rare trinket (marked owned) |
| 8% | **mimic** — springs into an elite-tier fight |

Outcome and item are printed ("The chest holds …"). The existing choice-based "Bound Chest"
EVENT is kept (renamed if needed to avoid confusion with the auto-loot chest room).

## F. Gear effects: Thorns + Evasion

- **Schema:** armor & trinket tuples gain `(efftype, effval)`. `efftype`: 0 none, 1 thorns
  (effval = % of damage reflected), 2 evasion (effval = % dodge chance).
- **Aggregation:** `emit_recompute_equip` sums armor+trinket into new fields `eqthorns`,
  `eqevade` (alongside `eqdef/eqhp/eqres/eqcrit`). Clamp `eqevade` ≤ 60.
- **Combat hooks** (both `combat_loop` and `boss_loop`, in `enemy_turn`→`strike`):
  - **Evasion:** before damage lands, `rnd(100) < eqevade` → dodge: print "you slip aside — the
    blow misses!", skip the HP subtraction and the enemy post-ability.
  - **Thorns:** after a hit deals `edmg` to the player, `reflect = edmg·eqthorns/100`; if `>0`,
    subtract from `ehp` and print "thorns reflect N." (cannot kill via thorns mid-enemy-turn is
    fine — enemy-dead is re-checked next loop).
- **Items:** new gear grants these — e.g. a **Thornmail** (armor, thorns), a **Mistcloak**
  (armor, evasion), a **Bramble Ring** / **Phantom Charm** (trinkets). Existing stat-only gear
  keeps `efftype=0`.

## G. Difficulty +20%

- Single constant `DIFFICULTY = 1.2` in `gen.py`. Applied at **data-emit time** (in `gen_boot`,
  where monster and boss tables are written) to **HP and ATK only**: `round(base · 1.2)`.
- Content tables keep base numbers (one dial to retune). Rewards/economy unchanged. Elite/ambush
  ×1.5 boost stacks on top as today.

## H. Schema & field summary

- **content.py tuples:**
  - `WEAPONS[c]`: `(name, tier, material, power, price, special, gate)`
  - `ARMORS`: `(name, material, tier, def, hp, res, price, gate, efftype, effval)`
  - `TRINKETS`: `(name, tier, hp, res, def, crit, price, gate, efftype, effval)`
  - `BOSSES`: 10 entries (add `floor`/`act` per curve above)
  - `FLOORS`: 30 entries; `ACT_INTRO`: 10 entries; `MONSTERS`: ~100.
- **New Game int fields:** `eqthorns`, `eqevade`, `bossmask`, `nextshield`.
  **Removed:** `b0`, `b1`, `b2`.
- `SAVE_FIELDS`: drop `b0/b1/b2`, add `bossmask`. (`eqthorns/eqevade/nextshield` are derived /
  transient — not saved; recomputed by `emit_recompute_equip` on load.)

## I. Build phases (each compiles, runs, and is verified before the next)

1. **Structure**: 3→10 act generalization (act math, boss cadence `floor%3==0`, `bossmask`,
   final check, `SAVE_FIELDS`). Stretch FLOORS/ACT_INTRO/BOSSES to 10/30 with placeholder-quality
   data so the game runs end to end. *Check:* a scripted run reaches and clears act-1 boss; the
   act/floor counters and breadcrumb are correct.
2. **Content**: write the 10 regions/floors, 10 bosses (lore + curve), ~50 new monsters, and the
   gear extension + `gate` re-gating. *Check:* shop shows act-appropriate gear at several acts;
   floor pools populate floors 1–30.
3. **Rooms + chests**: new explore roll + shrine/hazard/ambush/lore rooms + chest loot table.
   *Check:* forced room rolls produce each type without error; chest outcomes incl. mimic work.
4. **Effects**: thorns + evasion schema, aggregation, combat/boss hooks, effect-bearing gear.
   *Check:* equipping a thorns/evasion item reflects/dodges in a scripted fight.
5. **Difficulty + balance**: `DIFFICULTY=1.2` applied; quick balance read on enemy/boss numbers.
   *Check:* enemy HP/ATK in the emitted data files are ~1.2× base.

## J. Risks / constraints

- **ActionC int-only**: all new state is ints; new text (lore, names, room copy) lives in `say`
  literals or data files. No new strings in fields.
- **Method size / switches**: bossfight & combat already use generated switches over the boss
  list; adding bosses is data. Watch generated-method size but no hard limit hit expected.
- **Determinism**: room/chest/effect rolls use `rnd`; tests force specific seeds via scripted
  input where possible, else assert structurally on emitted source.
- **Difficulty as a knob**: keeping ×1.2 as one constant means future retunes are a one-line
  change and a regen.
