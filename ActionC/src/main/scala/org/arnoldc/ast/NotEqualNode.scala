package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class NotEqualNode(operand1: AstNode, operand2: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    ComparisonCodegen.generate(mv, symbolTable, operand1, operand2,
      IF_ICMPNE, FCMPL, IFNE, Some(true), "IT'S JUST BEEN REVOKED")
  }
}
