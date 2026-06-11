package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable

// I AM THE LAW <condition> [ "message" ]
// Throws an AssertionError (with the optional message) when the condition is false.
case class AssertNode(condition: OperandNode, message: Option[StringNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val ok = new Label()
    condition.generate(mv, symbolTable)
    mv.visitJumpInsn(IFNE, ok)

    mv.visitTypeInsn(NEW, "java/lang/AssertionError")
    mv.visitInsn(DUP)
    message.getOrElse(StringNode("Assertion failed")).generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/AssertionError", "<init>", "(Ljava/lang/Object;)V")
    mv.visitInsn(ATHROW)

    mv.visitLabel(ok)
  }
}
