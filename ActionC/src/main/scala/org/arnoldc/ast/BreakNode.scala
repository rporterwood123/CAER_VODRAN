package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class BreakNode() extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitJumpInsn(GOTO, symbolTable.currentBreakLabel)
  }
}
