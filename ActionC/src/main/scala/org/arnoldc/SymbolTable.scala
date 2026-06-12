package org.arnoldc

import scala.collection.mutable
import org.parboiled.errors.ParsingException
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label

// Metadata about a declared class, used during code generation for field
// resolution and inheritance. All fields are integers (per the OOP-lite design).
case class ClassMetadata(name: String, parent: Option[String], fields: Set[String],
                         methods: Map[String, MethodInformation])

case class SymbolTable(upperLevel: Option[SymbolTable], currentMethod: String) {

  val FirstSymbolTableAddress = 0
  private val variableTable = new mutable.HashMap[String, Integer]()
  private val typeTable = new mutable.HashMap[String, VariableType]()
  private val methodTable = new mutable.HashMap[String, MethodInformation]()
  private val classRegistry = new mutable.HashMap[String, ClassMetadata]()

  // The class whose instance method/constructor is currently being generated, if
  // any. When set, `this` is at local slot 0 and bare names may resolve to fields.
  var currentClass: Option[String] = None

  // When generating an async block's run() method, the synthetic class name whose
  // `result` field a return (I'LL BE BACK) should store into.
  var asyncResultClass: Option[String] = None

  // Synthetic classes (e.g. async Runnables) produced as a side effect of code
  // generation, collected on the root table and emitted as extra .class files.
  private val syntheticClasses = new mutable.HashMap[String, Array[Byte]]()

  def addSyntheticClass(name: String, bytecode: Array[Byte]): Unit = upperLevel match {
    case Some(parent) => parent.addSyntheticClass(name, bytecode)
    case None => syntheticClasses.put(name, bytecode)
  }

  def collectSyntheticClasses(): Map[String, Array[Byte]] = upperLevel match {
    case Some(parent) => parent.collectSyntheticClasses()
    case None => syntheticClasses.toMap
  }

  // Function references: a variable bound to a (statically-known) lambda/function
  // name via THE NAME'S PLISSKEN. Calls on the variable resolve to that function.
  private val functionRefs = new mutable.HashMap[String, String]()

  def putFunctionRef(variableName: String, targetFunction: String): Unit =
    functionRefs.put(variableName, targetFunction)

  def getFunctionRef(variableName: String): Option[String] =
    functionRefs.get(variableName).orElse(upperLevel.flatMap(_.getFunctionRef(variableName)))

  // Stack of enclosing breakable constructs, innermost first. Loops carry a
  // continue label and a break label; switches carry only a break label.
  // Used by GET OUT (break) and KEEP MOVING (continue).
  private var loopContexts: List[(Option[Label], Label)] = Nil

  def enterLoop(continueLabel: Label, breakLabel: Label): Unit = {
    loopContexts = (Some(continueLabel), breakLabel) :: loopContexts
  }

  def enterSwitch(breakLabel: Label): Unit = {
    loopContexts = (None, breakLabel) :: loopContexts
  }

  def exitLoop(): Unit = {
    loopContexts = loopContexts.tail
  }

  def currentBreakLabel: Label = loopContexts match {
    case (_, breakLabel) :: _ => breakLabel
    case Nil => throw new ParsingException("GET OUT USED OUTSIDE OF A LOOP OR SWITCH")
  }

  // continue ignores switch frames: it targets the nearest enclosing loop.
  def currentContinueLabel: Label =
    loopContexts.collectFirst { case (Some(continueLabel), _) => continueLabel }
      .getOrElse(throw new ParsingException("KEEP MOVING USED OUTSIDE OF A LOOP"))

  val initialNextVarAddress: Int = FirstSymbolTableAddress

  def size(): Int = {
    initialNextVarAddress + variableTable.size
  }

  def putVariable(variableName: String): Unit = putVariable(variableName, VariableType.IntType)

  def putVariable(variableName: String, variableType: VariableType): Unit = {
    val newVarAddress = initialNextVarAddress + variableTable.size
    if (variableTable.contains(variableName)) {
      throw new ParsingException("DUPLICATE VARIABLE: " + variableName)
    }
    variableTable += (variableName -> newVarAddress)
    typeTable += (variableName -> variableType)
  }

  def getVariableType(variableName: String): VariableType = {
    typeTable.getOrElse(variableName, {
      if (upperLevel.isEmpty) {
        throw new ParsingException("VARIABLE: " + variableName + " NOT DECLARED!")
      }
      upperLevel.get.getVariableType(variableName)
    })
  }

  def containsVariable(variableName: String): Boolean = {
    variableTable.contains(variableName) || upperLevel.exists(_.containsVariable(variableName))
  }

  def getVariableAddress(variableName: String): Integer = {
    variableTable.getOrElse(variableName, {
      if (upperLevel.isEmpty) {
        throw new ParsingException("VARIABLE: " + variableName + " NOT DECLARED!")
      }
      upperLevel.get.getVariableAddress(variableName)
    })
  }

  // --- Class registry (stored on the root table; child tables delegate up) ---

  def registerClass(meta: ClassMetadata): Unit = upperLevel match {
    case Some(parent) => parent.registerClass(meta)
    case None => classRegistry.put(meta.name, meta)
  }

  def lookupClass(name: String): Option[ClassMetadata] =
    classRegistry.get(name).orElse(upperLevel.flatMap(_.lookupClass(name)))

  // All field names reachable on a class: its own fields plus inherited ones.
  def reachableFields(className: String): Set[String] = lookupClass(className) match {
    case Some(meta) => meta.fields ++ meta.parent.map(reachableFields).getOrElse(Set.empty)
    case None => Set.empty
  }

  // Is `name` a field of the class whose method we're currently generating?
  def isFieldOfCurrentClass(name: String): Boolean =
    currentClass.exists(cn => reachableFields(cn).contains(name))

  // Resolve a method on a class, walking up the inheritance chain.
  def resolveMethod(className: String, methodName: String): Option[MethodInformation] =
    lookupClass(className).flatMap { meta =>
      meta.methods.get(methodName).orElse(meta.parent.flatMap(resolveMethod(_, methodName)))
    }

  // The class name of an object-typed variable (for field/method dispatch).
  def getObjectClassName(variableName: String): String = getVariableType(variableName) match {
    case VariableType.ObjectType(className) => className
    case other => throw new ParsingException("VARIABLE " + variableName + " IS NOT AN OBJECT (" + other + ")")
  }

  def putMethod(methodName: String, methodInformation: MethodInformation) = {
    methodTable.put(methodName, methodInformation)
  }

  def getMethodDescription(methodName: String): String = {
    if (methodName.equals("main")) {
      "([Ljava/lang/String;)V"
    }
    else {
      val method = getMethodInformation(methodName)
      val numberOfArguments = method.numberOfArguments
      val returnValue = if (method.returnsValue) "I" else "V"
      "(" + "I" * numberOfArguments + ")" + returnValue
    }
  }

  def getCurrentMethod(): MethodInformation = {
    getMethodInformation(currentMethod)
  }

  def getMethodInformation(methodName: String): MethodInformation = {
    methodTable.getOrElse(methodName, {
      if (upperLevel.isEmpty) {
        throw new ParsingException("METHOD: " + methodName + " NOT DECLARED!")
      }
      upperLevel.get.getMethodInformation(methodName)
    })
  }

  def getFileName(): String = {
    if (upperLevel.isEmpty) {
      currentMethod
    }
    else {
      upperLevel.get.getFileName()
    }
  }

  // The root (global) table: methods, classes, and synthetic-class state live
  // there. Code generated into a different JVM frame should parent to this so
  // enclosing-method locals (whose slot numbers belong to another frame) are
  // not visible.
  def rootTable: SymbolTable = upperLevel.map(_.rootTable).getOrElse(this)

}