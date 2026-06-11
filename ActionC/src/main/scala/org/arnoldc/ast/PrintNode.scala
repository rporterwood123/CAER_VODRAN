package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType


case class PrintNode(operand: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
    operand.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + printDescriptor(symbolTable) + ")V")
  }

  // Determine the println overload from the operand's type.
  private def printDescriptor(symbolTable: SymbolTable): String = operand match {
    case _: StringNode => "Ljava/lang/String;"
    case VariableNode(name) =>
      symbolTable.getVariableType(name) match {
        case VariableType.StringType => "Ljava/lang/String;"
        case VariableType.FloatType => "F"
        case _ => "I"
      }
    case ArrayAccessNode(name, _) =>
      symbolTable.getVariableType(name) match {
        case VariableType.StringArrayType => "Ljava/lang/String;"
        case VariableType.FloatArrayType => "F"
        case _ => "I"
      }
    // String-returning stdlib functions (printable directly, not just in concat).
    case _: UpperNode | _: LowerNode | _: TrimNode | _: SubstringNode |
         _: ReplaceNode | _: CharAtNode | _: ReverseNode | _: NumToStringNode =>
      "Ljava/lang/String;"
    case _ => "I"
  }
}

