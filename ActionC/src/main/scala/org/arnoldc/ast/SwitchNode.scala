package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.SymbolTable

case class CaseClause(matchValue: NumberNode, statements: List[StatementNode])

// CHOOSE YOUR DESTINY <value> ... FINISH HIM
// Each WHAT IF I TOLD YOU clause runs its statements and then exits the switch
// (no fall-through). SAME OLD SAME OLD is the optional default clause.
case class SwitchNode(value: OperandNode, cases: List[CaseClause], default: Option[List[StatementNode]]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val end = new Label()
    val defaultLabel = new Label()

    // LOOKUPSWITCH requires keys in ascending order.
    val sorted = cases.sortBy(_.matchValue.number)
    val keys = sorted.map(_.matchValue.number).toArray
    val caseLabels = sorted.map(_ => new Label()).toArray

    value.generate(mv, symbolTable)
    mv.visitLookupSwitchInsn(defaultLabel, keys, caseLabels)

    // GET OUT inside a case exits the switch (KEEP MOVING still targets the loop).
    symbolTable.enterSwitch(end)
    sorted.zip(caseLabels).foreach { case (clause, label) =>
      mv.visitLabel(label)
      clause.statements.foreach(_.generate(mv, symbolTable))
      mv.visitJumpInsn(GOTO, end)
    }

    mv.visitLabel(defaultLabel)
    default.foreach(_.foreach(_.generate(mv, symbolTable)))
    symbolTable.exitLoop()
    mv.visitLabel(end)
  }
}
