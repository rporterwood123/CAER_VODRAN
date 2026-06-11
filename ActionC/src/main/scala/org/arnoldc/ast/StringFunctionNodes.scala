package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// Standard-library string functions, compiled to java/lang/String calls.

// --- String-returning (usable wherever a string operand is expected) ---

// SAY IT LOUDER <str>  -> str.toUpperCase()
case class UpperNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toUpperCase", "()Ljava/lang/String;")
  }
}

// KEEP YOUR VOICE DOWN <str>  -> str.toLowerCase()
case class LowerNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;")
  }
}

// CUT THE FAT FROM <str>  -> str.trim()
case class TrimNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "trim", "()Ljava/lang/String;")
  }
}

// SPELL IT OUT <n>  -> String.valueOf(n). Type-aware: floats use the float overload
// so they stringify as e.g. "3.5"; everything else uses the int overload.
case class NumToStringNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    arg.generate(mv, symbolTable)
    if (TypeInference.isFloat(arg, symbolTable)) {
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(F)Ljava/lang/String;")
    } else {
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;")
    }
  }
}

// GIVE ME A PIECE OF <str> FROM <begin> TO <end>  -> str.substring(begin, end)
case class SubstringNode(str: AstNode, begin: OperandNode, end: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    begin.generate(mv, symbolTable)
    end.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(II)Ljava/lang/String;")
  }
}

// GET A NEW ONE <str> <target> <repl>  -> str.replace(target, repl) (all occurrences)
case class ReplaceNode(str: AstNode, target: AstNode, repl: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    target.generate(mv, symbolTable)
    repl.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace",
      "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;")
  }
}

// SHOW ME THE ONE AT <str> <index>  -> the one-character string at that index
case class CharAtNode(str: AstNode, index: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    index.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C")
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String;")
  }
}

// PUT IT IN REVERSE <str>  -> new StringBuilder(str).reverse().toString()
case class ReverseNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
    mv.visitInsn(DUP)
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "reverse", "()Ljava/lang/StringBuilder;")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;")
  }
}

// --- Int-returning (usable wherever an integer operand is expected) ---

// FIRST BLOOD <str> <prefix>  -> str.startsWith(prefix) ? 1 : 0
case class StartsWithNode(str: AstNode, prefix: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    prefix.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z")
  }
}

// LAST MAN STANDING <str> <suffix>  -> str.endsWith(suffix) ? 1 : 0
case class EndsWithNode(str: AstNode, suffix: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    suffix.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", "(Ljava/lang/String;)Z")
  }
}

// HOW LONG IS THIS THING <str>  -> str.length()
case class LengthNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I")
  }
}

// YOU TALKING TO ME ABOUT <str> <sub>  -> str.contains(sub) ? 1 : 0
case class ContainsNode(str: AstNode, sub: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    sub.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z")
  }
}

// DO THE MATH <str>  -> Integer.parseInt(str)
case class ParseIntNode(str: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    str.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I")
  }
}

// WHERE IS IT IN <haystack> <needle>  -> haystack.indexOf(needle)
case class IndexOfNode(haystack: AstNode, needle: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    haystack.generate(mv, symbolTable)
    needle.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I")
  }
}
