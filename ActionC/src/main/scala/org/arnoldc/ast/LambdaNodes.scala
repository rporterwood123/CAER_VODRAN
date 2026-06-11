package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.{MethodInformation, SymbolTable}

// CALL ME SNAKE <name> (<params>) => <body>
// Compiled to a static method that returns the body expression. Callable like any
// function via DO IT NOW <name> <args>.
case class LambdaMethodNode(methodName: String, arguments: List[VariableNode], body: AstNode)
  extends AbstractMethodNode {

  val returnsValue: Boolean = true
  val statements: List[StatementNode] = Nil // lambdas have a single expression body

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val methodSymbols = new SymbolTable(Some(symbolTable), methodName)
    methodSymbols.putMethod(methodName, new MethodInformation(true, arguments.size))
    arguments.foreach(a => methodSymbols.putVariable(a.variableName))
    mv.visitCode()
    body.generate(mv, methodSymbols)
    mv.visitInsn(IRETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
  }
}

// THE NAME'S PLISSKEN <function>
// A reference to a named lambda/function. Resolved at compile time: assigning one
// to a variable records the binding so DO IT NOW <variable> dispatches to it.
case class FunctionRefNode(targetFunction: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    // The binding is recorded at declaration; the stored slot itself is unused.
    mv.visitInsn(ICONST_0)
  }
}
