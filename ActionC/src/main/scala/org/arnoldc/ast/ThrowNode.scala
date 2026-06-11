package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

// WELCOME TO THE PARTY PAL <message>
// Throws a RuntimeException carrying the given string message.
case class ThrowNode(message: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitTypeInsn(NEW, "java/lang/RuntimeException")
    mv.visitInsn(DUP)
    message.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V")
    mv.visitInsn(ATHROW)
  }
}
