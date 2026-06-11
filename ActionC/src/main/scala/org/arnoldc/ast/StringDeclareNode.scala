package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType

// I HAVE COME HERE TO CHEW BUBBLEGUM <name>
// AND KICK ASS <part> [AND KICK ASS <part> ...]
// Declares a string variable initialized to the concatenation of its parts.
// Each part is a string literal, the empty-string literal, or a string variable.
case class StringDeclareNode(variableName: String, parts: List[AstNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.StringType)

    parts.head.generate(mv, symbolTable)
    parts.tail.foreach { part =>
      part.generate(mv, symbolTable)
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;")
    }

    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(variableName))
  }
}
