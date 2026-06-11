package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

case class FloatNode(value: Float) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitLdcInsn(java.lang.Float.valueOf(value))
  }
}
