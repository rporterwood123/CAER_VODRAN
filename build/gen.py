#!/usr/bin/env python3
"""
gen.py — generates caer_vodran.actionc (the full game) from content.py.

Architecture (forced by ActionC):
  * One `Game` class holds ALL numeric state in int fields (no globals exist).
  * `main` is the conductor: owns the dispatch loop; the ONLY caller of the Game's
    instance-method "screens" (instance methods cannot call each other). Each screen
    is self-contained and sets `this.mode` for the next.
  * Static methods are pure int math helpers (callable from anywhere).
  * Big string tables live in data files (written by boot), read+split+parsed by id.
"""
import os
import content as C
from emit import Emit, K

OUT = os.path.join(os.path.dirname(__file__), "..", "caer_vodran.actionc")

# ---- modes ----
M_TITLE, M_CLASS, M_CAMP, M_COMBAT = 0, 1, 2, 3
M_GAMEOVER, M_VICTORY, M_EXPLORE, M_EVENT = 4, 5, 6, 7
M_BOSS, M_QUIT, M_SHOP, M_EQUIP, M_ACTINTRO = 8, 9, 10, 11, 12

# ---- data files ----
SAVE = "vodran_save.dat"
MON = "vodran_mon.dat"
WPN = "vodran_wpn.dat"
ARM = "vodran_arm.dat"
TRK = "vodran_trk.dat"
BOSS = "vodran_boss.dat"
POOL = "vodran_pool.dat"

NW = 12  # weapons per class

# ---- all int fields ----
FIELDS = [
    "cls", "level", "xp", "xpnext", "started", "mode",
    "hp", "basehp", "maxhp", "res", "baseres", "maxres",
    "pstr", "pint", "pdex", "pdef", "gold", "kills",
    "wid", "wpow", "wspec", "aid", "tid",
    "eqhp", "eqres", "eqdef", "eqcrit", "ownw", "owna", "ownt",
    "potHeal", "potRes", "potCure", "bombs", "elixir",
    "act", "floor", "room", "roomcount", "b0", "b1", "b2", "shopret", "brandbuff",
    "eid", "ehp", "emaxhp", "eatk", "edef", "eabil",
    "estun", "epoison", "eweak", "pshield", "ppoison", "pweak",
    "isboss", "bossid", "bphase", "pendmon", "pendev",
    "crumb",
]

SAVE_FIELDS = ["cls", "level", "xp", "xpnext", "hp", "basehp", "res", "baseres",
               "pstr", "pint", "pdex", "pdef", "gold", "kills",
               "wid", "aid", "tid", "ownw", "owna", "ownt",
               "potHeal", "potRes", "potCure", "bombs", "elixir",
               "act", "floor", "b0", "b1", "b2"]


# =====================================================================  screen presentation
# Every input screen opens with a clear + a header so each new screen is
# unmistakable. The header is a breadcrumb of the screen you came from plus a
# banner naming the current request. The breadcrumb reads `this.crumb`, an int
# the conductor sets to "the screen we just left" after each dispatch.
CRUMB_SENTINEL = 99  # no previous screen (boot) -> breadcrumb suppressed
CRUMB_NAMES = {
    M_TITLE: "the threshold",
    M_CLASS: "the choosing",
    M_ACTINTRO: "the tale",
    M_CAMP: "Camp",
    M_SHOP: "the Mire-Pedlar",
    M_EQUIP: "your gear",
    M_EXPLORE: "the descent",
    M_COMBAT: "the fight",
    M_BOSS: "the boss",
    M_EVENT: "an omen",
    M_GAMEOVER: "death",
    M_VICTORY: "victory",
}


# ---- ANSI colors. The ESC byte is written via "\x1b" here; safe_str renders it
# as a readable  escape in the source, which the compiler decodes at runtime.
A_RST = "\x1b[0m"
A_RED = "\x1b[31m"
A_GRN = "\x1b[32m"
A_YEL = "\x1b[33m"
A_MAG = "\x1b[35m"
A_CYN = "\x1b[36m"


def col(text, code):
    """Wrap text in an ANSI style+reset. code is an SGR string like '31' or '1;36'."""
    return "\x1b[" + code + "m" + text + A_RST


def clear_screen(e):
    # Real ANSI clear: erase screen + scrollback, home the cursor. The ESC byte
    # is emitted as a  escape (see safe_str) that the compiler decodes.
    e.say("\x1b[2J\x1b[3J\x1b[H")


def crumb_line(e, cur_mode):
    # "  > from:  <prev screen>" unless there is no previous screen, or we just
    # looped back onto the same screen (e.g. room-to-room descent).
    def sw():
        cases = []
        for mode, name in CRUMB_NAMES.items():
            cases.append((mode, (lambda nm: (lambda: e.say(col("  > from:  " + nm, "2"))))(name)))
        e.switch(e.f("crumb"), cases, default=None)
    e.if_cmp(e.f("crumb"), "!=", CRUMB_SENTINEL,
             lambda: e.if_cmp(e.f("crumb"), "!=", cur_mode, sw))


def banner(e, name, request, tone="1;36"):
    # A fixed-width "==== NAME ====" bar (bold cyan by default; tone overridable)
    # with the request on the dim line below.
    title = " " + name + " "
    width = 44
    side = max(1, (width - len(title)) // 2)
    bar = "=" * side + title + "=" * max(1, width - side - len(title))
    e.say("  " + col(bar, tone))
    if request:
        e.say("  " + col(request, "2"))
    e.say_blank()


def stats_line(e):
    # Always-on compact vitals, shown on every screen once a character exists
    # (started==1 keeps it off the title / class-select screens).
    def show():
        e.say_string(e.lit("  " + A_RED + "HP "), e.num(e.f("hp")), e.lit("/"), e.num(e.f("maxhp")), e.lit(A_RST),
                     e.lit("  " + A_CYN + "res "), e.num(e.f("res")), e.lit("/"), e.num(e.f("maxres")), e.lit(A_RST),
                     e.lit("  Lv "), e.num(e.f("level")),
                     e.lit("  " + A_YEL + "Gold "), e.num(e.f("gold")), e.lit(A_RST),
                     e.lit("  Floor "), e.num(e.f("floor")), e.lit("/12"))
        e.say_blank()
    e.if_cmp(e.f("started"), "==", 1, show)


def screen_top(e, name, request, mode, tone="1;36"):
    """Clear the window and draw the breadcrumb + title banner + vitals for a screen."""
    clear_screen(e)
    crumb_line(e, mode)
    banner(e, name, request, tone)
    stats_line(e)


def read_choice(e, var):
    """Read a numbered choice, then always emit a trailing blank line."""
    e.read_into(var)
    e.say_blank()


# ---- floor -> monster id pool (ids whose [minfloor,maxfloor] covers the floor) ----
def floor_pool(f):
    ids = [i for i, m in enumerate(C.MONSTERS) if m[8] <= f <= m[9]]
    return ids if ids else [0]


# =====================================================================  build
def build():
    e = Emit()
    e.comment("================================================================")
    e.comment(" CAER VODRAN - The Sunken Crown        (written in ActionC)")
    e.comment(" A text roguelike. Generated by build/gen.py - do not hand-edit.")
    e.comment("================================================================")
    e.blank()
    gen_class(e)
    gen_main(e)
    gen_helpers(e)
    with open(OUT, "w") as f:
        f.write(e.text())
    print("wrote", os.path.abspath(OUT), "(%d lines)" % len(e.lines))


def gen_class(e):
    e.begin_class("Game")
    for fld in FIELDS:
        e.field(fld)
    e.blank()
    e.ctor()
    e.assign("LOOK AT ME.mode", M_TITLE)
    e.assign("LOOK AT ME.crumb", CRUMB_SENTINEL)
    e.end_ctor()
    e.blank()
    gen_boot(e)
    gen_setcrumb(e)
    gen_title(e)
    gen_class_select(e)
    gen_initclass(e)
    gen_actintro(e)
    gen_camp(e)
    gen_shop(e)
    gen_equip(e)
    gen_explore(e)
    gen_combat(e)
    gen_bossfight(e)
    gen_event(e)
    gen_gameover(e)
    gen_victory(e)
    e.end_class()


# -----------------------------------------------------------------  data emit helpers
def _records(rows):
    return "|".join("#".join(C.__dict__.get('safe', str)(x) if False else str(x) for x in r) for r in rows)


def gen_boot(e):
    e.imethod("boot")
    e.comment("write all data files (idempotent each run)")
    # monsters: name#hp#atk#def#xp#gold#abil
    mon = "|".join("#".join(str(x) for x in (m[0], m[2], m[3], m[4], m[5], m[6], m[7])) for m in C.MONSTERS)
    e.string("dm", e.lit(mon)); e.write_file("dm", MON)
    # weapons (all 36, global id = cls*12+idx): name#tier#mat#power#price#special
    wrows = []
    for cls in (0, 1, 2):
        for w in C.WEAPONS[cls]:
            wrows.append((w[0], w[1], w[2], w[3], w[4], w[5]))
    wp = "|".join("#".join(str(x) for x in r) for r in wrows)
    e.string("dw", e.lit(wp)); e.write_file("dw", WPN)
    # armor: name#mat#tier#def#hp#res#price
    ar = "|".join("#".join(str(x) for x in r) for r in C.ARMORS)
    e.string("da", e.lit(ar)); e.write_file("da", ARM)
    # trinkets: name#tier#hp#res#def#crit#price
    tr = "|".join("#".join(str(x) for x in r) for r in C.TRINKETS)
    e.string("dt", e.lit(tr)); e.write_file("dt", TRK)
    # bosses: name#hp#atk#def#xp#gold
    br = "|".join("#".join(str(x) for x in (b["name"], b["hp"], b["atk"], b["df"], b["xp"], b["gold"])) for b in C.BOSSES)
    e.string("db", e.lit(br)); e.write_file("db", BOSS)
    # per-floor monster id pools (record per floor, ids comma-separated)
    pool = "|".join(",".join(str(i) for i in floor_pool(f)) for f in range(1, 13))
    e.string("dp", e.lit(pool)); e.write_file("dp", POOL)
    e.assign("LOOK AT ME.mode", M_TITLE)
    e.end_imethod()


# conductor calls this after each screen so the next screen can show a breadcrumb
def gen_setcrumb(e):
    e.imethod("setcrumb", args=["n"])
    e.assign("LOOK AT ME.crumb", "n")
    e.end_imethod()


# read record `idx` from a #/|-delimited file; returns the field-array var name.
def read_record(e, path, idx_operand):
    raw = e.tmp("s"); recs = e.tmp("a"); rec = e.tmp("a")
    e.string(raw, e.read_file(path))
    e.split(recs, raw, "|")
    e.split(rec, Emit.aget(recs, idx_operand), "#")
    return rec


# -----------------------------------------------------------------  title
def gen_title(e):
    e.imethod("title")
    clear_screen(e)
    crumb_line(e, M_TITLE)
    e.say_blank()
    e.say("  ================================================================")
    e.say("          C A E R   V O D R A N")
    e.say("                 -- The Sunken Crown --")
    e.say("  ================================================================")
    e.say_blank()
    for ln in C.INTRO:
        e.say("   " + ln) if ln else e.say_blank()
    e.say_blank()
    e.say("   1) New Delve")
    e.declare("hassave", e.file_exists(SAVE))
    e.if_cmp("hassave", "==", 1, lambda: e.say("   2) Continue your delve"))
    e.say("   3) Abandon (quit)")
    e.say_blank()
    e.say("  Enter a number:")
    e.declare("c", 0)
    read_choice(e, "c")
    e.switch("c", [
        (1, lambda: e.assign("LOOK AT ME.mode", M_CLASS)),
        (2, lambda: title_continue(e)),
        (3, lambda: e.assign("LOOK AT ME.mode", M_QUIT)),
    ], default=lambda: e.say("  The gloom does not understand."))
    e.end_imethod()


def title_continue(e):
    e.declare("hs", e.file_exists(SAVE))
    e.if_cmp("hs", "==", 1, lambda: load_inline(e),
             else_body=lambda: (e.say("  No delve to continue."),
                                e.assign("LOOK AT ME.mode", M_TITLE)))


# -----------------------------------------------------------------  class select
def gen_class_select(e):
    e.imethod("classSelect")
    screen_top(e, "THE CHOOSING", "choose your path, Delver", M_CLASS)
    for cid in (0, 1, 2):
        c = C.CLASSES[cid]
        e.say("   %d) %-8s - %s" % (cid + 1, c["name"], c["blurb"]))
    e.say_blank()
    e.say("  Enter a number:")
    e.declare("c", 0)
    read_choice(e, "c")
    e.declare("k", 0)
    e.assign("k", "c", ("-", 1))
    e.if_cmp("k", "<", 0, lambda: e.set("k", 0))
    e.if_cmp("k", ">", 2, lambda: e.set("k", 0))
    e.assign("LOOK AT ME.cls", "k")
    e.end_imethod()


def gen_initclass(e):
    e.imethod("initclass")
    e.assign("LOOK AT ME.level", 1)
    e.assign("LOOK AT ME.xp", 0)
    e.assign("LOOK AT ME.xpnext", 40)
    e.assign("LOOK AT ME.gold", 30)
    e.assign("LOOK AT ME.kills", 0)
    e.assign("LOOK AT ME.started", 1)
    e.assign("LOOK AT ME.act", 1)
    e.assign("LOOK AT ME.floor", 1)
    e.assign("LOOK AT ME.room", 0)
    e.assign("LOOK AT ME.b0", 0); e.assign("LOOK AT ME.b1", 0); e.assign("LOOK AT ME.b2", 0)
    e.assign("LOOK AT ME.brandbuff", 0)
    # consumables
    e.assign("LOOK AT ME.potHeal", 3); e.assign("LOOK AT ME.potRes", 1)
    e.assign("LOOK AT ME.potCure", 1); e.assign("LOOK AT ME.bombs", 1); e.assign("LOOK AT ME.elixir", 0)
    # starting gear: weapon 0, armor 0, trinket 0, all owned
    e.assign("LOOK AT ME.wid", 0); e.assign("LOOK AT ME.aid", 0); e.assign("LOOK AT ME.tid", 0)
    e.assign("LOOK AT ME.ownw", 1); e.assign("LOOK AT ME.owna", 1); e.assign("LOOK AT ME.ownt", 1)

    def fighter():
        e.assign("LOOK AT ME.basehp", 36); e.assign("LOOK AT ME.baseres", 12)
        e.assign("LOOK AT ME.pstr", 8); e.assign("LOOK AT ME.pint", 2)
        e.assign("LOOK AT ME.pdex", 4); e.assign("LOOK AT ME.pdef", 5)

    def mage():
        e.assign("LOOK AT ME.basehp", 22); e.assign("LOOK AT ME.baseres", 24)
        e.assign("LOOK AT ME.pstr", 3); e.assign("LOOK AT ME.pint", 9)
        e.assign("LOOK AT ME.pdex", 5); e.assign("LOOK AT ME.pdef", 2)

    def rogue():
        e.assign("LOOK AT ME.basehp", 28); e.assign("LOOK AT ME.baseres", 16)
        e.assign("LOOK AT ME.pstr", 5); e.assign("LOOK AT ME.pint", 4)
        e.assign("LOOK AT ME.pdex", 9); e.assign("LOOK AT ME.pdef", 3)

    e.switch(e.f("cls"), [(0, fighter), (1, mage), (2, rogue)], default=fighter)
    emit_recompute_weapon(e)
    emit_recompute_equip(e)
    e.assign("LOOK AT ME.hp", e.f("maxhp"))
    e.assign("LOOK AT ME.res", e.f("maxres"))
    e.assign("LOOK AT ME.mode", M_ACTINTRO)
    e.end_imethod()


# recompute weapon-derived fields (wpow, wspec) from wid + cls
def emit_recompute_weapon(e):
    gw = e.tmp("v")
    e.declare(gw, e.f("cls"))
    e.assign(gw, e.f("cls"), ("*", NW), ("+", e.f("wid")))
    rec = read_record(e, WPN, gw)
    e.assign("LOOK AT ME.wpow", Emit.parse(Emit.aget(rec, 3)))
    e.assign("LOOK AT ME.wspec", Emit.parse(Emit.aget(rec, 5)))


# recompute armor+trinket aggregate bonuses and effective maxes; clamp hp/res
def emit_recompute_equip(e):
    adef, ahp, ares = e.tmp("v"), e.tmp("v"), e.tmp("v")
    thp, tres, tdef, tcrit = e.tmp("v"), e.tmp("v"), e.tmp("v"), e.tmp("v")
    ar = read_record(e, ARM, e.f("aid"))
    e.declare(adef, Emit.parse(Emit.aget(ar, 3)))
    e.declare(ahp, Emit.parse(Emit.aget(ar, 4)))
    e.declare(ares, Emit.parse(Emit.aget(ar, 5)))
    tr = read_record(e, TRK, e.f("tid"))
    e.declare(thp, Emit.parse(Emit.aget(tr, 2)))
    e.declare(tres, Emit.parse(Emit.aget(tr, 3)))
    e.declare(tdef, Emit.parse(Emit.aget(tr, 4)))
    e.declare(tcrit, Emit.parse(Emit.aget(tr, 5)))
    e.assign("LOOK AT ME.eqdef", adef, ("+", tdef))
    e.assign("LOOK AT ME.eqhp", ahp, ("+", thp))
    e.assign("LOOK AT ME.eqres", ares, ("+", tres))
    e.assign("LOOK AT ME.eqcrit", tcrit)
    e.assign("LOOK AT ME.maxhp", e.f("basehp"), ("+", e.f("eqhp")))
    e.assign("LOOK AT ME.maxres", e.f("baseres"), ("+", e.f("eqres")))
    emit_clamp(e)


def emit_clamp(e):
    e.if_cmp(e.f("hp"), ">", e.f("maxhp"), lambda: e.assign("LOOK AT ME.hp", e.f("maxhp")))
    e.if_cmp(e.f("res"), ">", e.f("maxres"), lambda: e.assign("LOOK AT ME.res", e.f("maxres")))
    e.if_cmp(e.f("hp"), "<", 1, lambda: e.assign("LOOK AT ME.hp", 1))


# -----------------------------------------------------------------  act intro
def gen_actintro(e):
    e.imethod("actintro")
    screen_top(e, "THE TALE", "the story so far", M_ACTINTRO)
    e.say("  ----------------------------------------------------------------")
    def show(aid):
        for ln in C.ACT_INTRO[aid]:
            e.say("   " + ln)
    e.switch(e.f("act"), [(1, lambda: show(1)), (2, lambda: show(2)), (3, lambda: show(3))])
    e.say("  ----------------------------------------------------------------")
    e.say_blank()
    e.say("  (press 1 to continue)")
    e.declare("c", 0)
    read_choice(e, "c")
    e.assign("LOOK AT ME.mode", M_CAMP)
    e.end_imethod()


# -----------------------------------------------------------------  camp hub
def gen_camp(e):
    e.imethod("camp")
    screen_top(e, "CAMP", "choose an action", M_CAMP)
    floor_name_switch(e, "  You make camp before:  ")
    # vitals (HP/res/Lv/Gold/Floor) are in the always-on stats line; show only the rest
    e.say_string(e.lit("  XP "), e.num(e.f("xp")), e.lit("/"), e.num(e.f("xpnext")),
                 e.lit("    Kills "), e.num(e.f("kills")))
    gear_line(e)
    e.say_blank()
    e.say("   1) Descend into the floor")
    e.say("   2) Rest (full heal)")
    e.say("   3) Shop (the Mire-Pedlar)")
    e.say("   4) Equipment")
    e.say("   5) Inventory / use item")
    e.say("   6) Save progress")
    e.say("   7) Save and abandon")
    e.say_blank()
    e.say("  Enter a number:")
    e.declare("c", 0)
    read_choice(e, "c")
    e.switch("c", [
        (1, lambda: (e.assign("LOOK AT ME.room", 0), set_roomcount(e), e.assign("LOOK AT ME.mode", M_EXPLORE))),
        (2, lambda: camp_rest(e)),
        (3, lambda: (e.assign("LOOK AT ME.shopret", M_CAMP), e.assign("LOOK AT ME.mode", M_SHOP))),
        (4, lambda: e.assign("LOOK AT ME.mode", M_EQUIP)),
        (5, lambda: camp_inventory(e)),
        (6, lambda: (save_inline(e), e.say("  Your progress is etched into the dark."))),
        (7, lambda: (save_inline(e), e.say("  You withdraw. The Crown waits."),
                     e.assign("LOOK AT ME.mode", M_QUIT))),
    ], default=lambda: e.say("  Nothing happens."))
    e.end_imethod()


def set_roomcount(e):
    # roomcount from FLOORS table by floor
    cases = []
    for i, fdef in enumerate(C.FLOORS):
        cases.append((i + 1, (lambda rc: (lambda: e.assign("LOOK AT ME.roomcount", rc)))(fdef[2])))
    e.switch(e.f("floor"), cases, default=lambda: e.assign("LOOK AT ME.roomcount", 5))


def floor_name_switch(e, prefix):
    cases = []
    for i, fdef in enumerate(C.FLOORS):
        nm = fdef[0]
        cases.append((i + 1, (lambda name: (lambda: e.say(prefix + name)))(nm)))
    e.switch(e.f("floor"), cases, default=lambda: e.say(prefix + "the dark"))


def gear_line(e):
    # print equipped weapon/armor/trinket names by reading the data files
    gw2 = e.tmp("v")
    e.declare(gw2, e.f("cls"))
    e.assign(gw2, e.f("cls"), ("*", NW), ("+", e.f("wid")))
    wr = read_record(e, WPN, gw2)
    ar = read_record(e, ARM, e.f("aid"))
    tr = read_record(e, TRK, e.f("tid"))
    e.say_string(e.lit("  Wield: "), Emit.aget(wr, 0), e.lit("  | Wear: "), Emit.aget(ar, 0),
                 e.lit("  | Trinket: "), Emit.aget(tr, 0))


def camp_rest(e):
    e.assign("LOOK AT ME.hp", e.f("maxhp"))
    e.assign("LOOK AT ME.res", e.f("maxres"))
    e.assign("LOOK AT ME.ppoison", 0)
    e.say("  You breathe. Wounds knit closed. You are whole again.")


def camp_inventory(e):
    e.say_blank()
    e.say("  -------- INVENTORY --------")
    for fld, label, price, note in C.CONSUMABLES:
        e.say_string(e.lit("   " + label + " x"), e.num(e.f(fld)), e.lit("   (" + note + ")"))
    e.say_blank()
    e.say("  Use which?  1)Mirewine 2)Star Tonic 3)Antidote 4)Greater Elixir 0)back")
    e.declare("c", 0)
    read_choice(e, "c")
    e.switch("c", [
        (1, lambda: use_item_camp(e, "potHeal", "hp", 24, "Mirewine Draught (+24 HP)")),
        (2, lambda: use_item_camp(e, "potRes", "res", 18, "Star Tonic (+18 resource)")),
        (3, lambda: cure_camp(e)),
        (4, lambda: elixir_camp(e)),
    ], default=lambda: e.say("  You pocket your gear."))


def use_item_camp(e, fld, stat, amt, label):
    def yes():
        e.assign("LOOK AT ME." + fld, e.f(fld), ("-", 1))
        e.assign("LOOK AT ME." + stat, e.f(stat), ("+", amt))
        emit_clamp(e)
        e.say("  You use a " + label + ".")
    e.if_cmp(e.f(fld), ">", 0, yes, else_body=lambda: e.say("  You have none."))


def cure_camp(e):
    def yes():
        e.assign("LOOK AT ME.potCure", e.f("potCure"), ("-", 1))
        e.assign("LOOK AT ME.ppoison", 0)
        e.say("  The antidote burns clean. Poison purged.")
    e.if_cmp(e.f("potCure"), ">", 0, yes, else_body=lambda: e.say("  You have no antidote."))


def elixir_camp(e):
    def yes():
        e.assign("LOOK AT ME.elixir", e.f("elixir"), ("-", 1))
        e.assign("LOOK AT ME.hp", e.f("maxhp"))
        e.assign("LOOK AT ME.res", e.f("maxres"))
        e.say("  The Greater Elixir restores you utterly.")
    e.if_cmp(e.f("elixir"), ">", 0, yes, else_body=lambda: e.say("  You have no elixir."))


# -----------------------------------------------------------------  shop
def gen_shop(e):
    e.imethod("shop")
    screen_top(e, "THE MIRE-PEDLAR", "spend your gold", M_SHOP)
    e.declare("shopping", 1)
    e.declare("c", 0)
    e.declare("buf", 0)

    def body():
        e.say_blank()
        e.say("  --------- THE MIRE-PEDLAR ---------")
        e.say_string(e.lit("  Your gold: "), e.num(e.f("gold")))
        e.say("   1) Weapons   2) Armor   3) Trinkets   4) Consumables   0) Leave")
        e.say("  Enter a number:")
        read_choice(e, "c")
        e.switch("c", [
            (1, lambda: shop_weapons(e)),
            (2, lambda: shop_list(e, "armor")),
            (3, lambda: shop_list(e, "trinket")),
            (4, lambda: shop_consum(e)),
            (0, lambda: e.set("shopping", 0)),
        ], default=lambda: e.say("  The pedlar shrugs."))
    e.while_("shopping", body)
    e.declare("rm", e.f("shopret"))
    e.assign("LOOK AT ME.mode", "rm")
    e.end_imethod()


def _act_gate_weapon(idx):
    # weapon local idx 0-11 -> earliest act available (normal a1, magic a2, unique a3)
    return 1 if idx <= 3 else (2 if idx <= 7 else 3)


def shop_weapons(e):
    e.say_blank()
    e.say("  -- Weapons for your discipline --")
    # list this class's weapons available up to current act, not owned
    for cls in (0, 1, 2):
        def block(cls=cls):
            for idx, w in enumerate(C.WEAPONS[cls]):
                gate = _act_gate_weapon(idx)
                if gate > 99:
                    continue
                name, tier, mat, power, price, spec = w
                spname = C.WEAPON_SPECIAL_NAME.get(spec, "")
                tag = "[%s %s%s]" % (tier, mat, (" " + spname) if spname else "")
                label = "   %2d) %-22s pow %2d  %4dg %s" % (idx + 1, name, power, price, tag)
                # only show if act >= gate
                e.if_cmp(e.f("act"), ">=", gate, (lambda lbl=label, idx=idx: (lambda: e.say(lbl)))())
        e.if_cmp(e.f("cls"), "==", cls, block)
    e.say("   0) back")
    e.say("  Buy which number?")
    e.declare("c", 0)
    read_choice(e, "c")
    e.if_cmp("c", ">", 0, lambda: buy_weapon(e, "c"))


def buy_weapon(e, choicevar):
    e.declare("idx", choicevar)
    e.assign("idx", choicevar, ("-", 1))
    e.if_cmp("idx", "<", 0, lambda: e.set("idx", 0))
    e.if_cmp("idx", ">", NW - 1, lambda: e.set("idx", NW - 1))
    e.declare("gidx", e.f("cls"))
    e.assign("gidx", e.f("cls"), ("*", NW), ("+", "idx"))
    rec = read_record(e, WPN, "gidx")
    e.declare("price", Emit.parse(Emit.aget(rec, 4)))
    # owned?
    owned = e.bit_test(e.f("ownw"), "idx")
    def notowned():
        def canpay():
            e.assign("LOOK AT ME.gold", e.f("gold"), ("-", "price"))
            e.bit_set("LOOK AT ME.ownw", "idx")
            e.assign("LOOK AT ME.wid", "idx")
            emit_recompute_weapon(e)
            e.say_string(e.lit("  You buy and wield the "), Emit.aget(rec, 0), e.lit("."))
        e.if_cmp(e.f("gold"), ">=", "price", canpay, else_body=lambda: e.say("  Not enough gold."))
    e.if_cmp(owned, "==", 1, lambda: e.say("  You already own that."), else_body=notowned)


def shop_list(e, kind):
    path = ARM if kind == "armor" else TRK
    rows = C.ARMORS if kind == "armor" else C.TRINKETS
    e.say_blank()
    e.say("  -- %s --" % ("Armor" if kind == "armor" else "Trinkets"))
    for idx, r in enumerate(rows):
        if kind == "armor":
            name, mat, tier, df, hp, rs, price = r
            gate = 1 if tier == "normal" else (2 if tier == "magic" else 3)
            tag = "[%s %s] def+%d hp+%d res+%d" % (tier, mat, df, hp, rs)
        else:
            name, tier, hp, rs, df, crit, price = r
            gate = 1 if tier == "normal" else (2 if tier == "magic" else 3)
            tag = "[%s] hp+%d res+%d def+%d crit+%d" % (tier, hp, rs, df, crit)
        label = "   %2d) %-20s %4dg %s" % (idx + 1, name, price, tag)
        e.if_cmp(e.f("act"), ">=", gate, (lambda lbl=label: (lambda: e.say(lbl)))())
    e.say("   0) back")
    e.say("  Buy which number?")
    e.declare("c", 0)
    read_choice(e, "c")
    e.if_cmp("c", ">", 0, lambda: buy_gear(e, "c", kind))


def buy_gear(e, choicevar, kind):
    path = ARM if kind == "armor" else TRK
    nrows = len(C.ARMORS) if kind == "armor" else len(C.TRINKETS)
    ownfield = "owna" if kind == "armor" else "ownt"
    idfield = "aid" if kind == "armor" else "tid"
    price_idx = 6
    e.declare("idx", choicevar)
    e.assign("idx", choicevar, ("-", 1))
    e.if_cmp("idx", "<", 0, lambda: e.set("idx", 0))
    e.if_cmp("idx", ">", nrows - 1, lambda: e.set("idx", nrows - 1))
    rec = read_record(e, path, "idx")
    e.declare("price", Emit.parse(Emit.aget(rec, price_idx)))
    owned = e.bit_test(e.f(ownfield), "idx")
    def notowned():
        def canpay():
            e.assign("LOOK AT ME.gold", e.f("gold"), ("-", "price"))
            e.bit_set("LOOK AT ME." + ownfield, "idx")
            e.assign("LOOK AT ME." + idfield, "idx")
            emit_recompute_equip(e)
            e.say_string(e.lit("  You buy and don the "), Emit.aget(rec, 0), e.lit("."))
        e.if_cmp(e.f("gold"), ">=", "price", canpay, else_body=lambda: e.say("  Not enough gold."))
    e.if_cmp(owned, "==", 1, lambda: e.say("  You already own that."), else_body=notowned)


def shop_consum(e):
    e.say_blank()
    e.say("  -- Consumables --")
    for i, (fld, label, price, note) in enumerate(C.CONSUMABLES):
        e.say("   %d) %-18s %3dg  (%s)" % (i + 1, label, price, note))
    e.say("   0) back")
    e.say("  Buy which number?")
    e.declare("c", 0)
    read_choice(e, "c")
    cases = []
    for i, (fld, label, price, note) in enumerate(C.CONSUMABLES):
        cases.append((i + 1, (lambda fld=fld, price=price, label=label: (lambda: buy_consum(e, fld, price, label)))()))
    e.switch("c", cases, default=lambda: None)


def buy_consum(e, fld, price, label):
    def canpay():
        e.assign("LOOK AT ME.gold", e.f("gold"), ("-", price))
        e.assign("LOOK AT ME." + fld, e.f(fld), ("+", 1))
        e.say("  You buy a " + label + ".")
    e.if_cmp(e.f("gold"), ">=", price, canpay, else_body=lambda: e.say("  Not enough gold."))


# -----------------------------------------------------------------  equipment screen
def gen_equip(e):
    e.imethod("equip")
    screen_top(e, "EQUIPMENT", "manage your gear", M_EQUIP)
    e.declare("managing", 1)
    e.declare("c", 0)

    def body():
        e.say_blank()
        e.say("  --------- EQUIPMENT ---------")
        gear_line(e)
        e.say_string(e.lit("  def+"), e.num(e.f("eqdef")), e.lit("  hp+"), e.num(e.f("eqhp")),
                     e.lit("  res+"), e.num(e.f("eqres")), e.lit("  crit+"), e.num(e.f("eqcrit")))
        e.say("   1) Equip a weapon you own")
        e.say("   2) Equip armor you own")
        e.say("   3) Equip a trinket you own")
        e.say("   0) Done")
        e.say("  Enter a number:")
        read_choice(e, "c")
        e.switch("c", [
            (1, lambda: equip_pick(e, "weapon")),
            (2, lambda: equip_pick(e, "armor")),
            (3, lambda: equip_pick(e, "trinket")),
            (0, lambda: e.set("managing", 0)),
        ], default=lambda: None)
    e.while_("managing", body)
    e.assign("LOOK AT ME.mode", M_CAMP)
    e.end_imethod()


def equip_pick(e, kind):
    if kind == "weapon":
        rows = C.WEAPONS  # per class
        ownf = "ownw"; idf = "wid"; n = NW
    elif kind == "armor":
        rows = C.ARMORS; ownf = "owna"; idf = "aid"; n = len(C.ARMORS)
    else:
        rows = C.TRINKETS; ownf = "ownt"; idf = "tid"; n = len(C.TRINKETS)
    e.say("  Owned " + kind + "s:")
    # list owned items (bit set)
    for idx in range(n):
        if kind == "weapon":
            # names depend on class
            for cls in (0, 1, 2):
                nm = C.WEAPONS[cls][idx][0]
                cond_owned = e.bit_test(e.f(ownf), str(idx))
                # show only if this class and owned
                def mk(cls=cls, idx=idx, nm=nm, cond=cond_owned):
                    def inner():
                        e.if_cmp(cond, "==", 1, (lambda: e.say("   %2d) %s" % (idx + 1, nm))))
                    return inner
                e.if_cmp(e.f("cls"), "==", cls, mk())
        else:
            nm = rows[idx][0]
            cond_owned = e.bit_test(e.f(ownf), str(idx))
            e.if_cmp(cond_owned, "==", 1, (lambda idx=idx, nm=nm: (lambda: e.say("   %2d) %s" % (idx + 1, nm))))())
    e.say("  Equip which number?")
    e.declare("c", 0)
    read_choice(e, "c")
    e.declare("idx", "c")
    e.assign("idx", "c", ("-", 1))
    def doit():
        owned = e.bit_test(e.f(ownf), "idx")
        def yes():
            e.assign("LOOK AT ME." + idf, "idx")
            if kind == "weapon":
                emit_recompute_weapon(e)
            else:
                emit_recompute_equip(e)
            e.say("  Equipped.")
        e.if_cmp(owned, "==", 1, yes, else_body=lambda: e.say("  You do not own that."))
    e.declare("ok1", 0)
    e.cmp("ok1", "idx", ">=", 0, declare=False)
    e.declare("ok2", 0)
    e.cmp("ok2", "idx", "<", n, declare=False)
    e.if_cmp("ok1", "==", 1, lambda: e.if_cmp("ok2", "==", 1, doit, else_body=lambda: e.say("  No such item.")),
             else_body=lambda: e.say("  No such item."))


# -----------------------------------------------------------------  explore (floor rooms)
def gen_explore(e):
    e.imethod("explore")
    screen_top(e, "THE DESCENT", "you press deeper", M_EXPLORE)
    e.assign("LOOK AT ME.room", e.f("room"), ("+", 1))
    # floor complete?
    e.declare("done", 0)
    e.cmp("done", e.f("room"), ">", e.f("roomcount"), declare=False)

    def floor_done():
        e.declare("isboss", 0)
        # boss on floors 4, 8, 12
        e.declare("f", e.f("floor"))
        e.declare("bossfloor", 0)
        e.if_cmp("f", "==", 4, lambda: e.set("bossfloor", 1))
        e.if_cmp("f", "==", 8, lambda: e.set("bossfloor", 1))
        e.if_cmp("f", "==", 12, lambda: e.set("bossfloor", 1))
        def to_boss():
            e.say_blank()
            e.say("  The way ahead opens into a vast chamber. Something waits.")
            e.assign("LOOK AT ME.mode", M_BOSS)
        def to_next():
            e.say_blank()
            e.say("  You clear the floor and find a defensible hollow to rest.")
            e.assign("LOOK AT ME.floor", e.f("floor"), ("+", 1))
            e.assign("LOOK AT ME.act", floor_to_act_expr(e))
            e.assign("LOOK AT ME.mode", M_CAMP)
        e.if_cmp("bossfloor", "==", 1, to_boss, else_body=to_next)

    def present_room():
        # roll room type: 0-54 combat, 55-74 event, 75-86 treasure, 87-94 rest, 95-99 elite
        floor_name_room(e)
        e.declare("roll", Emit.rnd(100))
        e.declare("rt", 0)  # room type
        e.if_cmp("roll", ">=", 55, lambda: e.set("rt", 1))
        e.if_cmp("roll", ">=", 75, lambda: e.set("rt", 2))
        e.if_cmp("roll", ">=", 87, lambda: e.set("rt", 3))
        e.if_cmp("roll", ">=", 95, lambda: e.set("rt", 4))
        e.switch("rt", [
            (0, lambda: room_combat(e, elite=False)),
            (1, lambda: room_event(e)),
            (2, lambda: room_treasure(e)),
            (3, lambda: room_rest(e)),
            (4, lambda: room_combat(e, elite=True)),
        ])
    e.if_cmp("done", "==", 1, floor_done, else_body=present_room)
    e.end_imethod()


def floor_to_act_expr(e):
    # compute act from new floor: floors 1-4=1,5-8=2,9-12=3. We just bumped floor.
    # act = ((floor-1)/4)+1
    e.declare("aa", e.f("floor"))
    e.assign("aa", e.f("floor"), ("-", 1), ("/", 4), ("+", 1))
    return "aa"


def floor_name_room(e):
    e.say_blank()
    e.say_string(e.lit("  -- Floor "), e.num(e.f("floor")), e.lit(", room "), e.num(e.f("room")),
                 e.lit("/"), e.num(e.f("roomcount")), e.lit(" --"))


def room_combat(e, elite):
    # choose a monster from the floor pool (elite: floor+2 pool, boosted)
    e.declare("pf", e.f("floor"))
    if elite:
        e.assign("pf", e.f("floor"), ("+", 2))
        e.if_cmp("pf", ">", 12, lambda: e.set("pf", 12))
    e.declare("pidx", "pf")
    e.assign("pidx", "pf", ("-", 1))
    poolrec = read_record(e, POOL, "pidx")
    # poolrec is a #-split of a single-field record (no #), so element 0 is the csv list
    csv = e.tmp("s")
    e.string(csv, Emit.aget(poolrec, 0))
    ids = e.tmp("a")
    e.split(ids, csv, ",")
    e.declare("pn", Emit.alen(ids))
    e.declare("pick", Emit.rnd("pn"))
    e.assign("LOOK AT ME.pendmon", Emit.parse(Emit.aget(ids, "pick")))
    e.assign("LOOK AT ME.isboss", 0)
    if elite:
        e.assign("LOOK AT ME.isboss", 2)  # 2 = elite flag (boosted in combat)
        e.say("  A hush, then dread: an ELITE foe blocks the way.")
    e.assign("LOOK AT ME.mode", M_COMBAT)


def room_treasure(e):
    e.say("  You find a cache half-buried in the muck.")
    e.declare("g", Emit.rnd(30))
    e.assign("g", "g", ("+", 15), ("+", e.f("floor")), ("*", 1))
    e.assign("LOOK AT ME.gold", e.f("gold"), ("+", "g"))
    e.say_string(e.lit("  +"), e.num("g"), e.lit(" gold."))
    # chance of a potion
    e.declare("r", Emit.rnd(100))
    e.if_cmp("r", "<", 40, lambda: (e.assign("LOOK AT ME.potHeal", e.f("potHeal"), ("+", 1)),
                                    e.say("  ...and a Mirewine Draught!")))
    e.say("  (press 1 to continue)")
    e.declare("c", 0)
    read_choice(e, "c")


def room_rest(e):
    e.say("  A still, dry alcove. You catch your breath.")
    e.declare("h", e.f("maxhp"))
    e.assign("h", e.f("maxhp"), ("/", 4))
    e.assign("LOOK AT ME.hp", e.f("hp"), ("+", "h"))
    e.assign("LOOK AT ME.res", e.f("res"), ("+", 4))
    emit_clamp(e)
    e.say_string(e.lit("  Recovered "), e.num("h"), e.lit(" HP."))
    e.say("  (press 1 to continue)")
    e.declare("c", 0)
    read_choice(e, "c")


def room_event(e):
    e.declare("ev", Emit.rnd(len(C.EVENTS)))
    e.assign("LOOK AT ME.pendev", "ev")
    e.assign("LOOK AT ME.mode", M_EVENT)


# Add a raw aget passthrough for clarity
def _patch_emit():
    if not hasattr(Emit, "aget_raw"):
        Emit.aget_raw = staticmethod(lambda name: name)


_patch_emit()


# -----------------------------------------------------------------  events
def gen_event(e):
    e.imethod("event")
    screen_top(e, "AN OMEN", "the dark offers a choice", M_EVENT)
    cases = []
    for i, ev in enumerate(C.EVENTS):
        cases.append((i, (lambda ev=ev: (lambda: event_body(e, ev)))()))
    e.switch(e.f("pendev"), cases, default=lambda: e.assign("LOOK AT ME.mode", M_EXPLORE))
    e.end_imethod()


def event_body(e, ev):
    e.say_blank()
    e.say("  ~ " + ev["title"] + " ~")
    for ln in ev["desc"]:
        e.say("   " + ln)
    e.say_blank()
    for i, (label, eff) in enumerate(ev["choices"]):
        e.say("   %d) %s" % (i + 1, label))
    e.say("  Enter a number:")
    e.declare("c", 0)
    read_choice(e, "c")
    cases = []
    for i, (label, eff) in enumerate(ev["choices"]):
        cases.append((i + 1, (lambda eff=eff: (lambda: apply_effect(e, eff)))()))
    e.switch("c", cases, default=lambda: e.say("  You do nothing."))
    # most events return to explore; shop sets its own mode
    e.declare("isshop", 0)
    e.cmp("isshop", e.f("mode"), "==", M_SHOP, declare=False)
    e.if_cmp("isshop", "==", 0, lambda: e.assign("LOOK AT ME.mode", M_EXPLORE))


def apply_effect(e, eff):
    toks = eff.split()
    for tok in toks:
        if tok == "fullheal":
            e.assign("LOOK AT ME.hp", e.f("maxhp")); e.assign("LOOK AT ME.res", e.f("maxres"))
            e.assign("LOOK AT ME.ppoison", 0)
            e.say("  The cold water restores you. HP and resource full.")
        elif tok == "nothing":
            e.say("  You leave it untouched.")
        elif tok == "curse":
            e.assign("LOOK AT ME.pweak", 3)
            e.say("  A chill settles in your bones. (weakened a while)")
        elif tok == "riskcurse":
            e.declare("rc", Emit.rnd(100))
            e.if_cmp("rc", "<", 35, lambda: (e.assign("LOOK AT ME.ppoison", 3),
                                             e.say("  The whisper bites back - you feel sick.")))
        elif tok == "brandbuff":
            e.assign("LOOK AT ME.brandbuff", 8)
            e.say("  Your weapon drinks the starlight. Your next 8 hits each deal +4 damage.")
        elif tok == "chestloot":
            e.declare("cl", Emit.rnd(100))
            def trap():
                e.assign("LOOK AT ME.hp", e.f("hp"), ("-", 10))
                emit_clamp(e)
                e.say("  A needle-trap! You lose 10 HP - but grab 30 gold.")
                e.assign("LOOK AT ME.gold", e.f("gold"), ("+", 30))
            def loot():
                e.declare("gg", Emit.rnd(50))
                e.assign("gg", "gg", ("+", 30))
                e.assign("LOOK AT ME.gold", e.f("gold"), ("+", "gg"))
                e.say_string(e.lit("  The chest yields "), e.num("gg"), e.lit(" gold!"))
            e.if_cmp("cl", "<", 30, trap, else_body=loot)
        elif tok == "trapdodge":
            e.declare("td", Emit.rnd(100))
            e.if_cmp("td", "<", 60, lambda: e.say("  You dive clear as darts rattle off stone."),
                     else_body=lambda: (e.assign("LOOK AT ME.hp", e.f("hp"), ("-", 8)), emit_clamp(e),
                                        e.say("  A dart grazes you for 8.")))
        elif tok == "trinketchance":
            e.declare("tc", Emit.rnd(100))
            e.if_cmp("tc", "<", 50, lambda: (e.bit_set("LOOK AT ME.ownt", 1),
                                             e.say("  You find a Ring of Vigor among their effects!")))
        elif tok == "shop":
            e.assign("LOOK AT ME.shopret", M_EXPLORE)
            e.assign("LOOK AT ME.mode", M_SHOP)
            e.say("  The pedlar spreads a stained cloth of wares.")
        elif tok.startswith("gold+"):
            amt = int(tok[5:])
            e.assign("LOOK AT ME.gold", e.f("gold"), ("+", amt))
            e.say_string(e.lit("  +"), e.num(str(amt)), e.lit(" gold."))
        elif tok.startswith("gold-"):
            amt = int(tok[5:])
            e.assign("LOOK AT ME.gold", e.f("gold"), ("-", amt))
        elif tok.startswith("hp-"):
            amt = int(tok[3:])
            e.assign("LOOK AT ME.hp", e.f("hp"), ("-", amt)); emit_clamp(e)
            e.say_string(e.lit("  You take "), e.num(str(amt)), e.lit(" damage."))
        elif tok.startswith("res+"):
            e.assign("LOOK AT ME.res", e.f("maxres"))
            e.say("  Resource restored.")
        elif tok.startswith("xp+"):
            amt = int(tok[3:])
            e.assign("LOOK AT ME.xp", e.f("xp"), ("+", amt))
            e.say_string(e.lit("  +"), e.num(str(amt)), e.lit(" XP."))
        elif tok.startswith("item:potHeal"):
            cnt = 2 if tok.endswith("2") else 1
            e.assign("LOOK AT ME.potHeal", e.f("potHeal"), ("+", cnt))
            e.say("  You recover %d Mirewine Draught(s)." % cnt)


# -----------------------------------------------------------------  combat (regular)
def gen_combat(e):
    e.imethod("combat")
    screen_top(e, "COMBAT", "to battle", M_COMBAT)
    e.comment("load monster by pendmon")
    rec = read_record(e, MON, e.f("pendmon"))
    e.assign("LOOK AT ME.ehp", Emit.parse(Emit.aget(rec, 1)))
    e.assign("LOOK AT ME.eatk", Emit.parse(Emit.aget(rec, 2)))
    e.assign("LOOK AT ME.edef", Emit.parse(Emit.aget(rec, 3)))
    e.declare("xpr", Emit.parse(Emit.aget(rec, 4)))
    e.declare("goldr", Emit.parse(Emit.aget(rec, 5)))
    e.assign("LOOK AT ME.eabil", Emit.parse(Emit.aget(rec, 6)))
    e.string("ename", Emit.aget(rec, 0))
    # elite boost
    e.if_cmp(e.f("isboss"), "==", 2, lambda: combat_elite_boost(e, "xpr", "goldr"))
    e.assign("LOOK AT ME.emaxhp", e.f("ehp"))
    e.assign("LOOK AT ME.estun", 0); e.assign("LOOK AT ME.epoison", 0)
    e.assign("LOOK AT ME.eweak", 0); e.assign("LOOK AT ME.pshield", 0)
    e.say_blank()
    e.say_string(e.lit("  A " + A_RED), "ename", e.lit(A_RST + " attacks!"))
    combat_loop(e, "xpr", "goldr", is_boss=False)
    e.end_imethod()


def combat_elite_boost(e, xpr, goldr):
    e.assign("LOOK AT ME.ehp", e.f("ehp"), ("*", 3), ("/", 2))
    e.assign("LOOK AT ME.eatk", e.f("eatk"), ("+", 3))
    e.assign("LOOK AT ME.edef", e.f("edef"), ("+", 2))
    e.assign(xpr, xpr, ("*", 2))
    e.assign(goldr, goldr, ("*", 2))


# the shared combat loop. For bosses, phase logic is added by the caller via flags.
def combat_loop(e, xpr, goldr, is_boss):
    e.declare("pstat", e.f("pstr"))
    e.if_cmp(e.f("cls"), "==", 1, lambda: e.set("pstat", e.f("pint")))
    e.if_cmp(e.f("cls"), "==", 2, lambda: e.set("pstat", e.f("pdex")))
    e.declare("critc", 0)
    e.assign("critc", e.f("pdex"), ("*", 3), ("+", e.f("eqcrit")))
    e.if_cmp(e.f("cls"), "==", 2, lambda: e.assign("critc", e.f("pdex"), ("*", 5), ("+", e.f("eqcrit"))))
    e.declare("defu", e.f("pdef"))
    e.assign("defu", e.f("pdef"), ("+", e.f("eqdef")))

    e.declare("fighting", 1)
    e.declare("choice", 0)
    e.declare("pdmg", 0)
    e.declare("acted", 0)
    e.declare("fled", 0)
    e.declare("cost", 0)
    e.declare("cr", 0)
    e.declare("eat", 0)
    e.declare("edmg", 0)
    e.declare("roll", 0)
    e.declare("life", 0)

    def loop_body():
        # poison the player suffers each round
        def ppois():
            e.assign("LOOK AT ME.hp", e.f("hp"), ("-", 3))
            e.assign("LOOK AT ME.ppoison", e.f("ppoison"), ("-", 1))
            e.say("  Venom courses through you (-3 HP).")
        e.if_cmp(e.f("ppoison"), ">", 0, ppois)
        # status
        e.say_blank()
        e.say_string(e.lit("  " + A_RED), "ename", e.lit(A_RST + "   HP "), e.num(e.f("ehp")), e.lit("/"), e.num(e.f("emaxhp")))
        e.say_string(e.lit("  You      HP "), e.num(e.f("hp")), e.lit("/"), e.num(e.f("maxhp")),
                     e.lit("   res "), e.num(e.f("res")), e.lit("/"), e.num(e.f("maxres")))
        combat_menu(e)
        e.say("  [1]attack [2]signature [3]skill [4]item [5]flee")
        read_choice(e, "choice")
        e.set("pdmg", 0)
        e.set("acted", 1)
        e.switch("choice", [
            (1, lambda: atk_basic(e)),
            (2, lambda: atk_signature(e)),
            (3, lambda: atk_skill(e)),
            (4, lambda: combat_item(e)),
            (5, lambda: try_flee(e, is_boss)),
        ], default=lambda: (e.say("  You hesitate."), e.set("acted", 0)))

        # apply player damage with crit + brand + weapon special
        def apply_pdmg():
            # brand buff bonus
            e.if_cmp(e.f("brandbuff"), ">", 0, lambda: (e.assign("pdmg", "pdmg", ("+", 4)),
                                                        e.assign("LOOK AT ME.brandbuff", e.f("brandbuff"), ("-", 1))))
            e.set("cr", Emit.rnd(100))
            e.if_cmp("cr", "<", "critc", lambda: (e.assign("pdmg", "pdmg", ("*", 2)),
                                                  e.say(col("  *** CRITICAL! ***", "1;33"))))
            # weapon special 5 brand => +2; 6 executioner => +50% vs low hp; 1 lifesteal
            e.if_cmp(e.f("wspec"), "==", 6, lambda: combat_execute(e))
            e.assign("LOOK AT ME.ehp", e.f("ehp"), ("-", "pdmg"))
            e.say_string(e.lit("  You hit "), "ename", e.lit(" for "), e.num("pdmg"), e.lit("."))
            # lifesteal
            def steal():
                e.set("life", "pdmg")
                e.assign("life", "pdmg", ("/", 4))
                e.assign("LOOK AT ME.hp", e.f("hp"), ("+", "life"))
                emit_clamp(e)
                e.if_cmp("life", ">", 0, lambda: e.say_string(e.lit("  Your blade drinks "), e.num("life"), e.lit(" life.")))
            e.if_cmp(e.f("wspec"), "==", 1, steal)
            # doublestrike chance
            def dbl():
                e.declare("dd", Emit.rnd(100))
                e.if_cmp("dd", "<", 35, lambda: (e.assign("LOOK AT ME.ehp", e.f("ehp"), ("-", "pdmg")),
                                                 e.say("  A second strike lands!")))
            e.if_cmp(e.f("wspec"), "==", 4, dbl)
        e.if_cmp("pdmg", ">", 0, apply_pdmg)

        # enemy poison/bleed on it
        def etick():
            e.assign("LOOK AT ME.ehp", e.f("ehp"), ("-", 3))
            e.assign("LOOK AT ME.epoison", e.f("epoison"), ("-", 1))
            e.say("  Poison rots the foe (-3).")
        e.if_cmp(e.f("epoison"), ">", 0, etick)

        # enemy dead?
        def enemy_dead():
            e.set("fighting", 0)
            e.say_blank()
            e.say_string(e.lit("  " + A_GRN + "The "), "ename", e.lit(" is slain." + A_RST))
            e.assign("LOOK AT ME.xp", e.f("xp"), ("+", xpr))
            e.assign("LOOK AT ME.gold", e.f("gold"), ("+", goldr))
            e.assign("LOOK AT ME.kills", e.f("kills"), ("+", 1))
            e.say_string(e.lit("  " + A_GRN + "+"), e.num(xpr), e.lit(" XP, +"), e.num(goldr), e.lit(" gold." + A_RST))
            check_levelup(e)
            e.say_blank()
            e.say("  (press 1 to gather the spoils)")
            read_choice(e, "choice")
            e.assign("LOOK AT ME.mode", M_EXPLORE)

        def enemy_turn():
            def stunned():
                e.assign("LOOK AT ME.estun", e.f("estun"), ("-", 1))
                e.say_string(e.lit("  The "), "ename", e.lit(" is staggered."))
            def strike():
                enemy_ability_pre(e)
                e.set("eat", e.f("eatk"))
                e.if_cmp(e.f("eweak"), ">", 0, lambda: (e.assign("eat", e.f("eatk"), ("-", 3)),
                                                        e.assign("LOOK AT ME.eweak", e.f("eweak"), ("-", 1))))
                e.assign("edmg", "eat", ("+", Emit.rnd(4)), ("-", "defu"), ("-", e.f("pshield")))
                # enemy crit ability (5)
                e.if_cmp(e.f("eabil"), "==", 5, lambda: enemy_crit(e))
                e.if_cmp("edmg", "<", 1, lambda: e.set("edmg", 1))
                e.assign("LOOK AT ME.hp", e.f("hp"), ("-", "edmg"))
                e.say_string(e.lit("  " + A_RED + "The "), "ename", e.lit(" hits you for "), e.num("edmg"), e.lit("." + A_RST))
                enemy_ability_post(e)
            e.if_cmp(e.f("estun"), ">", 0, stunned, else_body=strike)
            e.if_cmp(e.f("pshield"), ">", 0, lambda: e.assign("LOOK AT ME.pshield", e.f("pshield"), ("-", 1)))
            def dead():
                e.set("fighting", 0)
                e.assign("LOOK AT ME.mode", M_GAMEOVER)
            e.if_cmp(e.f("hp"), "<", 1, dead, else_body=lambda: regen_res(e))

        def aliveturn():
            def doenemy():
                e.if_cmp("acted", "==", 1, enemy_turn)
            e.if_cmp(e.f("ehp"), "<", 1, enemy_dead, else_body=doenemy)

        def didflee():
            e.set("fighting", 0)
            e.assign("LOOK AT ME.mode", M_EXPLORE)
        e.if_cmp("fled", "==", 1, didflee, else_body=aliveturn)

    e.while_("fighting", loop_body)


def enemy_ability_pre(e):
    # selfheal (2), enrage (3), shield (4) happen before/around strike
    def heal():
        e.declare("hh", Emit.rnd(5))
        e.assign("hh", "hh", ("+", 4))
        e.assign("LOOK AT ME.ehp", e.f("ehp"), ("+", "hh"))
        e.if_cmp(e.f("ehp"), ">", e.f("emaxhp"), lambda: e.assign("LOOK AT ME.ehp", e.f("emaxhp")))
        e.say_string(e.lit("  The foe knits its wounds (+"), e.num("hh"), e.lit(")."))
    e.if_cmp(e.f("eabil"), "==", 2, heal)
    def enrage():
        e.declare("low", 0)
        e.cmp("low", e.f("ehp"), "<", e.f("emaxhp"), declare=False)  # placeholder
    # enrage handled in damage via eabil==3 check below in enemy_crit-like; keep simple here


def enemy_ability_post(e):
    # drain (6), weaken-you (7), poison (1) apply after the hit
    def poison():
        e.if_cmp(e.f("ppoison"), "<", 1, lambda: (e.assign("LOOK AT ME.ppoison", 3),
                                                  e.say("  Its venom seeps into the wound. (poisoned)")))
    e.if_cmp(e.f("eabil"), "==", 1, poison)
    def drain():
        e.assign("LOOK AT ME.res", e.f("res"), ("-", 4))
        e.if_cmp(e.f("res"), "<", 0, lambda: e.assign("LOOK AT ME.res", 0))
        e.say("  The wraith drinks your focus (-4 resource).")
    e.if_cmp(e.f("eabil"), "==", 6, drain)
    def weaken():
        e.assign("LOOK AT ME.pweak", 2)
        e.say("  A hex saps your strength. (weakened)")
    e.if_cmp(e.f("eabil"), "==", 7, weaken)


def enemy_crit(e):
    e.declare("ec", Emit.rnd(100))
    e.if_cmp("ec", "<", 25, lambda: (e.assign("edmg", "edmg", ("*", 2)), e.say("  A vicious blow!")))


def combat_execute(e):
    # executioner: +60% damage if enemy below 35% hp
    e.declare("thr", e.f("emaxhp"))
    e.assign("thr", e.f("emaxhp"), ("*", 35), ("/", 100))
    e.if_cmp(e.f("ehp"), "<", "thr", lambda: (e.assign("pdmg", "pdmg", ("*", 8), ("/", 5)),
                                              e.say("  Execution!")))


def combat_menu(e):
    e.say_blank()
    e.switch(e.f("cls"), [
        (0, lambda: e.say("  [1]Strike  [2]Cleave(5)  [3]Shield Bash(3)")),
        (1, lambda: e.say("  [1]Firebolt  [2]Arcane Surge(8)  [3]Hex(4)")),
        (2, lambda: e.say("  [1]Slash  [2]Backstab(6)  [3]Poison Strike(4)")),
    ])


def _pweak_adjust(e):
    # if player weakened, reduce pstat-based dmg by 3 and tick down
    e.if_cmp(e.f("pweak"), ">", 0, lambda: (e.assign("pdmg", "pdmg", ("-", 3)),
                                            e.assign("LOOK AT ME.pweak", e.f("pweak"), ("-", 1)),
                                            e.if_cmp("pdmg", "<", 1, lambda: e.set("pdmg", 1))))


def atk_basic(e):
    e.assign("pdmg", "pstat", ("+", e.f("wpow")), ("+", Emit.rnd(4)), ("-", e.f("edef")))
    e.if_cmp("pdmg", "<", 1, lambda: e.set("pdmg", 1))
    _pweak_adjust(e)


def atk_signature(e):
    e.set("cost", 5)
    e.if_cmp(e.f("cls"), "==", 1, lambda: e.set("cost", 5))
    e.if_cmp(e.f("cls"), "==", 1, lambda: None)
    e.switch(e.f("cls"), [
        (0, lambda: e.set("cost", 5)),
        (1, lambda: e.set("cost", 8)),
        (2, lambda: e.set("cost", 6)),
    ])

    def enough():
        e.assign("LOOK AT ME.res", e.f("res"), ("-", "cost"))
        e.assign("pdmg", "pstat", ("*", 2), ("+", e.f("wpow")), ("+", Emit.rnd(5)), ("-", e.f("edef")))
        e.if_cmp("pdmg", "<", 1, lambda: e.set("pdmg", 1))
        e.switch(e.f("cls"), [
            (0, lambda: (e.assign("LOOK AT ME.estun", 1), e.say("  You CLEAVE in a savage arc - it staggers!"))),
            (1, lambda: (e.assign("pdmg", "pdmg", ("+", e.f("pint")), ("+", 2)),
                         e.say("  ARCANE SURGE! Star-fire blooms."))),
            (2, lambda: (e.assign("LOOK AT ME.epoison", 3), e.say("  BACKSTAB from the dark!"))),
        ])
        _pweak_adjust(e)
    e.if_cmp(e.f("res"), ">=", "cost", enough,
             else_body=lambda: (e.say("  Not enough resource. (wasted)"), e.set("pdmg", 0)))


def atk_skill(e):
    e.switch(e.f("cls"), [
        (0, lambda: e.set("cost", 3)),
        (1, lambda: e.set("cost", 4)),
        (2, lambda: e.set("cost", 4)),
    ])

    def enough():
        e.assign("LOOK AT ME.res", e.f("res"), ("-", "cost"))
        e.switch(e.f("cls"), [
            (0, lambda: (e.assign("LOOK AT ME.estun", 1), e.assign("LOOK AT ME.pshield", 2),
                         e.assign("pdmg", "pstat", ("-", e.f("edef"))),
                         e.if_cmp("pdmg", "<", 1, lambda: e.set("pdmg", 1)),
                         e.say("  Shield Bash! Foe staggered; you raise your guard."))),
            (1, lambda: (e.assign("LOOK AT ME.eweak", 3), e.assign("pdmg", 3),
                         e.say("  You weave a Hex; the foe's blows weaken."))),
            (2, lambda: (e.assign("LOOK AT ME.epoison", 5), e.assign("pdmg", 3),
                         e.say("  Poisoned strike! The rot sets in."))),
        ])
    e.if_cmp(e.f("res"), ">=", "cost", enough,
             else_body=lambda: (e.say("  Not enough resource. (wasted)"), e.set("pdmg", 0)))


def combat_item(e):
    e.say("   Use: 1)Mirewine(+24) 2)Tonic(+18 res) 3)Antidote 4)Thornbomb 5)Elixir")
    e.declare("ic", 0)
    read_choice(e, "ic")
    e.set("pdmg", 0)
    def bomb():
        def yes():
            e.assign("LOOK AT ME.bombs", e.f("bombs"), ("-", 1))
            e.set("pdmg", 20)
            e.say("  You hurl a Thornbomb! (20 damage)")
        e.if_cmp(e.f("bombs"), ">", 0, yes, else_body=lambda: (e.say("  No bombs."), e.set("acted", 0)))
    e.switch("ic", [
        (1, lambda: item_heal(e, "potHeal", "hp", 24, "Mirewine")),
        (2, lambda: item_heal(e, "potRes", "res", 18, "Star Tonic")),
        (3, lambda: item_cure(e)),
        (4, bomb),
        (5, lambda: item_elixir(e)),
    ], default=lambda: (e.say("  You fumble. (turn lost)"),))


def item_heal(e, fld, stat, amt, label):
    def yes():
        e.assign("LOOK AT ME." + fld, e.f(fld), ("-", 1))
        e.assign("LOOK AT ME." + stat, e.f(stat), ("+", amt))
        emit_clamp(e)
        e.say("  You use a " + label + ".")
    e.if_cmp(e.f(fld), ">", 0, yes, else_body=lambda: (e.say("  You have none."), e.set("acted", 0)))


def item_cure(e):
    def yes():
        e.assign("LOOK AT ME.potCure", e.f("potCure"), ("-", 1))
        e.assign("LOOK AT ME.ppoison", 0)
        e.say("  Antidote: poison purged.")
    e.if_cmp(e.f("potCure"), ">", 0, yes, else_body=lambda: (e.say("  No antidote."), e.set("acted", 0)))


def item_elixir(e):
    def yes():
        e.assign("LOOK AT ME.elixir", e.f("elixir"), ("-", 1))
        e.assign("LOOK AT ME.hp", e.f("maxhp")); e.assign("LOOK AT ME.res", e.f("maxres"))
        e.say("  Greater Elixir: fully restored!")
    e.if_cmp(e.f("elixir"), ">", 0, yes, else_body=lambda: (e.say("  No elixir."), e.set("acted", 0)))


def try_flee(e, is_boss):
    e.set("pdmg", 0)
    if is_boss:
        e.say("  There is no fleeing this. You stand.")
        e.set("acted", 1)
    else:
        e.set("roll", Emit.rnd(100))
        e.if_cmp("roll", "<", 45, lambda: (e.say("  You slip away into the dark."), e.set("fled", 1)),
                 else_body=lambda: e.say("  You stumble - no escape!"))


def regen_res(e):
    e.assign("LOOK AT ME.res", e.f("res"), ("+", 2))
    # weapon regen special (3)
    e.if_cmp(e.f("wspec"), "==", 3, lambda: e.assign("LOOK AT ME.res", e.f("res"), ("+", 2)))
    e.if_cmp(e.f("res"), ">", e.f("maxres"), lambda: e.assign("LOOK AT ME.res", e.f("maxres")))


def check_levelup(e):
    e.declare("canlvl", 0)
    e.cmp("canlvl", e.f("xp"), ">=", e.f("xpnext"), declare=False)

    def lvlloop():
        e.assign("LOOK AT ME.xp", e.f("xp"), ("-", e.f("xpnext")))
        e.assign("LOOK AT ME.level", e.f("level"), ("+", 1))
        # per-class growth via switch
        e.switch(e.f("cls"), [
            (0, lambda: grow(e, 0)),
            (1, lambda: grow(e, 1)),
            (2, lambda: grow(e, 2)),
        ])
        e.assign("LOOK AT ME.maxhp", e.f("basehp"), ("+", e.f("eqhp")))
        e.assign("LOOK AT ME.maxres", e.f("baseres"), ("+", e.f("eqres")))
        e.assign("LOOK AT ME.hp", e.f("maxhp"))
        e.assign("LOOK AT ME.res", e.f("maxres"))
        e.declare("bump", e.f("level"))
        e.assign("bump", e.f("level"), ("*", e.f("level")), ("*", 4))
        e.assign("LOOK AT ME.xpnext", 30, ("+", "bump"))
        e.say_string(e.lit("  " + A_MAG + "*** You reach level "), e.num(e.f("level")), e.lit("! ***" + A_RST))
        e.cmp("canlvl", e.f("xp"), ">=", e.f("xpnext"), declare=False)

    e.while_("canlvl", lvlloop)


def grow(e, cls):
    c = C.CLASSES[cls]
    e.assign("LOOK AT ME.basehp", e.f("basehp"), ("+", c["ghp"]))
    e.assign("LOOK AT ME.baseres", e.f("baseres"), ("+", c["gres"]))
    e.assign("LOOK AT ME.pstr", e.f("pstr"), ("+", c["gst"]))
    e.assign("LOOK AT ME.pint", e.f("pint"), ("+", c["git"]))
    e.assign("LOOK AT ME.pdex", e.f("pdex"), ("+", c["gdx"]))
    e.assign("LOOK AT ME.pdef", e.f("pdef"), ("+", c["gdf"]))


# -----------------------------------------------------------------  boss fight
def gen_bossfight(e):
    e.imethod("bossfight")
    screen_top(e, "BOSS", "to battle", M_BOSS)
    e.comment("which boss: by act (1->0, 2->1, 3->2)")
    e.declare("bi", e.f("act"))
    e.assign("bi", e.f("act"), ("-", 1))
    e.assign("LOOK AT ME.bossid", "bi")
    rec = read_record(e, BOSS, "bi")
    e.assign("LOOK AT ME.ehp", Emit.parse(Emit.aget(rec, 1)))
    e.assign("LOOK AT ME.emaxhp", e.f("ehp"))
    e.assign("LOOK AT ME.eatk", Emit.parse(Emit.aget(rec, 2)))
    e.assign("LOOK AT ME.edef", Emit.parse(Emit.aget(rec, 3)))
    e.declare("xpr", Emit.parse(Emit.aget(rec, 4)))
    e.declare("goldr", Emit.parse(Emit.aget(rec, 5)))
    e.assign("LOOK AT ME.eabil", 0)
    e.assign("LOOK AT ME.isboss", 1)
    e.assign("LOOK AT ME.bphase", 1)
    e.string("ename", Emit.aget(rec, 0))
    # intro lore by bossid
    boss_intro(e)
    boss_loop(e, "xpr", "goldr")
    e.end_imethod()


def boss_intro(e):
    cases = []
    for b in C.BOSSES:
        def mk(b=b):
            def inner():
                e.say_blank()
                for ln in b["intro"]:
                    e.say("   " + ln)
                e.say_blank()
            return inner
        cases.append((b["id"], mk()))
    e.switch(e.f("bossid"), cases)


def boss_loop(e, xpr, goldr):
    # like combat_loop but with phase transitions and no fleeing
    e.declare("pstat", e.f("pstr"))
    e.if_cmp(e.f("cls"), "==", 1, lambda: e.set("pstat", e.f("pint")))
    e.if_cmp(e.f("cls"), "==", 2, lambda: e.set("pstat", e.f("pdex")))
    e.declare("critc", 0)
    e.assign("critc", e.f("pdex"), ("*", 3), ("+", e.f("eqcrit")))
    e.if_cmp(e.f("cls"), "==", 2, lambda: e.assign("critc", e.f("pdex"), ("*", 5), ("+", e.f("eqcrit"))))
    e.declare("defu", e.f("pdef"))
    e.assign("defu", e.f("pdef"), ("+", e.f("eqdef")))

    e.declare("fighting", 1)
    e.declare("choice", 0)
    e.declare("pdmg", 0)
    e.declare("acted", 0)
    e.declare("fled", 0)
    e.declare("cost", 0)
    e.declare("cr", 0)
    e.declare("eat", 0)
    e.declare("edmg", 0)
    e.declare("roll", 0)
    e.declare("life", 0)

    def loop_body():
        def ppois():
            e.assign("LOOK AT ME.hp", e.f("hp"), ("-", 3))
            e.assign("LOOK AT ME.ppoison", e.f("ppoison"), ("-", 1))
            e.say("  Venom courses through you (-3 HP).")
        e.if_cmp(e.f("ppoison"), ">", 0, ppois)
        boss_phasecheck(e)
        e.say_blank()
        e.say_string(e.lit("  " + A_RED), "ename", e.lit(A_RST + "   HP "), e.num(e.f("ehp")), e.lit("/"), e.num(e.f("emaxhp")))
        e.say_string(e.lit("  You      HP "), e.num(e.f("hp")), e.lit("/"), e.num(e.f("maxhp")),
                     e.lit("   res "), e.num(e.f("res")), e.lit("/"), e.num(e.f("maxres")))
        combat_menu(e)
        e.say("  [1]attack [2]signature [3]skill [4]item [5]brace")
        read_choice(e, "choice")
        e.set("pdmg", 0)
        e.set("acted", 1)
        e.switch("choice", [
            (1, lambda: atk_basic(e)),
            (2, lambda: atk_signature(e)),
            (3, lambda: atk_skill(e)),
            (4, lambda: combat_item(e)),
            (5, lambda: (e.say("  You brace for the blow."), e.assign("LOOK AT ME.pshield", 2))),
        ], default=lambda: (e.say("  You hesitate."), e.set("acted", 0)))

        def apply_pdmg():
            e.if_cmp(e.f("brandbuff"), ">", 0, lambda: (e.assign("pdmg", "pdmg", ("+", 4)),
                                                        e.assign("LOOK AT ME.brandbuff", e.f("brandbuff"), ("-", 1))))
            e.set("cr", Emit.rnd(100))
            e.if_cmp("cr", "<", "critc", lambda: (e.assign("pdmg", "pdmg", ("*", 2)), e.say(col("  *** CRITICAL! ***", "1;33"))))
            e.if_cmp(e.f("wspec"), "==", 6, lambda: combat_execute(e))
            e.assign("LOOK AT ME.ehp", e.f("ehp"), ("-", "pdmg"))
            e.say_string(e.lit("  You hit "), "ename", e.lit(" for "), e.num("pdmg"), e.lit("."))
            def steal():
                e.set("life", "pdmg"); e.assign("life", "pdmg", ("/", 5))
                e.assign("LOOK AT ME.hp", e.f("hp"), ("+", "life")); emit_clamp(e)
            e.if_cmp(e.f("wspec"), "==", 1, steal)
        e.if_cmp("pdmg", ">", 0, apply_pdmg)

        def etick():
            e.assign("LOOK AT ME.ehp", e.f("ehp"), ("-", 3))
            e.assign("LOOK AT ME.epoison", e.f("epoison"), ("-", 1))
        e.if_cmp(e.f("epoison"), ">", 0, etick)

        def boss_dead():
            e.set("fighting", 0)
            boss_defeat(e, xpr, goldr)

        def boss_turn():
            def stunned():
                e.assign("LOOK AT ME.estun", e.f("estun"), ("-", 1))
                e.say_string(e.lit("  "), "ename", e.lit(" reels."))
            def strike():
                e.set("eat", e.f("eatk"))
                # phase scaling: phase2 +atk handled in phasecheck via eatk bump; phase3 heals
                e.if_cmp(e.f("eweak"), ">", 0, lambda: (e.assign("eat", e.f("eatk"), ("-", 3)),
                                                        e.assign("LOOK AT ME.eweak", e.f("eweak"), ("-", 1))))
                e.assign("edmg", "eat", ("+", Emit.rnd(5)), ("-", "defu"), ("-", e.f("pshield")))
                e.if_cmp("edmg", "<", 1, lambda: e.set("edmg", 1))
                e.assign("LOOK AT ME.hp", e.f("hp"), ("-", "edmg"))
                e.say_string(e.lit("  " + A_RED), "ename", e.lit(" strikes you for "), e.num("edmg"), e.lit("." + A_RST))
            e.if_cmp(e.f("estun"), ">", 0, stunned, else_body=strike)
            e.if_cmp(e.f("pshield"), ">", 0, lambda: e.assign("LOOK AT ME.pshield", e.f("pshield"), ("-", 1)))
            def dead():
                e.set("fighting", 0)
                e.assign("LOOK AT ME.mode", M_GAMEOVER)
            e.if_cmp(e.f("hp"), "<", 1, dead, else_body=lambda: regen_res(e))

        def aliveturn():
            def doenemy():
                e.if_cmp("acted", "==", 1, boss_turn)
            e.if_cmp(e.f("ehp"), "<", 1, boss_dead, else_body=doenemy)
        aliveturn()
    e.while_("fighting", loop_body)


def boss_phasecheck(e):
    # transitions at hp thresholds; each boss differs slightly
    e.declare("pct", e.f("ehp"))
    e.assign("pct", e.f("ehp"), ("*", 100), ("/", e.f("emaxhp")))
    # phase 2
    def to2():
        e.assign("LOOK AT ME.bphase", 2)
        cases = []
        for b in C.BOSSES:
            def mk(b=b):
                def inner():
                    e.say_blank(); e.say("  >> " + b["p2"])
                    e.assign("LOOK AT ME.eatk", e.f("eatk"), ("+", 3))
                return inner
            cases.append((b["id"], mk()))
        e.switch(e.f("bossid"), cases)
    def chk2():
        e.declare("p2t", 0)
        # threshold per boss
        cases = []
        for b in C.BOSSES:
            cases.append((b["id"], (lambda v=b["phase2"]: (lambda: e.set("p2t", v)))()))
        e.switch(e.f("bossid"), cases)
        e.if_cmp("pct", "<", "p2t", to2)
    e.if_cmp(e.f("bphase"), "==", 1, chk2)
    # phase 3
    def to3():
        e.assign("LOOK AT ME.bphase", 3)
        cases = []
        for b in C.BOSSES:
            def mk(b=b):
                def inner():
                    e.say_blank(); e.say("  >> " + b["p3"])
                    # phase-3 effect: heal a chunk + raise attack
                    e.assign("LOOK AT ME.ehp", e.f("ehp"), ("+", e.f("emaxhp")), ("/", 1))
                return inner
            # We instead apply a controlled heal below; keep message here
            cases.append((b["id"], mk()))
        # phase-3: heal ~15% and raise attack
        e.declare("h3", e.f("emaxhp"))
        e.assign("h3", e.f("emaxhp"), ("*", 15), ("/", 100))
        e.assign("LOOK AT ME.ehp", e.f("ehp"), ("+", "h3"))
        e.if_cmp(e.f("ehp"), ">", e.f("emaxhp"), lambda: e.assign("LOOK AT ME.ehp", e.f("emaxhp")))
        e.assign("LOOK AT ME.eatk", e.f("eatk"), ("+", 3))
        msgcases = []
        for b in C.BOSSES:
            msgcases.append((b["id"], (lambda b=b: (lambda: (e.say_blank(), e.say("  >> " + b["p3"]))))()))
        e.switch(e.f("bossid"), msgcases)
    def chk3():
        e.declare("p3t", 0)
        cases = []
        for b in C.BOSSES:
            cases.append((b["id"], (lambda v=b["phase3"]: (lambda: e.set("p3t", v)))()))
        e.switch(e.f("bossid"), cases)
        e.if_cmp("pct", "<", "p3t", to3)
    e.if_cmp(e.f("bphase"), "==", 2, chk3)


def boss_defeat(e, xpr, goldr):
    e.say_blank()
    cases = []
    for b in C.BOSSES:
        def mk(b=b):
            def inner():
                for ln in b["defeat"]:
                    e.say("   " + ln)
            return inner
        cases.append((b["id"], mk()))
    e.switch(e.f("bossid"), cases)
    e.assign("LOOK AT ME.xp", e.f("xp"), ("+", xpr))
    e.assign("LOOK AT ME.gold", e.f("gold"), ("+", goldr))
    e.assign("LOOK AT ME.kills", e.f("kills"), ("+", 1))
    e.say_string(e.lit("  " + A_GRN + "+"), e.num(xpr), e.lit(" XP, +"), e.num(goldr), e.lit(" gold." + A_RST))
    # grant boss drop (mark owned) by bossid
    drop_cases = []
    for b in C.BOSSES:
        eff = boss_drop_effect(e, b)
        drop_cases.append((b["id"], (lambda eff=eff: (lambda: eff()))()))
    e.switch(e.f("bossid"), drop_cases)
    check_levelup(e)
    # mark boss dead, advance
    def b0():
        e.assign("LOOK AT ME.b0", 1)
    def b1():
        e.assign("LOOK AT ME.b1", 1)
    def b2():
        e.assign("LOOK AT ME.b2", 1)
    e.switch(e.f("bossid"), [(0, b0), (1, b1), (2, b2)])
    e.assign("LOOK AT ME.isboss", 0)
    e.say_blank()
    e.say("  (press 1 to gather the spoils)")
    read_choice(e, "choice")
    # final boss -> victory; else advance floor+act, save, actintro
    def finalwin():
        e.assign("LOOK AT ME.mode", M_VICTORY)
    def advance():
        e.assign("LOOK AT ME.floor", e.f("floor"), ("+", 1))
        e.assign("LOOK AT ME.act", e.f("act"), ("+", 1))
        save_inline(e)
        e.assign("LOOK AT ME.mode", M_ACTINTRO)
    e.if_cmp(e.f("bossid"), "==", 2, finalwin, else_body=advance)


def boss_drop_effect(e, b):
    # find drop item and mark owned bit; auto-equip
    drop = b["drop"]
    # weapon?
    for cls in (0, 1, 2):
        for idx, w in enumerate(C.WEAPONS[cls]):
            if w[0] == drop:
                def eff(cls=cls, idx=idx, drop=drop):
                    def inner():
                        e.if_cmp(e.f("cls"), "==", cls, (lambda: (
                            e.bit_set("LOOK AT ME.ownw", str(idx)),
                            e.say("  The boss yields the " + drop + "! (owned)"))))
                    return inner
                return eff()
    for idx, a in enumerate(C.ARMORS):
        if a[0] == drop:
            def eff(idx=idx, drop=drop):
                def inner():
                    e.bit_set("LOOK AT ME.owna", str(idx))
                    e.say("  The boss yields the " + drop + "! (owned)")
                return inner
            return eff()
    for idx, t in enumerate(C.TRINKETS):
        if t[0] == drop:
            def eff(idx=idx, drop=drop):
                def inner():
                    e.bit_set("LOOK AT ME.ownt", str(idx))
                    e.say("  The boss yields the " + drop + "! (owned)")
                return inner
            return eff()
    return lambda: None


# -----------------------------------------------------------------  gameover / victory
def gen_gameover(e):
    e.imethod("gameover")
    screen_top(e, "YOU DIED", "the Mirewood drinks another Delver", M_GAMEOVER, tone="1;31")
    e.say(col("    You fall. The Mirewood drinks another Delver.", "31"))
    e.say_string(e.lit("    Level "), e.num(e.f("level")), e.lit(", "), e.num(e.f("kills")), e.lit(" kills, floor "),
                 e.num(e.f("floor")), e.lit("."))
    e.say_blank()
    e.say("  (press 1 to return)")
    e.declare("c", 0)
    read_choice(e, "c")
    e.declare("hs", e.file_exists(SAVE))
    def reload():
        e.say("    The dark spits you back to your last camp...")
        load_inline(e)
        e.assign("LOOK AT ME.hp", e.f("maxhp"))
        e.assign("LOOK AT ME.res", e.f("maxres"))
        e.assign("LOOK AT ME.ppoison", 0)
        e.assign("LOOK AT ME.mode", M_CAMP)
    def restart():
        e.say("    No camp was saved. Your tale ends here.")
        e.assign("LOOK AT ME.mode", M_TITLE)
        e.assign("LOOK AT ME.started", 0)
    e.if_cmp("hs", "==", 1, reload, else_body=restart)
    e.end_imethod()


def gen_victory(e):
    e.imethod("victory")
    screen_top(e, "VICTORY", "the Sunken Crown is broken", M_VICTORY, tone="1;33")
    e.say("  ================================================================")
    for ln in C.ENDING:
        e.say("   " + ln) if ln else e.say_blank()
    e.say("  ================================================================")
    e.say_string(e.lit("   Final: Level "), e.num(e.f("level")), e.lit(", "), e.num(e.f("kills")),
                 e.lit(" kills, "), e.num(e.f("gold")), e.lit(" gold."))
    e.say_blank()
    e.delete_file(SAVE)
    e.assign("LOOK AT ME.mode", M_QUIT)
    e.end_imethod()


# -----------------------------------------------------------------  save / load
def save_inline(e):
    sv = e.tmp("s")
    parts = []
    for i, fld in enumerate(SAVE_FIELDS):
        parts.append(e.num(e.f(fld)))
        if i != len(SAVE_FIELDS) - 1:
            parts.append(e.lit("#"))
    e.string(sv, *parts)
    e.write_file(sv, SAVE)


def load_inline(e):
    ld = e.tmp("s"); sf = e.tmp("a")
    e.string(ld, e.read_file(SAVE))
    e.split(sf, ld, "#")
    for i, fld in enumerate(SAVE_FIELDS):
        e.assign("LOOK AT ME." + fld, Emit.parse(Emit.aget(sf, i)))
    e.assign("LOOK AT ME.started", 1)
    e.assign("LOOK AT ME.room", 0)
    emit_recompute_weapon(e)
    emit_recompute_equip(e)
    e.assign("LOOK AT ME.mode", M_CAMP)


# -----------------------------------------------------------------  main conductor
def gen_main(e):
    e.begin_main()
    e.comment("conductor: owns the dispatch loop; only main can call screens")
    e.raw(K["new"] + " g " + K["as"] + " Game")
    e.call_method("g", "boot")
    e.declare("running", 1)

    def loop():
        e.declare("m", "g.mode")
        e.switch("m", [
            (M_TITLE, lambda: e.call_method("g", "title")),
            (M_CLASS, lambda: (e.call_method("g", "classSelect"), e.call_method("g", "initclass"))),
            (M_ACTINTRO, lambda: e.call_method("g", "actintro")),
            (M_CAMP, lambda: e.call_method("g", "camp")),
            (M_SHOP, lambda: e.call_method("g", "shop")),
            (M_EQUIP, lambda: e.call_method("g", "equip")),
            (M_EXPLORE, lambda: e.call_method("g", "explore")),
            (M_COMBAT, lambda: e.call_method("g", "combat")),
            (M_EVENT, lambda: e.call_method("g", "event")),
            (M_BOSS, lambda: e.call_method("g", "bossfight")),
            (M_GAMEOVER, lambda: e.call_method("g", "gameover")),
            (M_VICTORY, lambda: e.call_method("g", "victory")),
        ], default=lambda: e.set("running", 0))
        e.declare("q", "g.mode")
        e.cmp("running", "q", "!=", M_QUIT, declare=False)
        e.comment("record the screen we just left for the next screen's breadcrumb")
        e.call_method("g", "setcrumb", "m")
    e.while_("running", loop)
    e.say_blank()
    e.say("  Hasta la vista, Delver.")
    e.end_main()
    e.blank()


def gen_helpers(e):
    pass


if __name__ == "__main__":
    build()
