package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// Standard-library math functions, compiled to java/lang/Math calls. All operate
// on and return ints (sqrt/pow go through double internally and truncate).

// NO MORE HALF MEASURES <n>  -> abs(n)
case class AbsNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    arg.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(I)I")
  }
}

// GET TO THE ROOT OF <n>  -> (int) sqrt(n)
case class SqrtNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    arg.generate(mv, symbolTable)
    mv.visitInsn(I2D)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sqrt", "(D)D")
    mv.visitInsn(D2I)
  }
}

// MAXIMUM EFFORT OF <a> <b>  -> max(a, b)
case class MaxNode(a: OperandNode, b: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    a.generate(mv, symbolTable)
    b.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "max", "(II)I")
  }
}

// MINIMAL CASUALTIES OF <a> <b>  -> min(a, b)
case class MinNode(a: OperandNode, b: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    a.generate(mv, symbolTable)
    b.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(II)I")
  }
}

// UNLIMITED POWER OF <base> <exp>  -> (int) pow(base, exp)
case class PowNode(base: OperandNode, exp: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    base.generate(mv, symbolTable)
    mv.visitInsn(I2D)
    exp.generate(mv, symbolTable)
    mv.visitInsn(I2D)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "pow", "(DD)D")
    mv.visitInsn(D2I)
  }
}

// GO AHEAD MAKE MY DAY <bound>  -> a random int in [0, bound)
case class RandomNode(bound: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "random", "()D")
    bound.generate(mv, symbolTable)
    mv.visitInsn(I2D)
    mv.visitInsn(DMUL)
    mv.visitInsn(D2I)
  }
}

// HIT THE FLOOR <f>  -> (int) floor(f). Doubles as a float->int (truncate-toward
// -inf) conversion. The argument is promoted to float if it is an int.
case class FloorNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    TypeInference.generateAsFloat(arg, mv, symbolTable)
    mv.visitInsn(F2D)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "floor", "(D)D")
    mv.visitInsn(D2I)
  }
}

// THROUGH THE ROOF <f>  -> (int) ceil(f)
case class CeilNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    TypeInference.generateAsFloat(arg, mv, symbolTable)
    mv.visitInsn(F2D)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ceil", "(D)D")
    mv.visitInsn(D2I)
  }
}

// ROUND THEM UP <f>  -> round(f) to nearest int (half up)
case class RoundNode(arg: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    TypeInference.generateAsFloat(arg, mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "round", "(F)I")
  }
}
