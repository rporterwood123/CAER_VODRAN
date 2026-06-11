package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType

// NOW I HAVE A MACHINE GUN <name>
// HO HO HO <floatLiteral>
case class FloatDeclareNode(variableName: String, value: FloatNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.FloatType)
    value.generate(mv, symbolTable)
    mv.visitVarInsn(FSTORE, symbolTable.getVariableAddress(variableName))
  }
}
