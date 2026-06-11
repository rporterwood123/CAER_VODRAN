package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType

// LET'S SEE WHAT YOU'VE GOT ... GOTCHA <var> ... [CLEAN UP ON AISLE FIVE ...] THAT'S A WRAP
// The catch variable is bound to the caught exception's message (a String).
// The finally block (if present) runs on both the normal and the caught path.
case class TryNode(tryBody: List[AstNode], catchVar: String, catchBody: List[AstNode],
                   finallyBody: Option[List[AstNode]]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val start = new Label()
    val end = new Label()
    val handler = new Label()
    val done = new Label()

    mv.visitTryCatchBlock(start, end, handler, "java/lang/Exception")

    mv.visitLabel(start)
    tryBody.foreach(_.generate(mv, symbolTable))
    mv.visitLabel(end)
    // normal path: run finally, then skip the handler
    finallyBody.foreach(_.foreach(_.generate(mv, symbolTable)))
    mv.visitJumpInsn(GOTO, done)

    // exception path: stack holds the exception reference
    mv.visitLabel(handler)
    if (!symbolTable.containsVariable(catchVar)) {
      symbolTable.putVariable(catchVar, VariableType.StringType)
    }
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getMessage", "()Ljava/lang/String;")
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(catchVar))
    catchBody.foreach(_.generate(mv, symbolTable))
    finallyBody.foreach(_.foreach(_.generate(mv, symbolTable)))

    mv.visitLabel(done)
  }
}
