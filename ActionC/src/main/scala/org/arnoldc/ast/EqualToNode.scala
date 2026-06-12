package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class EqualToNode(operand1: AstNode, operand2: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    ComparisonCodegen.generate(mv, symbolTable, operand1, operand2,
      IF_ICMPEQ, FCMPL, IFEQ, Some(false), "YOU ARE NOT YOU YOU ARE ME")
  }
}
