"""
emit.py — A small, correct emitter for ActionC source.

ActionC (a superset of ArnoldC) is an esoteric language whose keywords are
action-movie one-liners. This module wraps the verbose, easy-to-get-wrong syntax
in Python helpers so the generated game is consistent and provably correct.

Hard language facts this emitter respects (verified against the compiler):
  * Reads from stdin are int-only (Scanner.nextInt).
  * Methods/instance-methods take only int args and return int or void.
  * Object fields are int-only. There are no globals.
  * `if`/`while` take a SINGLE pre-computed boolean operand (no inline compares).
  * Arithmetic in an assignment block is left-to-right, NO precedence.
  * print == println (newline every time) -> build whole lines, print once.
  * String literals may NOT contain backslashes or double-quotes.
  * Strings have no reassignment statement: a given string var name is declared
    ONCE per method (re-declaration is a duplicate-variable error). Use fresh
    names; inside a loop a single declaration is fine (one slot, re-stored).
"""

# ---- keyword constants (exact phrases from ArnoldParser.scala) ----
K = {
    "main_begin": "IT'S SHOWTIME",
    "main_end": "YOU HAVE BEEN TERMINATED",
    "print": "TALK TO THE HAND",
    "decl_int": "HEY CHRISTMAS TREE",
    "set_init": "YOU SET US UP",
    "assign": "GET TO THE CHOPPER",
    "set_val": "HERE IS MY INVITATION",
    "end_assign": "ENOUGH TALK",
    "plus": "GET UP",
    "minus": "GET DOWN",
    "mul": "YOU'RE FIRED",
    "div": "HE HAD TO SPLIT",
    "mod": "I LET HIM GO",
    "eq": "YOU ARE NOT YOU YOU ARE ME",
    "ne": "IT'S JUST BEEN REVOKED",
    "gt": "LET OFF SOME STEAM BENNET",
    "lt": "YOU'RE THE DISEASE AND I'M THE CURE",
    "ge": "I'M GETTING TOO OLD FOR THIS",
    "le": "BENEATH YOU",
    "or": "CONSIDER THAT A DIVORCE",
    "and": "KNOCK KNOCK",
    "not": "NEGATIVE",
    "band": "WINNERS GO HOME AND DATE THE PROM QUEEN",
    "bor": "DEAD OR ALIVE YOU'RE COMING WITH ME",
    "bxor": "FRIEND OR FOE",
    "shl": "MOVE IT",
    "shr": "FALL BACK",
    "if": "BECAUSE I'M GOING TO SAY PLEASE",
    "else": "BULLSHIT",
    "endif": "YOU HAVE NO RESPECT FOR LOGIC",
    "while": "STICK AROUND",
    "endwhile": "CHILL",
    "for": "LET'S ROCK",
    "from": "FROM",
    "to": "TO",
    "endfor": "GAME OVER MAN GAME OVER",
    "break": "GET OUT",
    "continue": "KEEP MOVING",
    "switch": "CHOOSE YOUR DESTINY",
    "case": "WHAT IF I TOLD YOU",
    "default": "SAME OLD SAME OLD",
    "endswitch": "FINISH HIM",
    "method": "LISTEN TO ME VERY CAREFULLY",
    "arg": "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE",
    "returns": "GIVE THESE PEOPLE AIR",
    "return": "I'LL BE BACK",
    "end_method": "HASTA LA VISTA, BABY",
    "call": "DO IT NOW",
    "call_assign": "GET YOUR ASS TO MARS",
    "read": "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY",
    "class": "MY NAME IS MAXIMUS",
    "end_class": "STRENGTH AND HONOR",
    "inherits": "LIKE FATHER LIKE SON",
    "field_pub": "OPEN TO THE PUBLIC",
    "field_priv": "THAT'S CLASSIFIED",
    "ctor": "IT'S ALIVE",
    "end_ctor": "BIRTH COMPLETE",
    "new": "WELCOME TO EARTH",
    "as": "AS",
    "this": "LOOK AT ME",
    "imethod": "COMMANDER IN CHIEF",
    "end_imethod": "DISMISSED SOLDIER",
    "incr": "ONE MORE TIME",
    "decr": "COUNTDOWN",
    "str_decl": "I HAVE COME HERE TO CHEW BUBBLEGUM",
    "str_app": "AND KICK ASS",
    "num2str": "SPELL IT OUT",
    "parseint": "DO THE MATH",
    "arr_decl": "I AIN'T GOT TIME TO BLEED",
    "arr_size": "UGLY MOTHERFUCKERS",
    "arr_with": "WITH",
    "arr_at": "GET IN LINE",
    "at": "AT",
    "arr_len": "HOW MANY OF THEM",
    "split": "DIVIDE AND CONQUER",
    "rand": "GO AHEAD MAKE MY DAY",
    "abs": "NO MORE HALF MEASURES",
    "sqrt": "GET TO THE ROOT OF",
    "max": "MAXIMUM EFFORT OF",
    "min": "MINIMAL CASUALTIES OF",
    "pow": "UNLIMITED POWER OF",
    "file_read": "WHAT'S IN THE BOX",
    "file_write": "WRITE THAT DOWN",
    "file_write_to": "TO",
    "file_exists": "HONEY I'M HOME",
    "file_delete": "SEAL THE EXITS",
    "comment": "I'M BATMAN",
}

CMP = {"==": "eq", "!=": "ne", ">": "gt", "<": "lt", ">=": "ge", "<=": "le"}
ARITH = {"+": "plus", "-": "minus", "*": "mul", "/": "div", "%": "mod"}


def safe_str(s):
    """Strings can't contain backslashes or double quotes; strip/replace them."""
    return str(s).replace("\\", "/").replace('"', "'")


class Emit:
    """Accumulates ActionC source lines with correct, composable idioms."""

    def __init__(self):
        self.lines = []
        self.ind = 0
        self._t = 0  # temp counter, reset per method via fresh_scope()
        self._declared = set()  # int locals declared in the current method

    # ---- low level ----
    def raw(self, s=""):
        self.lines.append(("    " * self.ind) + s if s else "")
        return self

    def kw(self, *parts):
        """Emit a line of space-joined tokens, translating known keys via K when wrapped."""
        self.raw(" ".join(str(p) for p in parts))
        return self

    def blank(self):
        self.lines.append("")
        return self

    def comment(self, text):
        self.raw(K["comment"] + " " + safe_str(text).replace("\n", " "))
        return self

    def fresh_scope(self):
        self._t = 0
        self._declared = set()

    def tmp(self, base="t"):
        # VariableName must be letters/digits only (no underscore), start with a letter.
        self._t += 1
        return "zz%s%d" % (base, self._t)

    def text(self):
        return "\n".join(self.lines) + "\n"

    # ---- structure ----
    def begin_main(self):
        self.raw(K["main_begin"]); self.ind += 1; return self

    def end_main(self):
        self.ind -= 1; self.raw(K["main_end"]); return self

    def static_method(self, name, args, returns):
        """Open a static (global) method. args: list of arg names. returns: bool."""
        self.fresh_scope()
        self.raw(K["method"] + " " + name)
        for a in args:
            self.raw(K["arg"] + " " + a)
        if returns:
            self.raw(K["returns"])
        self.ind += 1
        return self

    def end_static_method(self):
        self.ind -= 1
        self.raw(K["end_method"])
        self.blank()
        return self

    def begin_class(self, name, parent=None):
        hdr = K["class"] + " " + name
        if parent:
            hdr += " " + K["inherits"] + " " + parent
        self.raw(hdr); self.ind += 1; return self

    def field(self, name, public=True):
        self.raw((K["field_pub"] if public else K["field_priv"]) + " " + name); return self

    def ctor(self):
        self.fresh_scope(); self.raw(K["ctor"]); self.ind += 1; return self

    def end_ctor(self):
        self.ind -= 1; self.raw(K["end_ctor"]); return self

    def imethod(self, name, args=(), returns=False):
        self.fresh_scope()
        self.raw(K["imethod"] + " " + name)
        for a in args:
            self.raw(K["arg"] + " " + a)
        if returns:
            self.raw(K["returns"])
        self.ind += 1
        return self

    def end_imethod(self):
        self.ind -= 1; self.raw(K["end_imethod"]); self.blank(); return self

    def end_class(self):
        self.ind -= 1; self.raw(K["end_class"]); self.blank(); return self

    # ---- ints ----
    def declare(self, name, operand="0"):
        # Scope-aware: ActionC's symbol table is flat per method and forbids
        # re-declaring a name. If we've already declared this int local in the
        # current method, emit a reassignment instead (the slot already exists).
        if name in self._declared:
            return self.set(name, operand)
        self._declared.add(name)
        self.raw(K["decl_int"] + " " + name)
        self.raw(K["set_init"] + " " + str(operand))
        return self

    def assign(self, target, first, *ops):
        """target = first [op operand]...  (left-to-right). ops are ('+', operand) tuples
        or pre-formatted strings 'KEY operand'. target may be 'x', 'LOOK AT ME.f', 'o.f'."""
        self.raw(K["assign"] + " " + target)
        self.raw(K["set_val"] + " " + str(first))
        for op in ops:
            if isinstance(op, tuple):
                sym, val = op
                self.raw(K[ARITH[sym]] + " " + str(val))
            else:
                self.raw(op)
        self.raw(K["end_assign"])
        return self

    def set(self, target, operand):
        """target = operand (single operand)."""
        return self.assign(target, operand)

    def incr(self, name):
        self.raw(K["incr"] + " " + name); return self

    def decr(self, name):
        self.raw(K["decr"] + " " + name); return self

    # ---- booleans / comparisons ----
    def cmp(self, target, left, op, right, declare=True):
        """target = (left <op> right). op in CMP keys."""
        if declare:
            self.declare(target, "0")
        self.raw(K["assign"] + " " + target)
        self.raw(K["set_val"] + " " + str(left))
        self.raw(K[CMP[op]] + " " + str(right))
        self.raw(K["end_assign"])
        return self

    def new_cmp(self, left, op, right):
        """Declare a fresh temp = (left op right); return its name."""
        t = self.tmp("b")
        self.cmp(t, left, op, right, declare=True)
        return t

    def logic(self, target, left, opkey, right, declare=True):
        """target = (left <logic/bit op> right). opkey in K (and/or/band/bor/shl/shr...)."""
        if declare:
            self.declare(target, "0")
        self.raw(K["assign"] + " " + target)
        self.raw(K["set_val"] + " " + str(left))
        self.raw(K[opkey] + " " + str(right))
        self.raw(K["end_assign"])
        return self

    def bit_test(self, mask_operand, index_operand):
        """Return a fresh bool var name = ((mask >> index) & 1) != 0  (bit set?)."""
        sh = self.tmp("m")
        self.logic(sh, mask_operand, "shr", index_operand, declare=True)
        an = self.tmp("m")
        self.logic(an, sh, "band", 1, declare=True)
        b = self.tmp("b")
        self.cmp(b, an, "==", 1, declare=True)
        return b

    def bit_set(self, target_mask, index_operand):
        """target_mask = target_mask | (1 << index)."""
        sh = self.tmp("m")
        self.logic(sh, 1, "shl", index_operand, declare=True)
        self.logic(target_mask, target_mask, "bor", sh, declare=False)
        return self

    # ---- control flow (callbacks emit the body) ----
    def if_(self, cond_var, then_body, else_body=None):
        self.raw(K["if"] + " " + cond_var)
        self.ind += 1; then_body(); self.ind -= 1
        if else_body is not None:
            self.raw(K["else"])
            self.ind += 1; else_body(); self.ind -= 1
        self.raw(K["endif"])
        return self

    def if_cmp(self, left, op, right, then_body, else_body=None):
        t = self.new_cmp(left, op, right)
        return self.if_(t, then_body, else_body)

    def while_(self, cond_var, body):
        self.raw(K["while"] + " " + cond_var)
        self.ind += 1; body(); self.ind -= 1
        self.raw(K["endwhile"])
        return self

    def for_(self, var, lo, hi, body):
        self.raw(K["for"] + " " + var + " " + K["from"] + " " + str(lo) + " " + K["to"] + " " + str(hi))
        self.ind += 1; body(); self.ind -= 1
        self.raw(K["endfor"])
        return self

    def switch(self, operand, cases, default=None):
        """cases: list of (intliteral, body_callable). default: body_callable or None."""
        self.raw(K["switch"] + " " + str(operand))
        self.ind += 1
        for val, body in cases:
            self.raw(K["case"] + " " + str(val))
            self.ind += 1; body(); self.ind -= 1
        if default is not None:
            self.raw(K["default"])
            self.ind += 1; default(); self.ind -= 1
        self.ind -= 1
        self.raw(K["endswitch"])
        return self

    def break_(self):
        self.raw(K["break"]); return self

    # ---- printing ----
    def say(self, literal):
        self.raw(K["print"] + ' "' + safe_str(literal) + '"'); return self

    def say_var(self, operand):
        self.raw(K["print"] + " " + str(operand)); return self

    def say_blank(self):
        self.raw(K["print"] + ' ""'); return self

    # ---- strings ----
    def string(self, name, *parts):
        """Declare string `name` = concatenation of parts. Each part is a StringOperand
        produced by str_lit()/num()/svar()/arr() helpers below."""
        self.raw(K["str_decl"] + " " + name)
        for p in parts:
            self.raw(K["str_app"] + " " + p)
        return self

    def say_string(self, *parts):
        """Declare a temp string from parts and print it."""
        t = self.tmp("s")
        self.string(t, *parts)
        self.say_var(t)
        return self

    # string-operand builders (return source fragments) ----
    @staticmethod
    def lit(s):
        return '"' + safe_str(s) + '"'

    @staticmethod
    def num(operand):
        return K["num2str"] + " " + str(operand)

    @staticmethod
    def sval(name):
        return str(name)

    # ---- arrays ----
    def int_array(self, name, size):
        self.raw(K["arr_decl"] + " " + name + " " + K["arr_with"] + " " + str(size) + " " + K["arr_size"])
        return self

    def aset(self, name, idx, first, *ops):
        self.raw(K["arr_at"] + " " + name + " " + K["at"] + " " + str(idx))
        self.raw(K["set_val"] + " " + str(first))
        for op in ops:
            if isinstance(op, tuple):
                sym, val = op
                self.raw(K[ARITH[sym]] + " " + str(val))
            else:
                self.raw(op)
        self.raw(K["end_assign"])
        return self

    @staticmethod
    def aget(name, idx):
        return K["arr_at"] + " " + name + " " + K["at"] + " " + str(idx)

    @staticmethod
    def alen(name):
        return K["arr_len"] + " " + name

    def split(self, name, source_operand, delim):
        self.raw(K["split"] + " " + name + " " + str(source_operand) + ' "' + safe_str(delim) + '"')
        return self

    @staticmethod
    def parse(string_operand):
        return K["parseint"] + " " + str(string_operand)

    # ---- math operands (return fragments) ----
    @staticmethod
    def rnd(n):
        return K["rand"] + " " + str(n)

    @staticmethod
    def mmax(a, b):
        return K["max"] + " " + str(a) + " " + str(b)

    @staticmethod
    def mmin(a, b):
        return K["min"] + " " + str(a) + " " + str(b)

    @staticmethod
    def mabs(a):
        return K["abs"] + " " + str(a)

    # ---- this-field helpers ----
    @staticmethod
    def f(field):
        """Read this.field as an operand."""
        return K["this"] + "." + field

    @staticmethod
    def of(obj, field):
        return obj + "." + field

    # ---- method calls ----
    def call(self, name, *args):
        self.raw(K["call"] + " " + name + ("".join(" " + str(a) for a in args)))
        return self

    def call_into(self, target, name, *args):
        self.raw(K["call_assign"] + " " + target)
        self.raw(K["call"] + " " + name + ("".join(" " + str(a) for a in args)))
        return self

    def call_method(self, obj, method, *args):
        self.raw(K["call"] + " " + obj + "." + method + ("".join(" " + str(a) for a in args)))
        return self

    def call_method_into(self, target, obj, method, *args):
        self.raw(K["call_assign"] + " " + target)
        self.raw(K["call"] + " " + obj + "." + method + ("".join(" " + str(a) for a in args)))
        return self

    def read_into(self, var):
        """Read an int from stdin into already-declared `var`."""
        self.raw(K["call_assign"] + " " + var)
        self.raw(K["call"])
        self.raw(K["read"])
        return self

    # ---- file io ----
    def write_file(self, content_operand, path_literal):
        self.raw(K["file_write"] + " " + str(content_operand) + " " + K["file_write_to"] + ' "' + safe_str(path_literal) + '"')
        return self

    @staticmethod
    def read_file(path_literal):
        return K["file_read"] + ' "' + safe_str(path_literal) + '"'

    @staticmethod
    def file_exists(path_literal):
        return K["file_exists"] + ' "' + safe_str(path_literal) + '"'

    def delete_file(self, path_literal):
        self.raw(K["file_delete"] + ' "' + safe_str(path_literal) + '"')
        return self
