package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType
import org.arnoldc.ArrayVariableType

// I AIN'T GOT TIME TO BLEED <name> WITH <size> UGLY MOTHERFUCKERS
case class ArrayDeclareNode(variableName: String, size: OperandNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.IntArrayType)
    size.generate(mv, symbolTable)
    mv.visitIntInsn(NEWARRAY, T_INT)
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(variableName))
  }
}

// LOCK AND LOAD <name> WITH <size> UGLY MOTHERFUCKERS  (a float array)
case class FloatArrayDeclareNode(variableName: String, size: OperandNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.FloatArrayType)
    size.generate(mv, symbolTable)
    mv.visitIntInsn(NEWARRAY, T_FLOAT)
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(variableName))
  }
}

// DIVIDE AND CONQUER <name> <str> <delim>  -> declares String[] name = str.split(delim)
// The delimiter is treated literally (Pattern.quote), not as a regex.
case class SplitDeclareNode(variableName: String, str: AstNode, delim: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.StringArrayType)
    str.generate(mv, symbolTable)
    delim.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESTATIC, "java/util/regex/Pattern", "quote",
      "(Ljava/lang/String;)Ljava/lang/String;")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split",
      "(Ljava/lang/String;)[Ljava/lang/String;")
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(variableName))
  }
}

// GET IN LINE <name> AT <index>  (read an element; element type follows the array)
case class ArrayAccessNode(variableName: String, index: OperandNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(variableName))
    index.generate(mv, symbolTable)
    mv.visitInsn(elementLoadOpcode(symbolTable))
  }

  private def elementLoadOpcode(symbolTable: SymbolTable): Int =
    symbolTable.getVariableType(variableName) match {
      case at: ArrayVariableType => at.elementLoadOpcode
      case _ => IALOAD
    }
}

// HOW MANY OF THEM <name>  (array length — element type irrelevant)
case class ArrayLengthNode(variableName: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(variableName))
    mv.visitInsn(ARRAYLENGTH)
  }
}

// GET IN LINE <name> AT <index> / HERE IS MY INVITATION <expr> ... / ENOUGH TALK
case class ArrayAssignNode(variableName: String, index: OperandNode, expression: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val arrayType = symbolTable.getVariableType(variableName)
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(variableName))
    index.generate(mv, symbolTable)
    expression.generate(mv, symbolTable)
    // Coerce an int-valued expression into a float-array element.
    if (arrayType == VariableType.FloatArrayType && !TypeInference.isFloat(expression, symbolTable)) {
      mv.visitInsn(I2F)
    }
    mv.visitInsn(elementStoreOpcode(arrayType))
  }

  private def elementStoreOpcode(arrayType: VariableType): Int = arrayType match {
    case at: ArrayVariableType => at.elementStoreOpcode
    case _ => IASTORE
  }
}
