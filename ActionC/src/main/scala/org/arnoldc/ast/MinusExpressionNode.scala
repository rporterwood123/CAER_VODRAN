package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class MinusExpressionNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    if (TypeInference.isFloat(this, symbolTable)) {
      TypeInference.generateAsFloat(expression, mv, symbolTable)
      TypeInference.generateAsFloat(operand, mv, symbolTable)
      mv.visitInsn(FSUB)
    } else {
      expression.generate(mv, symbolTable)
      operand.generate(mv, symbolTable)
      mv.visitInsn(ISUB)
    }
  }
}
