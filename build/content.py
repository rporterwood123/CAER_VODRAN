#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
content.py — all CAER VODRAN game content as plain data.

The generator (gen.py) turns this into ActionC data files + dispatch code.
Schemas are documented inline. Ids are positional unless noted.

Delimiters used in emitted data files: records '|', fields '#'. Keep ALL text
free of '|', '#', backslash and double-quote (gen sanitizes, but stay clean).
"""

# ============================================================ CLASSES (3)
# cls id -> profile. Three attacks each; a2 is the class's UNIQUE signature.
# growth = per-level stat gains. wkind = which weapon table the class uses.
CLASSES = {
    0: dict(
        name="Fighter", res="Stamina", wkind="blade",
        maxhp=36, maxres=12, st=8, it=2, dx=4, df=5,
        ghp=7, gres=2, gst=3, git=1, gdx=2, gdf=2,
        sigcost=5, utilcost=3,
        a1="Strike", a2="Cleave", a3="Shield Bash",
        a1d="a dependable swing", a2d="a brutal arc that staggers (unique)",
        a3d="bash to stun and raise your guard",
        blurb="Iron and stamina. Cleave fells the wounded and staggers the strong."),
    1: dict(
        name="Mage", res="Mana", wkind="staff",
        maxhp=22, maxres=24, st=3, it=9, dx=5, df=2,
        ghp=4, gres=5, gst=1, git=3, gdx=2, gdf=1,
        sigcost=8, utilcost=4,
        a1="Firebolt", a2="Arcane Surge", a3="Hex",
        a1d="a cheap bolt of flame", a2d="raw star-fire scaling with INT (unique)",
        a3d="weaken the foe's attacks",
        blurb="Star-fire and mana. Arcane Surge unmakes foes; Hex blunts their fury."),
    2: dict(
        name="Rogue", res="Energy", wkind="fang",
        maxhp=28, maxres=16, st=5, it=4, dx=9, df=3,
        ghp=5, gres=4, gst=2, git=2, gdx=3, gdf=1,
        sigcost=6, utilcost=4,
        a1="Slash", a2="Backstab", a3="Poison Strike",
        a1d="a quick cut", a2d="a shadow strike with huge crit (unique)",
        a3d="envenom the blade for lingering rot",
        blurb="Shadow and crit. Backstab ends fights early; poison does the rest."),
}

# ============================================================ WEAPONS (12 per class = 36)
# (name, tier, material, power, price, special)
#   tier: normal / magic / unique
#   special: 0 none | 1 lifesteal | 2 crit+ | 3 resregen | 4 doublestrike | 5 brand(+dmg) | 6 executioner
WEAPONS = {
    0: [  # Fighter - blades, axes, maces
        ("Rusted Shortsword", "normal", "iron",     4,   0, 0),
        ("Iron Longsword",    "normal", "iron",     6,  40, 0),
        ("Woodsman Axe",      "normal", "iron",     7,  55, 0),
        ("Soldier Mace",      "normal", "iron",     8,  70, 0),
        ("Knight Broadsword",  "magic",  "iron",    10, 120, 5),
        ("Mirewood Halberd",   "magic",  "iron",    12, 160, 0),
        ("Warden Greataxe",    "magic",  "iron",    14, 210, 6),
        ("Ironheart Maul",     "magic",  "iron",    16, 260, 0),
        ("Mythril Saber",      "magic",  "mythril", 18, 340, 2),
        ("Bloodthorn Blade",   "unique", "mythril", 20, 460, 1),
        ("Doomforge Greatsword","unique","mythril", 24, 600, 6),
        ("Starfell Warhammer", "unique", "star",    30, 999, 5),
    ],
    1: [  # Mage - staves, wands, orbs
        ("Cracked Wand",      "normal", "wood",     4,   0, 0),
        ("Apprentice Staff",  "normal", "wood",     6,  40, 3),
        ("Birch Cane",        "normal", "wood",     7,  55, 0),
        ("Bone Focus",        "normal", "bone",     8,  70, 0),
        ("Runed Staff",       "magic",  "iron",     10, 120, 3),
        ("Frostglass Wand",   "magic",  "mythril",  12, 160, 5),
        ("Emberheart Rod",    "magic",  "iron",     14, 210, 5),
        ("Mythril Scepter",   "magic",  "mythril",  16, 260, 2),
        ("Choirglass Orb",    "magic",  "mythril",  18, 340, 3),
        ("Whispering Tome",   "unique", "bone",     20, 460, 3),
        ("Voidcaller Staff",  "unique", "star",     24, 600, 5),
        ("Starlit Scepter",   "unique", "star",     30, 999, 2),
    ],
    2: [  # Rogue - daggers, knives, bows
        ("Bent Dagger",       "normal", "iron",     4,   0, 0),
        ("Hunting Knife",     "normal", "iron",     6,  40, 0),
        ("Twin Shivs",        "normal", "iron",     7,  55, 4),
        ("Leather Dirk",      "normal", "leather",  8,  70, 0),
        ("Poisoner Kris",     "magic",  "iron",     10, 120, 0),
        ("Shadowsteel Dagger","magic",  "mythril",  12, 160, 2),
        ("Duelist Rapier",    "magic",  "iron",     14, 210, 4),
        ("Mythril Stiletto",  "magic",  "mythril",  16, 260, 2),
        ("Mirewood Longbow",  "magic",  "wood",     18, 340, 0),
        ("Nightfang",         "unique", "mythril",  20, 460, 1),
        ("Whisperwind Blades","unique", "mythril",  24, 600, 4),
        ("Starpiercer",       "unique", "star",     30, 999, 2),
    ],
}
WEAPON_SPECIAL_NAME = {
    0: "", 1: "lifesteal", 2: "+crit", 3: "regen", 4: "double", 5: "brand", 6: "execute",
}

# ============================================================ ARMOR (materials x tiers)
# (name, material, tier, defbonus, hpbonus, resbonus, price)
ARMORS = [
    ("Traveler Clothes",  "cloth",   "normal", 0,  0,  0,   0),
    ("Tattered Leathers", "leather", "normal", 2,  4,  0,  50),
    ("Padded Vest",       "leather", "normal", 3,  6,  2,  90),
    ("Studded Leather",   "leather", "magic",  5, 10,  4, 170),
    ("Iron Mail",         "iron",    "normal", 6, 14,  0, 200),
    ("Knight Plate",      "iron",    "magic",  9, 22,  0, 320),
    ("Apprentice Robe",   "cloth",   "normal", 1,  4, 10,  90),
    ("Runed Robe",        "cloth",   "magic",  3,  8, 22, 260),
    ("Shadowweave",       "leather", "unique", 7, 16, 10, 420),
    ("Wardenplate",       "iron",    "unique", 13, 34, 0, 520),
    ("Mythril Hauberk",   "mythril", "magic",  11, 26, 8, 480),
    ("Choir Vestments",   "cloth",   "unique", 6, 18, 30, 540),
    ("Starforged Mail",   "star",    "unique", 16, 44, 14, 980),
]

# ============================================================ TRINKETS
# (name, tier, hpbonus, resbonus, defbonus, critbonus, price)
TRINKETS = [
    ("Cracked Charm",      "normal", 0,  0, 0, 0,   0),
    ("Ring of Vigor",      "normal", 12, 0, 0, 0, 110),
    ("Amulet of Focus",    "normal", 0, 12, 0, 0, 110),
    ("Band of the Cat",    "magic",  0,  0, 0, 12,180),
    ("Aegis Signet",       "magic",  6,  0, 4, 0, 220),
    ("Sanguine Locket",    "magic", 18,  0, 0, 5, 300),
    ("Mythril Torc",       "magic", 10, 10, 3, 4, 360),
    ("Starseed Pendant",   "unique",20, 16, 4, 10,720),
]

# ============================================================ CONSUMABLES
# field name, label, price, effect note. Counts tracked as Game fields.
CONSUMABLES = [
    ("potHeal", "Mirewine Draught", 12, "+24 HP"),
    ("potRes",  "Star Tonic",       14, "+18 resource"),
    ("potCure", "Antidote",         10, "cures poison"),
    ("bombs",   "Thornbomb",        20, "20 unblockable damage"),
    ("elixir",  "Greater Elixir",   60, "full HP and resource"),
]

# ============================================================ MONSTER RACES (12)
RACES = ["Goblin", "Beast", "Hollow", "Spider", "Thornkin", "Wraith",
         "Gargoyle", "Cultist", "Ooze", "Voidspawn", "Ratkin", "Bone"]

# ============================================================ NPC MONSTERS (50)
# (name, race, hp, atk, def, xp, gold, abil, minfloor, maxfloor)
#   abil: 0 none |1 poison |2 selfheal |3 enrage |4 shield |5 crit |6 drain(res) |7 weaken-you
MONSTERS = [
    # --- Act I (floors 1-4) ---
    ("Goblin Skulker",   "Goblin",  12, 5, 1,  8,  5, 0, 1, 2),
    ("Goblin Cutter",    "Goblin",  16, 6, 2, 10,  6, 0, 1, 3),
    ("Goblin Shaman",    "Goblin",  14, 5, 1, 12,  9, 2, 1, 3),
    ("Hobgoblin Brute",  "Goblin",  24, 8, 3, 16, 10, 3, 2, 4),
    ("Goblin Warlord",   "Goblin",  30, 9, 4, 22, 16, 3, 3, 4),
    ("Mire Wolf",        "Beast",   16, 6, 1, 11,  4, 0, 1, 2),
    ("Rabid Direwolf",   "Beast",   22, 8, 2, 15,  6, 3, 2, 4),
    ("Gloomstalker",     "Beast",   20, 9, 2, 16,  7, 5, 2, 4),
    ("Thornback Boar",   "Beast",   28, 7, 5, 17, 10, 0, 2, 4),
    ("Mirewood Broodling","Spider", 10, 5, 1,  9,  5, 1, 1, 2),
    ("Cave Spider",      "Spider",  15, 6, 2, 12,  6, 1, 1, 3),
    ("Venomfang Spider", "Spider",  20, 7, 2, 16,  9, 1, 3, 5),
    ("Sapling Thornkin", "Thornkin",18, 4, 4, 12,  7, 0, 1, 3),
    ("Bramble Lurker",   "Thornkin",26, 6, 6, 17, 10, 4, 2, 4),
    ("Mire Rat",         "Ratkin",   8, 4, 0,  6,  3, 0, 1, 2),
    ("Plague Ratkin",    "Ratkin",  14, 6, 1, 11,  5, 1, 1, 3),
    ("Ratkin Cutthroat", "Ratkin",  18, 8, 2, 15,  9, 5, 2, 4),
    ("Bog Lurcher",      "Beast",   34, 8, 5, 24, 14, 0, 3, 4),
    # --- Act II (floors 5-8) ---
    ("Hollow Footman",   "Hollow",  30, 9, 4, 20, 12, 0, 4, 6),
    ("Hollow Pikeman",   "Hollow",  38, 11,5, 26, 14, 0, 5, 7),
    ("Hollow Knight",    "Hollow",  52, 14,8, 36, 22, 4, 5, 8),
    ("Hollow Captain",   "Hollow",  64, 16,9, 46, 30, 3, 6, 8),
    ("Risen Archer",     "Hollow",  34, 13,3, 28, 16, 5, 5, 7),
    ("Stone Gargoyle",   "Gargoyle",46, 12,10,34, 18, 4, 4, 7),
    ("Grinning Gargoyle","Gargoyle",40, 15,7, 36, 20, 5, 5, 8),
    ("Chapel Sentinel",  "Gargoyle",60, 14,12,44, 26, 4, 6, 8),
    ("Crown Acolyte",    "Cultist", 36, 12,4, 30, 20, 2, 4, 6),
    ("Crown Zealot",     "Cultist", 44, 15,5, 38, 24, 3, 5, 8),
    ("Hollow Choirsinger","Cultist",40, 13,5, 40, 28, 6, 6, 8),
    ("Whisper Cultist",  "Cultist", 38, 14,4, 38, 26, 7, 5, 8),
    ("Shade",            "Wraith",  32, 14,2, 32, 14, 6, 4, 7),
    ("Gloom Wraith",     "Wraith",  44, 17,4, 42, 22, 6, 5, 8),
    ("Bannshee",         "Wraith",  40, 16,3, 44, 24, 7, 6, 8),
    ("Lurkfang Spider",  "Spider",  46, 13,5, 36, 18, 1, 5, 7),
    ("Thorn Horror",     "Thornkin",58, 12,9, 42, 22, 2, 5, 8),
    ("Bonepile Shambler","Bone",    50, 12,6, 38, 18, 2, 4, 7),
    # --- Act III (floors 9-12) ---
    ("Voidspawn Imp",    "Voidspawn",54, 18,5, 50, 24, 5, 8, 10),
    ("Void Howler",      "Voidspawn",66, 22,6, 60, 28, 3, 9, 11),
    ("Star Devourer",    "Voidspawn",90, 26,9, 80, 40, 6, 10,12),
    ("Hollow Revenant",  "Hollow",   84, 24,12,74, 38, 4, 9, 12),
    ("Dread Knight",     "Hollow",  104, 27,14,92, 50, 3, 10,12),
    ("Star-Ooze",        "Ooze",     70, 16,4, 52, 22, 2, 8, 10),
    ("Devouring Slime",  "Ooze",     96, 20,6, 68, 30, 2, 9, 12),
    ("Crown Archpriest", "Cultist",  80, 24,8, 78, 46, 2, 9, 12),
    ("Void Wraith",      "Wraith",   72, 26,6, 74, 36, 6, 9, 12),
    ("Bone Colossus",    "Bone",    120, 22,16,96, 50, 4, 10,12),
    ("Gravecaller",      "Bone",     76, 21,8, 70, 34, 2, 8, 11),
    ("Crystalback Gargoyle","Gargoyle",100,24,15,90,48,4,10,12),
    ("Mirror Horror",    "Voidspawn",88, 28,8, 88, 44, 7, 10,12),
    ("Starless Stalker", "Beast",    78, 30,6, 84, 40, 5, 9, 12),
]

# ============================================================ BOSSES (10)
# Each: dict with stats, lore, and phase thresholds (% hp) -> behavior tags.
# behaviors: harden(+def) | summon(heal) | shift(element flavor) | choir(heal) |
#            silence(drain) | void(big hit) | enrage(+atk)
BOSSES = [
    dict(
        id=0, name="The Mire-Warden", floor=3, act=1,
        hp=110, atk=11, df=5, xp=120, gold=110,
        intro=[
            "The reeds part on a ring of standing roots. They knot together, rise,",
            "and take up a crown of black thorn and a drowned knight's rusted arms.",
            "THE MIRE-WARDEN wakes to keep a watch older than the kingdom."],
        defeat=[
            "The Warden sloughs apart into wet timber and old steel.",
            "Where its heart should be, a cold seed pulses once, then stills.",
            "Beyond the roots, the first true stone of Caer Vodran stands waiting."],
        drop="Studded Leather",
        phase2=60, phase3=30,
        p2="The Warden's limbs lash faster - bark splits to bare cruel thorns! (attack rises)",
        p3="It drags sap from the bog to knit its wounds and flails in a frenzy! (heals + rages)"),
    dict(
        id=1, name="The Gate-Golem", floor=6, act=2,
        hp=155, atk=13, df=6, xp=170, gold=150,
        intro=[
            "The half-fallen portcullis grinds. The gatehouse stone itself stands up,",
            "iron grate for a face, a century of siege ground into its fists.",
            "THE GATE-GOLEM bars the way, as it was built and broken to do."],
        defeat=[
            "The golem comes apart at its old fracture lines, slab by slab,",
            "and settles into rubble that finally stops pretending to be a wall.",
            "The inner courts lie open, ankle-deep in black water."],
        drop="Iron Mail",
        phase2=60, phase3=30,
        p2="The golem sheds its broken plates and strikes with bare, faster fists! (attack rises)",
        p3="It hauls fallen stone into itself, re-forming as it pounds you! (heals + rages)"),
    dict(
        id=2, name="Sir Caedric, the Drowned", floor=9, act=3,
        hp=215, atk=15, df=7, xp=240, gold=200,
        intro=[
            "A knight stands waist-deep in the flooded ward, water pouring endlessly",
            "from the joints of his armor. He does not breathe; he has not, in an age.",
            "SIR CAEDRIC, last captain of the wards, lifts a blade gone green with rot."],
        defeat=[
            "Caedric sinks to one knee, and the water finally takes him under for good.",
            "His blade slips from a hand that seems, for a moment, almost grateful.",
            "A cold stair winds deeper, into the castle's hollowed heart."],
        drop="Aegis Signet",
        phase2=60, phase3=30,
        p2="Caedric remembers how to kill, and the drowned blade quickens! (attack rises)",
        p3="The flood pours back into his wounds and he fights past death! (heals + rages)"),
    dict(
        id=3, name="The Echo", floor=12, act=4,
        hp=290, atk=17, df=8, xp=320, gold=260,
        intro=[
            "The gallery is empty, but the air is crowded. Every step you have taken",
            "comes back wrong, and the wrongness gathers into a shape made of sound -",
            "THE ECHO, the hall's memory of every soldier who ever screamed here."],
        defeat=[
            "The Echo comes apart into a hundred fading cries, then silence -",
            "a real silence, the first this hall has held in a hundred years.",
            "Drowned shelves and floating pages glimmer in the dark below."],
        drop="Knight Plate",
        phase2=60, phase3=30,
        p2="The Echo layers a hundred voices into one and strikes louder! (attack rises)",
        p3="It swallows your own cries to mend itself and shrieks anew! (heals + rages)"),
    dict(
        id=4, name="The Ink-Wraith", floor=15, act=5,
        hp=375, atk=19, df=9, xp=420, gold=320,
        intro=[
            "Among the drowned stacks, the spilled ink of ten thousand books has",
            "learned to stand. It wears a reader's robe and a face of running text,",
            "THE INK-WRAITH, the Archive's last and maddest librarian."],
        defeat=[
            "The wraith unspools into a slick of black water and ruined words",
            "that finally, mercifully, mean nothing at all.",
            "A cartographer's vault gapes beyond, hung with maps of a lost sky."],
        drop="Runed Robe",
        phase2=60, phase3=30,
        p2="The Ink-Wraith rewrites itself sharper, and its lashings quicken! (attack rises)",
        p3="It drinks the drowned library to refill its veins and rages! (heals + rages)"),
    dict(
        id=5, name="Captain Vurm, Unfallen", floor=18, act=6,
        hp=460, atk=22, df=10, xp=540, gold=390,
        intro=[
            "The muster yard rings with a horn that has no horn. From the cot-rows of",
            "grey ash rises a captain in fused armor, his garrison forming up behind.",
            "CAPTAIN VURM never stood down, and will not, until you make him."],
        defeat=[
            "Vurm salutes - whether you or some long-dead king, you cannot tell -",
            "and crumbles, and his soldiers with him, back into quiet ash.",
            "A nave opens ahead, holding one long, breathless note."],
        drop="Wardenplate",
        phase2=60, phase3=30,
        p2="Vurm calls the cadence and his strikes fall faster, harder! (attack rises)",
        p3="The ash of his garrison pours into him and he fights renewed! (heals + rages)"),
    dict(
        id=6, name="Maerith of the Choir", floor=21, act=7,
        hp=560, atk=25, df=11, xp=680, gold=470,
        intro=[
            "A hundred hollow mouths hum one endless note. At the altar floats MAERITH,",
            "once Archmage of Aelthmoor, now a chorus of stolen voices wearing her shape.",
            "She turns her eyeless face toward you, and the choir inhales."],
        defeat=[
            "The choir falls silent, voice by voice, until only Maerith remains -",
            "and then she, too, unravels into drifting motes of cold light.",
            "The stone past the altar turns to black glass, and remembers the sky."],
        drop="Choir Vestments",
        phase2=66, phase3=33,
        p2="Maerith conducts the choir to a shriek and strikes on the beat! (attack rises)",
        p3="She draws the choir's breath into her wounds and sings on! (heals + rages)"),
    dict(
        id=7, name="The Crystalline Horror", floor=24, act=8,
        hp=650, atk=28, df=12, xp=820, gold=560,
        intro=[
            "The geode sings at your heartbeat. Then the crystal answers with its own,",
            "tearing free of the walls into a thing of black glass and trapped starlight.",
            "THE CRYSTALLINE HORROR unfolds, refracting a hundred wrong reflections of you."],
        defeat=[
            "The Horror shatters into a slow rain of singing glass that dims, and dies.",
            "In the quiet after, a single shard of true star-stuff floats, waiting.",
            "Below, the whisper takes on a voice you know far too well."],
        drop="Mythril Hauberk",
        phase2=60, phase3=30,
        p2="The Horror sharpens every facet and its blows come quicker! (attack rises)",
        p3="It draws light from the geode to mend its cracks and rages! (heals + rages)"),
    dict(
        id=8, name="The Mirror", floor=27, act=9,
        hp=730, atk=31, df=13, xp=980, gold=660,
        intro=[
            "Something steps out of the still black water wearing your face, your scars,",
            "your blade - and your every habit of fighting, learned by watching you fall.",
            "THE MIRROR smiles with your mouth and says your name in the whisper's voice."],
        defeat=[
            "You strike the blow you would never have seen coming, because it is yours.",
            "The Mirror cracks, and the thing behind your face flees back into the dark.",
            "Only the Last Stair remains, warm and faintly breathing, leading down."],
        drop="Shadowweave",
        phase2=60, phase3=30,
        p2="The Mirror learns your tempo and turns your own speed against you! (attack rises)",
        p3="It heals the way you would have and fights with your desperation! (heals + rages)"),
    dict(
        id=9, name="Vodran, the Sunken Crown", floor=30, act=10,
        hp=820, atk=34, df=15, xp=1500, gold=800,
        intro=[
            "The vault is a wound in the world. At its center turns the Sunken Crown:",
            "a fallen star folded into a ring of black gold, whispering your name in a",
            "voice you have always known. VODRAN opens like an eye, and the dark leans in."],
        defeat=[
            "You drive the final blow into the heart of the star. The whisper",
            "stutters, climbs to a shriek - and goes out, like a candle pinched cold.",
            "The Mirewood exhales. Far above, for the first time in an age, it is morning."],
        drop="Starforged Mail",
        phase2=70, phase3=35,
        p2="Vodran bends the dark - gravity twists and it strikes with terrible weight! (attack rises)",
        p3="The star UNFOLDS. Reality thins. Vodran lashes with void and mends itself! (heals + rages)"),
]

# ============================================================ FLOORS (30, 10 acts)
# (name, act, rooms, flavor) ; every 3rd floor (3,6,...,30) culminates in the act boss.
FLOORS = [
    # Act 1 - The Drowned Mire
    ("Black Lily Causeway",        1, 5, "Black water and crooked birches; the causeway sinks with every step."),
    ("The Sunken Track",           1, 5, "A road the bog swallowed whole. Marshlights bob over it, and lie."),
    ("Mire-Warden's Reach",        1, 4, "The reeds open on a ring of standing roots. Something tall begins to unfold."),
    # Act 2 - The Broken Barbican
    ("The Shattered Gatehouse",    2, 5, "Outerworks ground to rubble and bramble - the first true stone of Caer Vodran."),
    ("Murder-Hole Walk",           2, 6, "A gauntlet of dark slots overhead. The walls have not forgotten their purpose."),
    ("The Portcullis Yard",        2, 4, "An iron grate hangs half-fallen. Beyond it, something vast tests the chains."),
    # Act 3 - The Flooded Wards
    ("The Drowned Courtyard",      3, 5, "Black water across a parade ground; armor rusts where men once stood."),
    ("The Weeping Cloister",       3, 6, "Arched walks running with cold water that tastes of old grief."),
    ("The Reflecting Cells",       3, 4, "Flooded oubliettes. Your face stares back from every pool, a beat too late."),
    # Act 4 - The Hollow Halls
    ("The Hall of Banners",        4, 5, "Rotted standards of a dead court. The armor beneath them stands too straight."),
    ("The Cold Hearth",            4, 6, "A great hall whose fires went out an age ago. The cold here listens."),
    ("The Echoing Gallery",        4, 4, "A long gallery that answers every sound with a voice not quite yours."),
    # Act 5 - The Sunken Archive
    ("The Drowned Stacks",         5, 5, "Shelves to the dark ceiling, pages dissolving into ink-black water."),
    ("The Ink-Dark Reading Room",  5, 6, "Tables set for scholars centuries gone. The ink still moves."),
    ("The Cartographer's Vault",   5, 4, "Maps of a sky no one remembers. Something has been redrawing them."),
    # Act 6 - The Ash Barracks
    ("The Rusted Armory",          6, 5, "Racks of weapons fused with rust and ash. The garrison kept its edge too long."),
    ("The Cot-Rows",               6, 6, "Endless cots of grey ash in the shape of sleeping men. Some still turn over."),
    ("The Muster Yard",            6, 4, "A drill-square scarred by ten thousand marches. A horn sounds that has no horn."),
    # Act 7 - The Chapel of the Choir
    ("The Nave of Held Breath",    7, 5, "A vaulted nave where the silence is a held note, waiting to break."),
    ("The Reliquary",              7, 6, "Shrines to a saint who became a sound. The relics hum when you near them."),
    ("The High Choir",             7, 4, "A hundred hollow mouths turn toward you as one, and inhale."),
    # Act 8 - The Star-Crypt
    ("The Black-Crystal Threshold",8, 5, "The stone turns to black glass that remembers being sky."),
    ("The Singing Geode",          8, 6, "A cavern of crystal that rings at your heartbeat and answers with its own."),
    ("The Crypt of Sky",           8, 4, "A vault where a shard of the fallen star is entombed, and dreaming."),
    # Act 9 - The Whispering Deep
    ("The Hall of Your Voice",     9, 5, "The whisper has your cadence now. It greets you by a name only you know."),
    ("The Drowned Mirror",         9, 6, "Still black water that shows you doing things you have not done. Yet."),
    ("The Threshold of Self",      9, 4, "Something steps out of the dark wearing your face, your scars, your blade."),
    # Act 10 - The Vault of the Crown
    ("The Last Stair",            10, 5, "Steps cut into the wound of the world, warm and faintly breathing."),
    ("The Antechamber of the Star",10,6, "Light with no source. Gravity leans toward the center like a held breath."),
    ("The Vault of the Crown",    10, 4, "The heart of it all. The Sunken Crown turns on nothing, and says your name."),
]

# ============================================================ EVENTS (non-combat rooms)
# Each: title, desc lines, and 2-3 choices. choice effect codes handled in gen.py:
#   heal+N, res+N, gold+N, gold-N, hp-N, item:potHeal, xp+N, weakencurse, nothing,
#   trinketchance, fullheal
EVENTS = [
    dict(key="shrine", title="A Cracked Shrine",
         desc=["A weathered shrine to a forgotten saint leans in an alcove,",
               "its basin holding a finger of cold, clear water."],
         choices=[("Drink (restore HP and resource)", "fullheal"),
                  ("Pry loose the silver inlay (gold, but it feels wrong)", "gold+40 curse"),
                  ("Leave it be", "nothing")]),
    dict(key="chest", title="A Bound Chest",
         desc=["A war-chest sits in the muck, its lock long rusted through.",
               "It might hold coin - or a trap."],
         choices=[("Force it open", "chestloot"),
                  ("Search carefully first (safer, less)", "gold+25"),
                  ("Walk away", "nothing")]),
    dict(key="corpse", title="A Fallen Delver",
         desc=["Another adventurer, weeks dead, slumped against the wall.",
               "Their pack is half-spilled across the stones."],
         choices=[("Take their potions", "item:potHeal2"),
                  ("Take their coin and trinket", "gold+30 trinketchance"),
                  ("Say a word and move on", "xp+15")]),
    dict(key="font", title="A Whispering Font",
         desc=["A basin of starlit water murmurs in a voice almost like yours.",
               "Drinking from it might sharpen the mind - or invite the whisper in."],
         choices=[("Drink deep (restore resource, small risk)", "res+99 riskcurse"),
                  ("Wet your blade in it (+4 damage on your next 8 hits)", "brandbuff"),
                  ("Refuse the whisper", "nothing")]),
    dict(key="merchant", title="The Mire-Pedlar",
         desc=["A hunched figure in a coat of stitched hides waves a lantern.",
               "'Down here too? Brave. Or stupid. I have wares either way.'"],
         choices=[("Trade (open shop)", "shop"),
                  ("Ask the way (small XP)", "xp+20"),
                  ("Move on", "nothing")]),
    dict(key="trap", title="A Tripwire",
         desc=["Your boot snags a near-invisible thread strung across the dark.",
               "Somewhere above, a counterweight drops."],
         choices=[("Dive forward", "trapdodge"),
                  ("Freeze", "hp-12")]),
]

# misc lore shown at act transitions
ACT_INTRO = {
    1: ["ACT I  -  THE DROWNED MIRE",
        "They say a star fell on Aelthmoor and the castle drank it. They say the",
        "Mirewood grew to seal the wound. You go where the saying ends."],
    2: ["ACT II  -  THE BROKEN BARBICAN",
        "The bog is behind you; the first real stone is not. Caer Vodran's outer",
        "wall still stands its watch, though nothing living mans it."],
    3: ["ACT III  -  THE FLOODED WARDS",
        "Inside the wall, the courts lie under black water to the knee. The garrison",
        "drowned at its posts an age ago, and has not yet noticed."],
    4: ["ACT IV  -  THE HOLLOW HALLS",
        "The keep proper. Banners rot, hearths are cold, and the halls are not empty -",
        "they are occupied, by everything they used to be, hollowed and humming."],
    5: ["ACT V  -  THE SUNKEN ARCHIVE",
        "Down into the drowned library, where the kingdom kept what it knew of the star.",
        "The water here is more ink than water, and the ink has opinions."],
    6: ["ACT VI  -  THE ASH BARRACKS",
        "The garrison that held the inner keep never stood down, never burned out -",
        "it only banked itself in ash, and waits in ranks for an order that never came."],
    7: ["ACT VII  -  THE CHAPEL OF THE CHOIR",
        "Here the court tried to pray the whisper away, and the whisper answered.",
        "It took their voices first, and then the rest of them, one held note at a time."],
    8: ["ACT VIII  -  THE STAR-CRYPT",
        "Below the chapel the stone gives way to black crystal that remembers being sky.",
        "A shard of the fallen star is buried here, and it is not buried deeply enough."],
    9: ["ACT IX  -  THE WHISPERING DEEP",
        "The whisper is a wind now, and it has learned your cadence, your name, your face.",
        "Down here, your own thoughts answer you - and they are not always on your side."],
    10:["ACT X  -  THE VAULT OF THE CROWN",
        "Below the foundations the stone goes warm and the angles go wrong.",
        "The whisper has a direction now. It is down. It has always been down."],
}

INTRO = [
    "Long ago a star fell on the kingdom of Aelthmoor, and the court built",
    "Caer Vodran to cage its light. The mages called the shard the Sunken Crown,",
    "and tried to wear it. It wore them instead. The castle sank into the Mirewood,",
    "and its halls filled with the changed.",
    "",
    "You are a Delver. The whisper has begun to reach the waking world.",
    "Go down. End it. Or join the choir.",
]

ENDING = [
    "You climb back through a castle gone suddenly, ordinarily quiet:",
    "old stone, old dust, a draft that is only a draft. The thorns at the gate",
    "have withered to ordinary deadwood. Beyond them, the Mirewood is just a wood.",
    "",
    "No one will believe where you have been. That is the price and the mercy of it.",
    "You walk out into the morning, and the morning does not whisper back.",
    "",
    "                    -- CAER VODRAN  -  fin --",
]
