# The Long Descent Expansion — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement
> this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expand CAER VODRAN to a 10-boss / 30-floor campaign with a rewritten story, new room
types + random chests, passive gear effects (thorns, evasion), and a +20% difficulty bump.

**Architecture:** Pure data + emitter. Game content lives in `build/content.py`; `build/gen.py`
emits `caer_vodran.actionc`, compiled by the vendored ActionC jar. Generalize the hardcoded "3
acts" logic in `gen.py` to be data-driven off `content.py`, then grow the data. ActionC limits
hold: int-only fields/methods/stdin, strings in `say`-literals or data files, conditions take a
pre-computed boolean.

**Tech Stack:** Python 3 (generator), ActionC (game), JDK 21 + the vendored ActionC.jar.

**Spec:** `docs/superpowers/specs/2026-06-13-long-descent-expansion-design.md`

**Verification harness (used by every task's verify step):**
```bash
cd /projects/roguelike
export JAVA_HOME=/home/pwood/tools/jdk-21.0.11+10
export PATH="$JAVA_HOME/bin:$PATH"
./build.sh            # regen + compile; prints "compiled OK" on success
# scripted run helper (strip ANSI for readability):
play() { printf "$1" | java caer_vodran 2>&1 | sed -e 's/\x1b\[2J\x1b\[3J\x1b\[H/[CLR]/g' -e 's/\x1b\[[0-9;]*m//g'; }
```
"Tests" here = `./build.sh` compiles clean **and** a scripted run / `grep` on `caer_vodran.actionc`
shows the expected structure. Commit after each task that compiles and verifies.

---

## File structure (what changes, and why)

- `build/content.py` — all data. Grows: `BOSSES` 3→10, `FLOORS` 12→30, `ACT_INTRO` 3→10,
  `MONSTERS` ~50→~100, gear tables extended + new schema fields (`gate`, `efftype`, `effval`),
  new lore. One file; it's the data home by design.
- `build/gen.py` — emitter/logic. Edits: act/floor math, boss cadence, `bossmask`, final-boss
  check, `DIFFICULTY` scalar, new room dispatch + chest, effect aggregation + combat hooks,
  shop gating by `gate`, `FIELDS`/`SAVE_FIELDS`.
- `build/emit.py` — unchanged (already supports everything needed).
- `caer_vodran.actionc` — regenerated artifact (committed).

---

## PHASE 1 — Structure: 3 → 10 acts (game runs on stretched data)

Goal: the engine supports 30 floors / 10 bosses before real content lands. Use the existing 3
bosses repeated/placeholder so the game runs end-to-end.

### Task 1.1: Act/floor math + boss cadence

**Files:** Modify `build/gen.py` (`floor_to_act_expr`, `gen_explore` floor-done block).

- [ ] **Step 1: Change act math.** In `floor_to_act_expr`, replace the `/4` with `/3`:
```python
def floor_to_act_expr(e):
    # act = ((floor-1)/3)+1   (3 floors per act, boss on the 3rd)
    e.declare("aa", e.f("floor"))
    e.assign("aa", e.f("floor"), ("-", 1), ("/", 3), ("+", 1))
    return "aa"
```
- [ ] **Step 2: Change boss cadence.** In `gen_explore`'s `floor_done()`, replace the three
  `== 4/8/12` checks with a modulo test:
```python
        e.declare("bossfloor", 0)
        e.declare("fmod", e.f("floor"))
        e.assign("fmod", e.f("floor"), ("%", 3))
        e.if_cmp("fmod", "==", 0, lambda: e.set("bossfloor", 1))
```
- [ ] **Step 3: Verify.** `./build.sh` → "compiled OK". (Full run after Task 1.4.)
- [ ] **Step 4: Commit** `git commit -am "Phase 1: 3-floors-per-act math + modulo boss cadence"`

### Task 1.2: bossmask replaces b0/b1/b2

**Files:** Modify `build/gen.py` (`FIELDS`, `SAVE_FIELDS`, `gen_initclass`, `boss_defeat`).

- [ ] **Step 1: Fields.** In `FIELDS`, remove `"b0", "b1", "b2"`, add `"bossmask"`. In
  `SAVE_FIELDS`, remove `b0,b1,b2`, add `"bossmask"`.
- [ ] **Step 2: Init.** In `gen_initclass`, replace the `b0/b1/b2 = 0` line with
  `e.assign("LOOK AT ME.bossmask", 0)`.
- [ ] **Step 3: Set on defeat.** In `boss_defeat`, replace the `b0/b1/b2` switch with a single
  bit set on `bossid`:
```python
    e.bit_set("LOOK AT ME.bossmask", e.f("bossid"))
```
  (Remove the `def b0/b1/b2` closures and their switch.)
- [ ] **Step 4: Verify.** `./build.sh` → compiled OK; `grep -c 'bossmask' caer_vodran.actionc` > 0.
- [ ] **Step 5: Commit** `git commit -am "Phase 1: collapse b0/b1/b2 into bossmask bitfield"`

### Task 1.3: Final-boss check by act

**Files:** Modify `build/gen.py` (`boss_defeat` finalwin/advance branch).

- [ ] **Step 1:** Change the final check from `bossid == 2` to act 10 (`bossid == 9`):
```python
    e.if_cmp(e.f("bossid"), "==", 9, finalwin, else_body=advance)
```
- [ ] **Step 2: Verify** `./build.sh` → compiled OK.
- [ ] **Step 3: Commit** `git commit -am "Phase 1: final boss is act 10"`

### Task 1.4: Stretch data to 10 acts / 30 floors (placeholder quality)

**Files:** Modify `build/content.py` (`BOSSES`, `FLOORS`, `ACT_INTRO`); `build/gen.py`
(`set_roomcount` already data-driven; `floor_pool` already auto).

- [ ] **Step 1: FLOORS → 30.** Extend `FLOORS` to 30 tuples `(name, act, rooms, flavor)` with
  `act = ((i)//3)+1`, rooms 4–6. (Names can be provisional here; finalized in Phase 2.)
- [ ] **Step 2: ACT_INTRO → 10.** Add entries `4..10` (provisional 3-line lore each).
- [ ] **Step 3: BOSSES → 10.** Add 7 boss dicts (ids 3–9) with `floor=act*3`, `act=id+1`, stats
  from the spec curve, provisional lore + phase tags + a `drop` (reuse an existing gear name for
  now). Keep schema identical to current dicts.
- [ ] **Step 4: Monsters cover 30 floors (stopgap).** Temporarily bump the deepest monsters'
  `maxfloor` to 30 so `floor_pool` is non-empty on floors 13–30 (real bestiary in Phase 2).
- [ ] **Step 5: Verify full run.** `./build.sh` → compiled OK. Then a scripted descent reaches
  act-1 boss and clears to act 2:
```bash
play '1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n'  # new->fighter->descend->attack-spam
```
  Expected: combat → boss on floor 3 → act 2 actintro; stats line shows `Floor 3/30`.
- [ ] **Step 6: Commit** `git commit -am "Phase 1: stretch to 10 acts / 30 floors (placeholder content)"`

**Phase 1 acceptance:** game compiles and a scripted run advances through ≥1 boss with correct
Floor n/30 and act counters.

---

## PHASE 2 — Content: regions, bosses, bestiary, gear

### Task 2.1: 10 regions (FLOORS + ACT_INTRO + INTRO/ENDING polish)

**Files:** Modify `build/content.py`.

- [ ] **Step 1:** Write final `FLOORS` (30 tuples) per the spec's region table — 3 floors per
  region with names + flavor in the established voice (e.g. act 1 "The Drowned Mire": floors
  "Black Lily Causeway", "The Sunken Track", "Mire-Warden's Reach").
- [ ] **Step 2:** Write final `ACT_INTRO[1..10]` — 3 lines each, region-themed.
- [ ] **Step 3: Verify** `./build.sh` → compiled OK; `play` a descent and eyeball act intros.
- [ ] **Step 4: Commit** `git commit -am "Phase 2: 10 region names + act intros"`

### Task 2.2: 10 bosses (final lore + curve)

**Files:** Modify `build/content.py` (`BOSSES`).

- [ ] **Step 1:** Finalize all 10 boss dicts: name, `floor=act*3`, `act`, stats from the spec
  curve table, `intro`/`defeat` lore (3–4 lines), `phase2`/`phase3` thresholds + `p2`/`p3`
  behavior text, and a unique `drop` (a gear name added in Task 2.4). Boss 10 = Vodran (final).
- [ ] **Step 2: Verify** `./build.sh` → compiled OK; `grep -c 'IT.S ALIVE' ...` n/a — instead
  confirm 10 boss intros emit: `grep -c 'COMMANDER IN CHIEF bossfight' caer_vodran.actionc`
  is 1 and the boss-intro switch has 10 cases (`grep -c '>> ' ` per phase text is plausibility).
  Primary check: a scripted run that reaches a boss prints its new intro.
- [ ] **Step 3: Commit** `git commit -am "Phase 2: 10 bosses with lore + stat curve"`

### Task 2.3: Bestiary to floor 30 (~50 new monsters)

**Files:** Modify `build/content.py` (`MONSTERS`, `RACES`).

- [ ] **Step 1:** Revert the Task 1.4 maxfloor stopgap. Add new `RACES` as needed
  ("Construct","Drowned","Crystal","Mirror","Star").
- [ ] **Step 2:** Add ~50 monster tuples `(name, race, hp, atk, def, xp, gold, abil, minfloor,
  maxfloor)` spanning floors 13–30, grouped by region, stats rising with floor (extrapolate the
  existing act III curve). Re-theme/extend existing entries' floor ranges so floors 1–12 stay
  populated. Keep `abil` in 0–7.
- [ ] **Step 3: Verify** every floor's pool is non-empty:
```bash
python3 -c "import sys; sys.path.insert(0,'build'); import content as C; \
ok=all(any(m[8]<=f<=m[9] for m in C.MONSTERS) for f in range(1,31)); print('pools ok' if ok else 'GAP')"
```
  Expected: `pools ok`. Then `./build.sh` → compiled OK.
- [ ] **Step 4: Commit** `git commit -am "Phase 2: ~50 new monsters spanning floors 13-30"`

### Task 2.4: Gear extension + `gate` re-gating

**Files:** Modify `build/content.py` (`WEAPONS`,`ARMORS`,`TRINKETS` schema + rows); `build/gen.py`
(`gen_boot` weapon/armor/trinket writers, `_act_gate_weapon`, `shop_*` gating, `gen_class` field
reads if any positional indices shift).

- [ ] **Step 1: Schema.** Add `gate` (int act 1–10) as the LAST element of every weapon tuple,
  and `gate, efftype, effval` (efftype/effval = 0 for now) as the last elements of every armor &
  trinket tuple. Update the inline schema comments.
- [ ] **Step 2: gen.py readers.** In `gen_boot`, the weapon/armor/trinket record writers select
  fields by index — they currently write specific indices; leave the written record layout
  unchanged (don't emit `gate`/`effval` into the existing data files yet; they're build-time-only
  for gating in Phase 2, consumed for effects in Phase 4). Confirm no writer reads a now-shifted
  index incorrectly.
- [ ] **Step 3: Re-gate.** Replace `_act_gate_weapon(idx)` and the tier→act `gate = 1 if …`
  lines in `shop_weapons`/`shop_list` with the explicit per-item `gate`:
```python
# shop_weapons inner loop: gate = w[6]
# shop_list: gate = r[7]   (armor & trinket: gate is index 7)
```
- [ ] **Step 4: New gear rows.** Add ~6 weapons/class (18 total each; power 32/35/38/42/46/50,
  late materials), ~7 armors (→20), ~6 trinkets (→14). Set sensible `gate` acts spread 1–10.
  Effect fields stay 0 here (filled in Phase 4 for the thorns/evasion items).
- [ ] **Step 5: Verify** shop shows act-appropriate gear deep in:
```bash
play '1\n1\n1\n'      # to camp; then shop and inspect weapon list at act 1
```
  Plus `./build.sh` compiled OK. Sanity: late-act gear (gate≥6) should NOT appear in an act-1
  shop, and should appear once `act` is high (verify by a save edited to a high act, or trust the
  `act>=gate` code path + a structural grep).
- [ ] **Step 6: Commit** `git commit -am "Phase 2: extend gear + explicit per-item act gating"`

**Phase 2 acceptance:** full 30-floor content present; pools non-empty floors 1–30; a descent
shows region intros and new bosses; shop gates gear across acts.

---

## PHASE 3 — New room types + random chests

### Task 3.1: Re-weighted room roll + room dispatch

**Files:** Modify `build/gen.py` (`gen_explore` `present_room`).

- [ ] **Step 1:** Replace the room-type roll with the spec table (0–35 combat, 36–42 ambush,
  43–48 elite, 49–60 event, 61–70 chest, 71–77 treasure, 78–83 shrine, 84–89 hazard, 90–94 lore,
  95–99 rest). Build `rt` via cumulative `if roll >= N: set rt` steps, then a `switch` over
  `rt` 0–9 dispatching to `room_combat / room_ambush / room_combat(elite) / room_event /
  room_chest / room_treasure / room_shrine / room_hazard / room_lore / room_rest`.
- [ ] **Step 2:** Stub the four new room functions to no-op-but-valid (print a line, read
  continue) so it compiles before filling them in.
- [ ] **Step 3: Verify** `./build.sh` compiled OK; a long `play` descent hits varied rooms with
  no crash.
- [ ] **Step 4: Commit** `git commit -am "Phase 3: re-weighted room roll + dispatch (stubs)"`

### Task 3.2: room_chest (random loot table)

**Files:** Modify `build/gen.py` (new `room_chest`); `build/content.py` if a loot helper needs data.

- [ ] **Step 1:** Implement `room_chest(e)` per spec weights: roll `rnd(100)`; <35 gold (scaled
  `15 + rnd(25) + floor*2`), <60 random consumable (+1 to a `rnd`-picked count field), <80 a
  gear piece at current act (pick a `gate<=act` item, `bit_set` its own-flag), <92 a rare trinket
  (`bit_set ownt` on a chosen id), else mimic → `room_combat(e, elite=True)` with a "It was a
  mimic!" line. Print the outcome; non-combat outcomes end with a `read_choice` continue.
- [ ] **Step 2: Verify** force chests by `play`ing many descents; confirm each branch prints and
  mimic enters combat. `./build.sh` compiled OK.
- [ ] **Step 3: Commit** `git commit -am "Phase 3: random chest room with weighted loot + mimic"`

### Task 3.3: room_shrine, room_hazard, room_ambush, room_lore

**Files:** Modify `build/gen.py` (the four functions); `build/content.py` (`LORE_FRAGMENTS`,
`SHRINE` text, hazard flavor).

- [ ] **Step 1: room_ambush** — call `room_combat(e, elite=True)` with an "AMBUSH!" intro and a
  bonus-loot flag (extra gold + guaranteed `potHeal +1` on the post-combat reward; reuse the
  spoils block, gated by a transient `ambush` marker or just grant up-front).
- [ ] **Step 2: room_shrine** — a `read_choice` menu of 2–3 blessings: brand charge
  (`brandbuff=8`), a next-fight shield (`nextshield=N`), or a partial heal; some cost gold/HP.
- [ ] **Step 3: room_hazard** — `rnd(100)` vs a DEX-derived threshold; success "you slip past"
  (+ occasional small reward), failure floor-scaled `hp-N` + clamp.
- [ ] **Step 4: room_lore** — print a region-keyed fragment from a new `LORE_FRAGMENTS` table +
  small `xp+`/`gold+`; `read_choice` continue.
- [ ] **Step 5: Verify** `./build.sh` compiled OK; `play` descents exercise each room type with
  no crash; shrine choices apply (e.g. brand charge shows in next fight).
- [ ] **Step 6: Commit** `git commit -am "Phase 3: shrine / hazard / ambush / lore rooms"`

### Task 3.4: nextshield wiring

**Files:** Modify `build/gen.py` (`FIELDS` add `nextshield`; `gen_initclass` init 0; combat/boss
loop start: apply `pshield += nextshield` then zero it).

- [ ] **Step 1:** Add field; init; at the top of `combat_loop`/`boss_loop` (before the while),
  `pshield = nextshield; nextshield = 0`.
- [ ] **Step 2: Verify** shrine shield reduces first-fight damage in a `play` run; `./build.sh` OK.
- [ ] **Step 3: Commit** `git commit -am "Phase 3: nextshield carries a shrine blessing into combat"`

**Phase 3 acceptance:** all 10 room types reachable and crash-free; chest loot + mimic work;
shrine blessings take effect.

---

## PHASE 4 — Gear effects: thorns + evasion

### Task 4.1: Effect data into the gear data files

**Files:** Modify `build/gen.py` (`gen_boot` armor/trinket writers to append `efftype`,`effval`;
`read_record` consumers; `emit_recompute_equip`); `build/content.py` (set effect fields on
chosen items).

- [ ] **Step 1:** In `gen_boot`, extend the armor record to include `efftype,effval` (new trailing
  fields) and likewise trinkets. Update any index-based reads of those records accordingly.
- [ ] **Step 2:** Set `(efftype,effval)` on the new items: a Thornmail (armor, type 1, ~25%),
  a Mistcloak (armor, type 2, ~20%), a Bramble Ring (trinket, type 1), a Phantom Charm (trinket,
  type 2). Tune values.
- [ ] **Step 3: Verify** `./build.sh` compiled OK; armor/trinket data files contain the new
  trailing fields (`grep` the emitted record count per row, or print via a debug run).
- [ ] **Step 4: Commit** `git commit -am "Phase 4: carry thorns/evasion effect fields on gear"`

### Task 4.2: Aggregate eqthorns / eqevade

**Files:** Modify `build/gen.py` (`FIELDS` add `eqthorns`,`eqevade`; `gen_initclass` init 0;
`emit_recompute_equip` sum from armor+trinket records, clamp evade ≤ 60).

- [ ] **Step 1:** Add fields + init. In `emit_recompute_equip`, after reading armor & trinket
  records, compute `eqthorns`/`eqevade` by summing each item's effval when its efftype matches,
  then clamp evade.
- [ ] **Step 2: Verify** `./build.sh` compiled OK; equipping a Mistcloak sets a non-zero evade
  (confirm via a debug print or by the dodge behavior in 4.3).
- [ ] **Step 3: Commit** `git commit -am "Phase 4: aggregate eqthorns/eqevade on equip/load"`

### Task 4.3: Combat hooks (both loops)

**Files:** Modify `build/gen.py` (`combat_loop` and `boss_loop` `enemy_turn`→`strike`).

- [ ] **Step 1: Evasion.** Before the player takes damage, roll `rnd(100) < eqevade`; on dodge,
  print `[evasion] you slip aside — the blow misses!` and skip the `hp -= edmg` and post-ability.
  Implement by computing `edmg` then gating its application on a `dodged` bool.
- [ ] **Step 2: Thorns.** After `hp -= edmg` (non-dodged), `reflect = edmg*eqthorns/100`; if
  `>0`, `ehp -= reflect` and print `[thorns] N reflected.`
- [ ] **Step 3: Verify** equip Thornmail / Mistcloak via a scripted run and confirm reflect/dodge
  lines appear in combat; `./build.sh` OK.
- [ ] **Step 4: Commit** `git commit -am "Phase 4: evasion (dodge) + thorns (reflect) in combat"`

**Phase 4 acceptance:** equipping a thorns item reflects damage; an evasion item sometimes dodges;
both work in normal and boss fights.

---

## PHASE 5 — Difficulty +20%

### Task 5.1: DIFFICULTY scalar on enemy/boss HP & ATK

**Files:** Modify `build/gen.py` (top-level constant; `gen_boot` monster + boss table writers).

- [ ] **Step 1:** Add `DIFFICULTY = 1.2` near the top of `gen.py`. In `gen_boot`, when building
  the monster table, write `round(hp*DIFFICULTY)` and `round(atk*DIFFICULTY)` (leave def/xp/gold).
  Same for the boss table's hp/atk.
- [ ] **Step 2: Verify** emitted data files show scaled values:
```bash
./build.sh && head -c 200 vodran_mon.dat   # first monster hp/atk ≈ 1.2x base
```
  Expected: e.g. base Goblin Skulker hp 12 → 14, atk 5 → 6.
- [ ] **Step 3: Commit** `git commit -am "Phase 5: DIFFICULTY=1.2 scales enemy/boss hp+atk"`

### Task 5.2: Balance read + README touch-up

**Files:** Modify `README.md` (acts/floors/bosses counts, controls note if needed).

- [ ] **Step 1:** Update README stats table (3→10 bosses, 12→30 floors, gear/effect counts, run
  length) and the act list.
- [ ] **Step 2:** Play a short descent; sanity-check early fights are tougher but winnable.
- [ ] **Step 3: Commit** `git commit -am "Phase 5: update README for the expanded campaign"`

**Phase 5 acceptance:** emitted enemy/boss hp+atk ≈ 1.2× base; README reflects the new scope.

---

## Self-review (against the spec)

- **Spec A (structure)** → Tasks 1.1–1.4 ✓ (act math, cadence, bossmask, final check, stretch).
- **Spec B (story)** → Tasks 2.1–2.2 ✓ (regions, 10 bosses).
- **Spec C (bestiary/gear)** → Tasks 2.3–2.4 ✓.
- **Spec D (rooms)** → Tasks 3.1, 3.3 ✓.
- **Spec E (chests)** → Task 3.2 ✓.
- **Spec F (effects)** → Tasks 4.1–4.3 ✓.
- **Spec G (difficulty)** → Task 5.1 ✓.
- **Spec H (schema/fields)** → covered across 1.2 (bossmask), 2.4 (gate), 3.4 (nextshield),
  4.1–4.2 (efftype/effval, eqthorns/eqevade). ✓
- **Spec I (phasing)** → matches Phases 1–5. ✓

**Type/name consistency:** field names used consistently — `bossmask`, `nextshield`, `eqthorns`,
`eqevade`; gear `gate` at weapon idx 6 / armor idx 7 / trinket idx 7; effect fields trailing.
Room functions named `room_chest/room_shrine/room_hazard/room_ambush/room_lore` throughout.

**Note on data tasks:** content-authoring steps (2.1–2.4, 3.3 text) specify schema + curve +
examples + an acceptance check rather than enumerating every row — the rows are written during
execution against those acceptance checks (enumerating ~100 monsters/gear here would duplicate the
implementation). All *logic* steps include exact code.
