package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

object MissingReturn {
  // Fall-through epilogue for value-returning methods that reach the end without
  // an I'LL BE BACK: throw instead of silently returning a default.
  def throwMissingReturn(mv: MethodVisitor, methodName: String) {
    mv.visitTypeInsn(NEW, "java/lang/IllegalStateException")
    mv.visitInsn(DUP)
    mv.visitLdcInsn("METHOD " + methodName + " ENDED WITHOUT I'LL BE BACK")
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalStateException", "<init>", "(Ljava/lang/String;)V")
    mv.visitInsn(ATHROW)
  }
}

case class MethodNode(methodName: String, arguments: List[VariableNode], returnsValue: Boolean, statements: List[StatementNode]) extends AbstractMethodNode {

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    val methodSymbolTable = new SymbolTable(Some(symbolTable), methodName)
    mv.visitCode()
    arguments.foreach {
      a =>
        methodSymbolTable.putVariable(a.variableName)
    }
    statements.foreach(_.generate(mv, methodSymbolTable))
    if (!returnsValue) {
      mv.visitInsn(RETURN)
    }
    else {
      // Falling off the end of a value-returning method is a program bug; fail
      // loudly instead of silently returning a default value.
      MissingReturn.throwMissingReturn(mv, methodName)
    }
    mv.visitMaxs(100, 100)
    mv.visitEnd()
  }

}
