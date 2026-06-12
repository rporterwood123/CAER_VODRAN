package org.arnoldc.ast

import org.objectweb.asm.{Label, MethodVisitor}
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.parboiled.errors.ParsingException

// Shared codegen for the six comparison operators, dispatching on operand types:
// two strings compare by content via String.equals (equality forms only), a float
// on either side promotes both to float (FCMPL/FCMPG chosen per operator so NaN
// always compares false, matching javac), and the default is the plain int compare.
object ComparisonCodegen {

  // `stringNegate`: Some(false) for ==, Some(true) for !=, None for the ordering
  // operators (which strings don't support). `intCompareOpcode` jumps when the
  // comparison is true; `floatBranchOpcode` does the same on the FCMP result.
  def generate(mv: MethodVisitor, symbolTable: SymbolTable,
               operand1: AstNode, operand2: AstNode,
               intCompareOpcode: Int, floatCompareOpcode: Int, floatBranchOpcode: Int,
               stringNegate: Option[Boolean], operatorName: String) {
    val string1 = TypeInference.isString(operand1, symbolTable)
    val string2 = TypeInference.isString(operand2, symbolTable)
    if (string1 || string2) {
      if (!(string1 && string2)) {
        throw new ParsingException("CANNOT COMPARE A STRING WITH A NUMBER (" + operatorName + ")")
      }
      stringNegate match {
        case Some(negate) =>
          operand1.generate(mv, symbolTable)
          operand2.generate(mv, symbolTable)
          mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z")
          if (negate) {
            mv.visitInsn(ICONST_1)
            mv.visitInsn(IXOR)
          }
        case None =>
          throw new ParsingException("STRINGS ONLY SUPPORT EQUALITY COMPARISONS (" + operatorName + ")")
      }
    } else if (TypeInference.isFloat(operand1, symbolTable) || TypeInference.isFloat(operand2, symbolTable)) {
      TypeInference.generateAsFloat(operand1, mv, symbolTable)
      TypeInference.generateAsFloat(operand2, mv, symbolTable)
      mv.visitInsn(floatCompareOpcode)
      pushBool(mv, isTrue => mv.visitJumpInsn(floatBranchOpcode, isTrue))
    } else {
      operand1.generate(mv, symbolTable)
      operand2.generate(mv, symbolTable)
      pushBool(mv, isTrue => mv.visitJumpInsn(intCompareOpcode, isTrue))
    }
  }

  // Emit the given conditional jump, pushing 1 when it's taken and 0 otherwise.
  private def pushBool(mv: MethodVisitor, jumpWhenTrue: Label => Unit) {
    val isTrue = new Label()
    val conclude = new Label()
    jumpWhenTrue(isTrue)
    mv.visitInsn(ICONST_0)
    mv.visitJumpInsn(GOTO, conclude)
    mv.visitLabel(isTrue)
    mv.visitInsn(ICONST_1)
    mv.visitLabel(conclude)
  }
}
