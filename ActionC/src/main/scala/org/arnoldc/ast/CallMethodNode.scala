package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.parboiled.errors.ParsingException

case class CallMethodNode(returnVar: String, methodName: String, arguments: List[OperandNode]) extends StatementNode {

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) = {
    // If methodName is a variable bound to a function reference, dispatch to the
    // referenced function instead.
    val actualMethod = symbolTable.getFunctionRef(methodName).getOrElse(methodName)

    val argumentsExpected = symbolTable.getMethodInformation(actualMethod).numberOfArguments
    if (arguments.size != argumentsExpected) {
      throw new ParsingException("INVALID NUMBER OF ARGUMENTS WHEN CALLING METHOD: " + actualMethod + "\n" +
        "EXPECTED: " + argumentsExpected + ", GOT: " + arguments.size)
    }
    arguments.zipWithIndex.foreach { case (argument, index) =>
      TypeInference.requireInt(argument, symbolTable,
        "ARGUMENT " + (index + 1) + " TO METHOD " + actualMethod)
    }
    arguments.foreach(_.generate(mv, symbolTable))
    mv.visitMethodInsn(INVOKESTATIC, symbolTable.getFileName(), actualMethod, symbolTable.getMethodDescription(actualMethod))
    handleStackAfterCall

    def handleStackAfterCall {
      if (returnVar != "") {
        if (!symbolTable.getMethodInformation(actualMethod).returnsValue) {
          throw new ParsingException("CANNOT ASSIGN VALUE TO VARIABLE " + returnVar + ", METHOD " + actualMethod + " IS A TYPE OF VOID")
        }
        mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(returnVar))
      }
      else if (symbolTable.getMethodInformation(actualMethod).returnsValue) {
        mv.visitInsn(POP)
      }
    }
  }
}
