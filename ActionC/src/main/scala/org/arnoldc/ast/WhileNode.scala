package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable

case class WhileNode(condition: OperandNode, statements: List[AstNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val loopStart = new Label()
    val loopEnd = new Label()

    mv.visitLabel(loopStart)
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFEQ, loopEnd)
    // continue re-checks the condition; break exits the loop
    symbolTable.enterLoop(loopStart, loopEnd)
    statements.foreach(_.generate(mv, symbolTable))
    symbolTable.exitLoop()
    mv.visitJumpInsn(GOTO, loopStart)
    mv.visitLabel(loopEnd)
  }
}
