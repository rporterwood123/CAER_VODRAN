package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.arnoldc.VariableType
import org.parboiled.errors.ParsingException

// Compile-time numeric type inference for arithmetic operands. ActionC numbers are
// either int or 32-bit float; an expression is float if any leaf is a float literal
// or a float-typed variable. Mixed int/float arithmetic promotes the int side to
// float (I2F) so both operands share the JVM's F-variant opcodes (FADD/FSUB/...).
object TypeInference {

  // Does this operand/expression evaluate to a float at runtime?
  def isFloat(node: AstNode, symbolTable: SymbolTable): Boolean = node match {
    case _: FloatNode => true
    case VariableNode(name) =>
      // Field-backed bare names (not local variables) are int-only by design.
      symbolTable.containsVariable(name) &&
        symbolTable.getVariableType(name) == VariableType.FloatType
    case ArrayAccessNode(name, _) =>
      symbolTable.containsVariable(name) &&
        symbolTable.getVariableType(name) == VariableType.FloatArrayType
    case PlusExpressionNode(l, r) => isFloat(l, symbolTable) || isFloat(r, symbolTable)
    case MinusExpressionNode(l, r) => isFloat(l, symbolTable) || isFloat(r, symbolTable)
    case MultiplicationExpressionNode(l, r) => isFloat(l, symbolTable) || isFloat(r, symbolTable)
    case DivisionExpressionNode(l, r) => isFloat(l, symbolTable) || isFloat(r, symbolTable)
    case ModuloExpressionNode(l, r) => isFloat(l, symbolTable) || isFloat(r, symbolTable)
    case _ => false
  }

  // Generate `node`, then coerce its result to float if it produced an int.
  def generateAsFloat(node: AstNode, mv: MethodVisitor, symbolTable: SymbolTable) {
    node.generate(mv, symbolTable)
    if (!isFloat(node, symbolTable)) mv.visitInsn(I2F)
  }

  // Does this operand/expression evaluate to a String at runtime?
  def isString(node: AstNode, symbolTable: SymbolTable): Boolean = node match {
    case _: StringNode => true
    case VariableNode(name) =>
      symbolTable.containsVariable(name) &&
        symbolTable.getVariableType(name) == VariableType.StringType
    case ArrayAccessNode(name, _) =>
      symbolTable.containsVariable(name) &&
        symbolTable.getVariableType(name) == VariableType.StringArrayType
    case _: UpperNode | _: LowerNode | _: TrimNode | _: SubstringNode |
         _: ReplaceNode | _: CharAtNode | _: ReverseNode | _: NumToStringNode |
         _: ReadFileNode => true
    case _ => false
  }

  // Method signatures are int-only by design: reject string/float operands where
  // an int is required, instead of emitting bytecode the verifier rejects.
  def requireInt(node: AstNode, symbolTable: SymbolTable, context: String) {
    if (isString(node, symbolTable)) {
      throw new ParsingException(context + " MUST BE AN INTEGER, GOT A STRING")
    }
    if (isFloat(node, symbolTable)) {
      throw new ParsingException(context + " MUST BE AN INTEGER, GOT A FLOAT")
    }
  }
}
