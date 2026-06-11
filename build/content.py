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

# ============================================================ BOSSES (3)
# Each: dict with stats, lore, and phase thresholds (% hp) -> behavior tags.
# behaviors: harden(+def) | summon(heal) | shift(element flavor) | choir(heal) |
#            silence(drain) | void(big hit) | enrage(+atk)
BOSSES = [
    dict(
        id=0, name="The Warden of Thorns", floor=4, act=1,
        hp=130, atk=12, df=6, xp=120, gold=120,
        intro=[
            "The gatehouse groans. Roots tear from the masonry and stand upright,",
            "a knot of bark and rusted armor crowned in black thorns.",
            "THE WARDEN OF THORNS wakes to keep its ancient watch."],
        defeat=[
            "The Warden sloughs apart into wet timber and old steel.",
            "Within its hollow chest: a seed of something colder. The gate yawns open.",
            "Stairs spiral down into the castle proper."],
        drop="Wardenplate",
        phase2=60, phase3=30,
        p2="The Warden hardens its bark. (defense rises)",
        p3="Thorns erupt - the Warden lashes in a frenzy! (attack rises)"),
    dict(
        id=1, name="Maerith, the Hollow Choir", floor=8, act=2,
        hp=300, atk=18, df=8, xp=300, gold=260,
        intro=[
            "The chapel is full. A hundred hollow mouths hum one endless note.",
            "At the altar floats MAERITH, once Archmage of Aelthmoor, now a chorus",
            "of stolen voices wearing her shape. She turns her eyeless face to you."],
        defeat=[
            "The choir falls silent, voice by voice, until only Maerith remains -",
            "and then she, too, unravels into drifting motes of cold light.",
            "Beneath the altar, a stair descends toward a buried star."],
        drop="Choir Vestments",
        phase2=66, phase3=33,
        p2="Maerith draws the choir's breath - the song mends her wounds! (heals)",
        p3="The hollow voices SCREAM, tearing at your focus! (drains resource)"),
    dict(
        id=2, name="Vodran, the Sunken Crown", floor=12, act=3,
        hp=520, atk=24, df=12, xp=999, gold=500,
        intro=[
            "The vault is a wound in the world. At its center turns the Sunken Crown:",
            "a fallen star folded into a ring of black gold, whispering your name in a",
            "voice you have always known. VODRAN opens like an eye, and the dark leans in."],
        defeat=[
            "You drive the final blow into the heart of the star. The whisper",
            "stutters, climbs to a shriek - and goes out, like a candle pinched cold.",
            "The Mirewood exhales. Far above, for the first time in an age, it is morning."],
        drop="Starfell Warhammer",
        phase2=70, phase3=35,
        p2="Vodran bends the dark - gravity twists and it strikes with terrible weight! (attack rises)",
        p3="The star UNFOLDS. Reality thins. Vodran lashes with void and mends itself! (heals + enrages)"),
]

# ============================================================ FLOORS (12, 3 acts)
# (name, act, rooms, flavor) ; floors 4/8/12 culminate in the act boss.
FLOORS = [
    ("The Mirewood Verge",     1, 5, "Black water and crooked birches. The castle is a rumor of stone ahead."),
    ("The Drowned Path",       1, 5, "A causeway half-sunk in bog. Things move beneath the lilies."),
    ("The Broken Barbican",    1, 6, "Shattered outerworks choked in bramble. The gatehouse looms."),
    ("The Thorn Gate",         1, 4, "The great gate, sealed in living thorn. Something guards it."),
    ("The Hollow Hall",        2, 5, "Inside at last. Banners rot on the walls; armor stands too still."),
    ("The Sunken Library",     2, 6, "Drowned shelves and floating pages. Knowledge gone to mildew and worse."),
    ("The Barracks of Ash",    2, 5, "Rows of cots and rusted racks. The garrison never stood down."),
    ("The Choir Chapel",       2, 4, "A vaulted nave humming with a single held note. Maerith waits."),
    ("The Sunken Stair",       3, 5, "Down past the foundations, where the stone turns wrong and warm."),
    ("The Star-Crypt",         3, 6, "A cavern of black crystal. The walls remember being sky."),
    ("The Whispering Deep",    3, 5, "The whisper is a wind now. Your own thoughts answer in its voice."),
    ("The Vault of the Crown", 3, 4, "The heart of the wound. The Sunken Crown turns, and waits for you."),
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
                  ("Wet your blade in it (a charge of power)", "brandbuff"),
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
    1: ["ACT I  -  THE MIREWOOD APPROACH",
        "They say a star fell on Aelthmoor and the castle drank it. They say the",
        "Mirewood grew to seal the wound. You go where the saying ends."],
    2: ["ACT II  -  THE HOLLOW HALLS",
        "The gate is broken behind you. Inside, the castle is not empty - it is",
        "occupied, by everything it used to be, hollowed and humming."],
    3: ["ACT III  -  THE SUNKEN VAULT",
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
