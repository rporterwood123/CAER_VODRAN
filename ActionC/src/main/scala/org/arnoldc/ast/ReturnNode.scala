package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.parboiled.errors.ParsingException


case class ReturnNode(operand: Option[OperandNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    // Inside an async block's run() method, a return stores its value into the
    // synthetic future's `result` field instead of returning from the method.
    symbolTable.asyncResultClass match {
      case Some(asyncClass) =>
        val resultOperand =
          operand.getOrElse(throw new ParsingException("ASYNC BLOCK MUST RETURN A VALUE"))
        TypeInference.requireInt(resultOperand, symbolTable, "I'LL BE BACK VALUE IN AN ASYNC BLOCK")
        mv.visitVarInsn(ALOAD, 0)
        resultOperand.generate(mv, symbolTable)
        mv.visitFieldInsn(PUTFIELD, asyncClass, "result", "I")
        return
      case None =>
    }
    if (operand.isEmpty) {
      if (symbolTable.getCurrentMethod().returnsValue) {
        throw new ParsingException("NON VOID METHOD: " + symbolTable.currentMethod + " MUST RETURN AN ARGUMENT")
      }
      mv.visitInsn(RETURN)
    }
    else {
      if (!symbolTable.getCurrentMethod().returnsValue) {
        throw new ParsingException("VOID METHOD: " + symbolTable.currentMethod + " CANNOT RETURN AN ARGUMENT")
      }
      TypeInference.requireInt(operand.get, symbolTable,
        "RETURN VALUE OF METHOD " + symbolTable.currentMethod)
      operand.get.generate(mv, symbolTable)
      mv.visitInsn(IRETURN)

    }
  }
}

