package org.arnoldc

import org.arnoldc.ast._

object Declaimer {

  val p = new ArnoldParser

  def declaim(root: RootNode, outputFile: String): Unit = {
    SpeechUtils.init("kevin16", outputFile)
    declaim(root)
    SpeechUtils.terminate()
  }

  def say(text: String) = SpeechUtils.doSpeak(text + "!\n")

  def declaim(node: AstNode): Unit = node match {
    case RootNode(_, methods) => methods.map(m => declaim(m))
    case MainMethodNode(stmts) =>
      say(p.BeginMain)
      stmts foreach declaim
      say(p.EndMain)
    case MethodNode(name, args, ret, stmts) =>
      say(s"${p.DeclareMethod} $name")
      args.foreach(a => say(s"${p.MethodArguments} ${a.variableName}"))
      if (ret) say(p.NonVoidMethod);
      stmts.map(s => declaim(s))
      say(p.EndMethodDeclaration)

    case AssignVariableNode(name, expr) =>
      say(s"${p.AssignVariable} $name")
      say(p.SetValue)
      declaim(expr)
      say(p.EndAssignVariable)
    case PrintNode(what) =>
      say(p.Print)
      declaim(what)
    case DeclareIntNode(name, value) =>
      say(s"${p.DeclareInt} $name")
      say(s"${p.SetInitialValue} $value")
    case ConditionNode(condition, ifStmts, elseStmts) =>
      say(p.If)
      declaim(condition)
      ifStmts foreach declaim
      if (elseStmts.nonEmpty) {
        say(p.Else)
        elseStmts foreach declaim
      }
      say(p.EndIf)
    case WhileNode(condition, stmts) =>
      say(p.While)
      declaim(condition)
      stmts foreach declaim
      say(p.EndWhile)
    case CallMethodNode(variable, name, args) =>
      if (variable.nonEmpty) {
        say(s"${p.AssignVariableFromMethodCall} $variable")
      }
      say(s"${p.CallMethod} $name")
      args foreach declaim
    case CallReadMethodNode(variable) =>
      say(s"${p.Read} $variable")
    case ReturnNode(expr) =>
      say(p.Return)
      expr.map { x => declaim(x) }

    case AndNode(expr1, expr2) =>
      declaim(expr1)
      say(p.And)
      declaim(expr2)
    case OrNode(expr1, expr2) =>
      declaim(expr1)
      say(p.Or)
      declaim(expr2)
    case PlusExpressionNode(expr1, expr2) =>
      declaim(expr1)
      say(p.PlusOperator)
      declaim(expr2)
    case MinusExpressionNode(expr1, expr2) =>
      declaim(expr1)
      say(p.MinusOperator)
      declaim(expr2)
    case DivisionExpressionNode(expr1, expr2) =>
      declaim(expr1)
      say(p.DivisionOperator)
      declaim(expr2)
    case MultiplicationExpressionNode(expr1, expr2) =>
      declaim(expr1)
      say(p.MultiplicationOperator)
      declaim(expr2)
    case ModuloExpressionNode(expr1, expr2) =>
      declaim(expr1)
      say(p.Modulo)
      declaim(expr2)
    case GreaterThanNode(expr1, expr2) =>
      declaim(expr1)
      say(p.GreaterThan)
      declaim(expr2)
    case EqualToNode(expr1, expr2) =>
      declaim(expr1)
      say(p.EqualTo)
      declaim(expr2)

    case NumberNode(num)    => say(num.toString)
    case StringNode(str)    => say(str)
    case VariableNode(name) => say(name)

    case other              => say(s"${p.ParseError} $other")
  }

}

// Console-based fallback for the `-declaim` feature. The original implementation
// synthesized speech via FreeTTS/JSAPI (javax.speech, com.sun.speech.freetts),
// native TTS libraries that are not declared in the build and are not cleanly
// available on Maven Central. Rather than drop the feature, `declaim` now prints
// the spoken form of the program to stdout.
object SpeechUtils {

  def init(voiceName: String, outputFile: String): Unit = {}

  def terminate(): Unit = {}

  def doSpeak(speakText: String): Unit = {
    print(speakText)
  }
}