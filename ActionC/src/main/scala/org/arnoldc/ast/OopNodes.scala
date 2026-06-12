package org.arnoldc.ast

import org.objectweb.asm.{ClassWriter, MethodVisitor}
import org.objectweb.asm.Opcodes._
import org.arnoldc.{ClassMetadata, MethodInformation, SymbolTable, VariableType}

// Field metadata (all fields are int). `OPEN TO THE PUBLIC` / `THAT'S CLASSIFIED`.
case class FieldNode(name: String, isPublic: Boolean)

// An instance method: COMMANDER IN CHIEF <name> ... DISMISSED SOLDIER
case class InstanceMethodNode(methodName: String, params: List[VariableNode],
                              returnsValue: Boolean, body: List[StatementNode]) {
  def descriptor: String = "(" + "I" * params.size + ")" + (if (returnsValue) "I" else "V")

  def generateInto(cw: ClassWriter, className: String, globalSymbols: SymbolTable): Unit = {
    val mv = cw.visitMethod(ACC_PUBLIC, methodName, descriptor, null, null)
    mv.visitCode()
    val methodSymbols = new SymbolTable(Some(globalSymbols), methodName)
    methodSymbols.currentClass = Some(className)
    methodSymbols.putMethod(methodName, new MethodInformation(returnsValue, params.size))
    methodSymbols.putVariable("$this") // reserve local slot 0 for `this`
    params.foreach(p => methodSymbols.putVariable(p.variableName))
    body.foreach(_.generate(mv, methodSymbols))
    if (returnsValue) {
      MissingReturn.throwMissingReturn(mv, methodName)
    } else {
      mv.visitInsn(RETURN)
    }
    mv.visitMaxs(0, 0)
    mv.visitEnd()
  }
}

// MY NAME IS MAXIMUS <name> [LIKE FATHER LIKE SON <parent>] ... STRENGTH AND HONOR
case class ClassDefNode(className: String, parent: Option[String], fields: List[FieldNode],
                        constructorBody: Option[List[StatementNode]], methods: List[InstanceMethodNode]) {

  def metadata: ClassMetadata =
    ClassMetadata(className, parent, fields.map(_.name).toSet,
      methods.map(m => m.methodName -> new MethodInformation(m.returnsValue, m.params.size)).toMap)

  def parentInternal: String = parent.getOrElse("java/lang/Object")

  def generateClass(globalSymbols: SymbolTable): Array[Byte] = {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
    cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, className, null, parentInternal, null)
    cw.visitSource(globalSymbols.getFileName() + ".actionc", null)

    fields.foreach { f =>
      val access = if (f.isPublic) ACC_PUBLIC else ACC_PRIVATE
      cw.visitField(access, f.name, "I", null, null).visitEnd()
    }

    generateConstructor(cw, globalSymbols)
    methods.foreach(_.generateInto(cw, className, globalSymbols))

    cw.visitEnd()
    cw.toByteArray
  }

  private def generateConstructor(cw: ClassWriter, globalSymbols: SymbolTable): Unit = {
    val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
    mv.visitCode()
    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, parentInternal, "<init>", "()V")

    val ctorSymbols = new SymbolTable(Some(globalSymbols), "<init>")
    ctorSymbols.currentClass = Some(className)
    ctorSymbols.putVariable("$this") // reserve local slot 0 for `this`
    constructorBody.getOrElse(Nil).foreach(_.generate(mv, ctorSymbols))

    mv.visitInsn(RETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
  }
}

// WELCOME TO EARTH <var> AS <class>
case class NewInstanceNode(variableName: String, className: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    symbolTable.putVariable(variableName, VariableType.ObjectType(className))
    mv.visitTypeInsn(NEW, className)
    mv.visitInsn(DUP)
    mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V")
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(variableName))
  }
}

// <obj>.<field>  (read)
case class FieldAccessNode(objectVariable: String, fieldName: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val className = symbolTable.getObjectClassName(objectVariable)
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(objectVariable))
    mv.visitFieldInsn(GETFIELD, className, fieldName, "I")
  }
}

// GET TO THE CHOPPER <obj>.<field> / HERE IS MY INVITATION <expr> ... / ENOUGH TALK
case class FieldAssignNode(objectVariable: String, fieldName: String, expression: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val className = symbolTable.getObjectClassName(objectVariable)
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(objectVariable))
    expression.generate(mv, symbolTable)
    mv.visitFieldInsn(PUTFIELD, className, fieldName, "I")
  }
}

// LOOK AT ME.<field>  (read this.field)
case class ThisFieldAccessNode(fieldName: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val className = symbolTable.currentClass.get
    mv.visitVarInsn(ALOAD, 0)
    mv.visitFieldInsn(GETFIELD, className, fieldName, "I")
  }
}

// GET TO THE CHOPPER LOOK AT ME.<field> / HERE IS MY INVITATION <expr> ... / ENOUGH TALK
case class ThisFieldAssignNode(fieldName: String, expression: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val className = symbolTable.currentClass.get
    mv.visitVarInsn(ALOAD, 0)
    expression.generate(mv, symbolTable)
    mv.visitFieldInsn(PUTFIELD, className, fieldName, "I")
  }
}

// [GET YOUR ASS TO MARS <result>] DO IT NOW <obj>.<method> <args...>
case class InstanceMethodCallNode(resultVariable: String, objectVariable: String,
                                  methodName: String, arguments: List[OperandNode]) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val className = symbolTable.getObjectClassName(objectVariable)
    val method = symbolTable.resolveMethod(className, methodName)
      .getOrElse(throw new org.parboiled.errors.ParsingException(
        "METHOD " + methodName + " NOT DECLARED ON CLASS " + className))
    val descriptor = "(" + "I" * method.numberOfArguments + ")" + (if (method.returnsValue) "I" else "V")

    arguments.zipWithIndex.foreach { case (argument, index) =>
      TypeInference.requireInt(argument, symbolTable,
        "ARGUMENT " + (index + 1) + " TO METHOD " + methodName)
    }
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(objectVariable))
    arguments.foreach(_.generate(mv, symbolTable))
    mv.visitMethodInsn(INVOKEVIRTUAL, className, methodName, descriptor)

    if (resultVariable.nonEmpty) {
      mv.visitVarInsn(ISTORE, symbolTable.getVariableAddress(resultVariable))
    } else if (method.returnsValue) {
      mv.visitInsn(POP)
    }
  }
}
