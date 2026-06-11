package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable

case class LessOrEqualNode(operand1: AstNode, operand2: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val trueLabel = new Label()
    val conclude = new Label()
    operand1.generate(mv, symbolTable)
    operand2.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPLE, trueLabel)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, conclude)
    mv.visitLabel(trueLabel)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(conclude)
  }
}
