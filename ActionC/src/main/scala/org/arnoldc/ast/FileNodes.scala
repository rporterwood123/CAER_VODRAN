package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable

object FileNodes {
  // Push a java.nio.file.Path built from a string operand: Paths.get(path).
  def pushPath(mv: MethodVisitor, symbolTable: SymbolTable, path: AstNode): Unit = {
    path.generate(mv, symbolTable)
    mv.visitInsn(ICONST_0)
    mv.visitTypeInsn(ANEWARRAY, "java/lang/String") // empty varargs array
    mv.visitMethodInsn(INVOKESTATIC, "java/nio/file/Paths", "get",
      "(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;")
  }

  // Push a new java.io.File(path).
  def pushFile(mv: MethodVisitor, symbolTable: SymbolTable, path: AstNode): Unit = {
    mv.visitTypeInsn(NEW, "java/io/File")
    mv.visitInsn(DUP)
    path.generate(mv, symbolTable)
    mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V")
  }
}

// WHAT'S IN THE BOX <path>  -> Files.readString(Paths.get(path))  (returns String)
case class ReadFileNode(path: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    FileNodes.pushPath(mv, symbolTable, path)
    mv.visitMethodInsn(INVOKESTATIC, "java/nio/file/Files", "readString",
      "(Ljava/nio/file/Path;)Ljava/lang/String;")
  }
}

// WRITE THAT DOWN <content> TO <path>  -> Files.writeString(Paths.get(path), content)
case class WriteFileNode(content: AstNode, path: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    FileNodes.pushPath(mv, symbolTable, path)
    content.generate(mv, symbolTable)
    mv.visitInsn(ICONST_0)
    mv.visitTypeInsn(ANEWARRAY, "java/nio/file/OpenOption") // empty varargs array
    mv.visitMethodInsn(INVOKESTATIC, "java/nio/file/Files", "writeString",
      "(Ljava/nio/file/Path;Ljava/lang/CharSequence;[Ljava/nio/file/OpenOption;)Ljava/nio/file/Path;")
    mv.visitInsn(POP) // discard returned Path
  }
}

// HONEY I'M HOME <path>  -> new File(path).exists() ? 1 : 0
case class FileExistsNode(path: AstNode) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    FileNodes.pushFile(mv, symbolTable, path)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "exists", "()Z")
  }
}

// SEAL THE EXITS <path>  -> new File(path).delete()
case class DeleteFileNode(path: AstNode) extends StatementNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    FileNodes.pushFile(mv, symbolTable, path)
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/File", "delete", "()Z")
    mv.visitInsn(POP) // discard boolean result
  }
}
