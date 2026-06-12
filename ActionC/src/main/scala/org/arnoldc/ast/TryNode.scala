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
    val catchEnd = new Label()
    val rethrow = new Label()
    val done = new Label()

    mv.visitLabel(start)
    tryBody.foreach(_.generate(mv, symbolTable))
    mv.visitLabel(end)
    // Registered after generating the body so nested try blocks (which register
    // during it) come first in the exception table — the JVM takes the first match.
    mv.visitTryCatchBlock(start, end, handler, "java/lang/Exception")
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
    mv.visitLabel(catchEnd)
    finallyBody.foreach { fin =>
      // The finally must also run when the catch body itself throws: cover the
      // catch body with a catch-all that runs the finally and rethrows.
      mv.visitTryCatchBlock(handler, catchEnd, rethrow, null)
      fin.foreach(_.generate(mv, symbolTable))
      mv.visitJumpInsn(GOTO, done)
      mv.visitLabel(rethrow) // stack: [throwable]
      fin.foreach(_.generate(mv, symbolTable))
      mv.visitInsn(ATHROW)
    }
    mv.visitLabel(done)
  }
}
