package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class MainMethodNode(statements: List[StatementNode]) extends AbstractMethodNode {

  val methodName: String = "main"
  val arguments = Nil
  val returnsValue = false

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // Main's locals get their own frame table: putting them on the shared global
    // table would leak them into lambdas, async blocks, and class methods, which
    // run in different JVM frames where those slot numbers mean something else.
    val mainSymbols = new SymbolTable(Some(symbolTable), methodName)
    mv.visitCode()
    statements.foreach(_.generate(mv, mainSymbols))
    mv.visitInsn(RETURN)
    mv.visitMaxs(100, 100)
    mv.visitEnd()
  }

}
