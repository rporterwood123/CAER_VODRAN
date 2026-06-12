package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class LessThanNode(operand1: AstNode, operand2: AstNode) extends ExpressionNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    ComparisonCodegen.generate(mv, symbolTable, operand1, operand2,
      IF_ICMPLT, FCMPG, IFLT, None, "YOU'RE THE DISEASE AND I'M THE CURE")
  }
}
