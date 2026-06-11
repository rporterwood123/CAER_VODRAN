package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// WHAT TIME IS IT  -> (int) System.currentTimeMillis()
// Returns the low 32 bits of the current epoch-millis as an int.
case class TimeNode() extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J")
    mv.visitInsn(L2I)
  }
}

// CHILL OUT FOR <ms>  -> Thread.sleep(ms)
case class SleepNode(millis: OperandNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    millis.generate(mv, symbolTable)
    mv.visitInsn(I2L)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V")
  }
}
