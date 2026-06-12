package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class CallReadMethodNode(returnVar: String) extends StatementNode{
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    mv.visitFieldInsn(GETSTATIC, symbolTable.getFileName(), "$scanner", "Ljava/util/Scanner;")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I")
    mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(returnVar))
  }
}
