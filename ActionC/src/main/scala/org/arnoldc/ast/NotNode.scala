package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class NotNode(operand: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    operand.generate(mv, symbolTable)
    mv.visitInsn(ICONST_1)
    mv.visitInsn(IXOR)
  }
}
