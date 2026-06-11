package org.arnoldc.ast

import org.objectweb.asm.{ClassWriter, MethodVisitor}
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.Label
import org.arnoldc.{ClassMetadata, SymbolTable, VariableType}

// COVER ME <name> ... MISSION COMPLETE
// Compiles to a synthetic Runnable class with `int result` and `volatile int done`
// fields; the block runs on a new thread. I'LL BE BACK stores into `result`, and
// run() sets `done` when finished. The block name becomes a variable holding the
// future (the Runnable instance), so <name>.result reads the computed value.
case class AsyncBlockNode(name: String, body: List[StatementNode]) extends StatementNode {

  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val asyncClassName = symbolTable.getFileName() + "$async$" + name

    // Build and register the synthetic Runnable class.
    symbolTable.addSyntheticClass(asyncClassName, buildAsyncClass(asyncClassName, symbolTable))
    symbolTable.registerClass(ClassMetadata(asyncClassName, None, Set("result", "done"), Map.empty))

    // future = new <asyncClass>()
    symbolTable.putVariable(name, VariableType.ObjectType(asyncClassName))
    mv.visitTypeInsn(NEW, asyncClassName)
    mv.visitInsn(DUP)
    mv.visitMethodInsn(INVOKESPECIAL, asyncClassName, "<init>", "()V")
    mv.visitVarInsn(ASTORE, symbolTable.getVariableAddress(name))

    // new Thread(future).start()
    mv.visitTypeInsn(NEW, "java/lang/Thread")
    mv.visitInsn(DUP)
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(name))
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Thread", "<init>", "(Ljava/lang/Runnable;)V")
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "start", "()V")
  }

  private def buildAsyncClass(asyncClassName: String, globalSymbols: SymbolTable): Array[Byte] = {
    val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
    cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, asyncClassName, null, "java/lang/Object",
      Array("java/lang/Runnable"))
    cw.visitField(ACC_PUBLIC, "result", "I", null, null).visitEnd()
    cw.visitField(ACC_PUBLIC + ACC_VOLATILE, "done", "I", null, null).visitEnd()

    // default constructor
    val init = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
    init.visitCode()
    init.visitVarInsn(ALOAD, 0)
    init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
    init.visitInsn(RETURN)
    init.visitMaxs(0, 0)
    init.visitEnd()

    // run() { <body>; this.done = 1; }
    val run = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null)
    run.visitCode()
    val runSymbols = new SymbolTable(Some(globalSymbols), "run")
    runSymbols.currentClass = Some(asyncClassName)
    runSymbols.asyncResultClass = Some(asyncClassName)
    runSymbols.putVariable("$this") // reserve local slot 0 for `this`
    body.foreach(_.generate(run, runSymbols))
    run.visitVarInsn(ALOAD, 0)
    run.visitInsn(ICONST_1)
    run.visitFieldInsn(PUTFIELD, asyncClassName, "done", "I")
    run.visitInsn(RETURN)
    run.visitMaxs(0, 0)
    run.visitEnd()

    cw.visitEnd()
    cw.toByteArray
  }
}

// HOLD THE LINE <name>  -> spin-wait until the future's `done` flag is set.
case class AwaitNode(name: String) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    val asyncClassName = symbolTable.getObjectClassName(name)
    val loop = new Label()
    val done = new Label()

    mv.visitLabel(loop)
    mv.visitVarInsn(ALOAD, symbolTable.getVariableAddress(name))
    mv.visitFieldInsn(GETFIELD, asyncClassName, "done", "I")
    mv.visitJumpInsn(IFNE, done)
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "yield", "()V")
    mv.visitJumpInsn(GOTO, loop)
    mv.visitLabel(done)
  }
}
