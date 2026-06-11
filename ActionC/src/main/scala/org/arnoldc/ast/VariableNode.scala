package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class VariableNode(variableName: String) extends OperandNode{
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    // Inside an instance method/constructor, a bare name that is not a local and
    // is a field of the current class resolves to this.field.
    if (!symbolTable.containsVariable(variableName) && symbolTable.isFieldOfCurrentClass(variableName)) {
      mv.visitVarInsn(ALOAD, 0)
      mv.visitFieldInsn(GETFIELD, symbolTable.currentClass.get, variableName, "I")
    } else {
      val variableType = symbolTable.getVariableType(variableName)
      mv.visitVarInsn(variableType.loadOpcode, symbolTable.getVariableAddress(variableName))
    }
  }
}
