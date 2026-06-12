package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class LessOrEqualNode(operand1: AstNode, operand2: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    ComparisonCodegen.generate(mv, symbolTable, operand1, operand2,
      IF_ICMPLE, FCMPG, IFLE, None, "BENEATH YOU")
  }
}
