package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType
import org.objectweb.asm.Opcodes._

case class AssignVariableNode(variable: String, expression: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    // A bare name that is not a local but is a field of the current class assigns
    // to this.field (PUTFIELD); otherwise it is a normal local store.
    if (!symbolTable.containsVariable(variable) && symbolTable.isFieldOfCurrentClass(variable)) {
      mv.visitVarInsn(ALOAD, 0)
      expression.generate(mv, symbolTable)
      mv.visitFieldInsn(PUTFIELD, symbolTable.currentClass.get, variable, "I")
    } else {
      val variableAddress = symbolTable.getVariableAddress(variable)
      expression.generate(mv, symbolTable)
      // Coerce an int-valued expression into a float-typed target (e.g. seeding a
      // float variable from an integer expression).
      if (symbolTable.getVariableType(variable) == VariableType.FloatType &&
          !TypeInference.isFloat(expression, symbolTable)) {
        mv.visitInsn(I2F)
      }
      mv.visitVarInsn(symbolTable.getVariableType(variable).storeOpcode, variableAddress)
    }
  }
}
