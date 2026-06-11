package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// Bitwise binary operators. Each consumes the running expression value and one
// operand, like the arithmetic nodes.

case class BitwiseAndNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    expression.generate(mv, symbolTable)
    operand.generate(mv, symbolTable)
    mv.visitInsn(IAND)
  }
}

case class BitwiseOrNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    expression.generate(mv, symbolTable)
    operand.generate(mv, symbolTable)
    mv.visitInsn(IOR)
  }
}

case class BitwiseXorNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    expression.generate(mv, symbolTable)
    operand.generate(mv, symbolTable)
    mv.visitInsn(IXOR)
  }
}

case class LeftShiftNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    expression.generate(mv, symbolTable)
    operand.generate(mv, symbolTable)
    mv.visitInsn(ISHL)
  }
}

case class RightShiftNode(expression: AstNode, operand: AstNode) extends AstNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    expression.generate(mv, symbolTable)
    operand.generate(mv, symbolTable)
    mv.visitInsn(ISHR)
  }
}
