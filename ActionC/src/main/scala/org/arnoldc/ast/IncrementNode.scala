package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable

case class IncrementNode(variableName: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitIincInsn(symbolTable.getVariableAddress(variableName), 1)
  }
}
