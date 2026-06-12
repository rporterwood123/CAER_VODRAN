package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

case class NumberNode(number: Int) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    // BIPUSH/SIPUSH hold signed 8/16-bit operands; anything wider needs the
    // constant pool, or the value silently truncates.
    if (number >= Byte.MinValue && number <= Byte.MaxValue) {
      mv.visitIntInsn(BIPUSH, number)
    } else if (number >= Short.MinValue && number <= Short.MaxValue) {
      mv.visitIntInsn(SIPUSH, number)
    } else {
      mv.visitLdcInsn(Int.box(number))
    }
  }
}
