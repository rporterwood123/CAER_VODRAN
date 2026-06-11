package org.arnoldc.ast

import org.objectweb.asm.{Label, MethodVisitor}
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class CallReadMethodNode(returnVar: String) extends StatementNode{
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Read an int from a SINGLE shared Scanner over System.in (lazily created and
    // stored in the main class's static ACTIONC_IN field). Reusing one Scanner is
    // essential: a fresh Scanner per read buffers and discards the rest of the
    // stream, which breaks every multi-read program under piped/redirected input.
    val owner = symbolTable.getFileName()
    val ready = new Label()

    mv.visitFieldInsn(GETSTATIC, owner, "ACTIONC_IN", "Ljava/util/Scanner;")
    mv.visitJumpInsn(IFNONNULL, ready)
    mv.visitTypeInsn(NEW, "java/util/Scanner")
    mv.visitInsn(DUP)
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;")
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V")
    mv.visitFieldInsn(PUTSTATIC, owner, "ACTIONC_IN", "Ljava/util/Scanner;")
    mv.visitLabel(ready)

    mv.visitFieldInsn(GETSTATIC, owner, "ACTIONC_IN", "Ljava/util/Scanner;")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I")
    mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(returnVar))
  }
}
