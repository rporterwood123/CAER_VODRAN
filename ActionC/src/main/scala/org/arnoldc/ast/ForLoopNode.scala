package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable

// LET'S ROCK <counter> FROM <start> TO <end> ... GAME OVER MAN GAME OVER
// Inclusive range: the body runs for counter = start, start+1, ..., end.
case class ForLoopNode(counterName: String, start: OperandNode, end: OperandNode, statements: List[AstNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    if (!symbolTable.containsVariable(counterName)) {
      symbolTable.putVariable(counterName)
    }
    val address = symbolTable.getVariableAddress(counterName)

    val loopStart = new Label()
    val continueLabel = new Label()
    val loopEnd = new Label()

    // counter = start
    start.generate(mv, symbolTable)
    mv.visitVarInsn(ISTORE, address)

    mv.visitLabel(loopStart)
    // if (counter > end) exit
    mv.visitVarInsn(ILOAD, address)
    end.generate(mv, symbolTable)
    mv.visitJumpInsn(IF_ICMPGT, loopEnd)

    // continue jumps to the increment so the counter still advances
    symbolTable.enterLoop(continueLabel, loopEnd)
    statements.foreach(_.generate(mv, symbolTable))
    symbolTable.exitLoop()

    // counter++
    mv.visitLabel(continueLabel)
    mv.visitIincInsn(address, 1)
    mv.visitJumpInsn(GOTO, loopStart)
    mv.visitLabel(loopEnd)
  }
}
