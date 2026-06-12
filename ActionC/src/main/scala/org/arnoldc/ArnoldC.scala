package org.arnoldc

import java.io.File
import java.nio.file.Files

import org.arnoldc.ast.RootNode
import org.parboiled.errors.{ParserRuntimeException, ParsingException}

object ArnoldC {
  def main(args: Array[String]) {
    if (args.length < 1 || args.length > 2) {
      println("Usage: ArnoldC [-run|-declaim] [FileToSourceCode]")
      return
    }
    val sourceFile = new File(getFileNameFromArgs(args)).getAbsoluteFile
    val source = scala.io.Source.fromFile(sourceFile)
    val sourceCode = try source.mkString finally source.close()

    // The main class is named after the bare basename (a path-derived name would
    // not be a valid JVM class name), and every .class file — main, user classes,
    // synthetics — is written next to the source file.
    val classFilename = sourceFile.getName.replaceAll("\\.[^.]*$", "")
    val outputDir = sourceFile.getParentFile

    try {
      val (classes, root) = new ArnoldGenerator().generate(sourceCode, classFilename)
      classes.foreach { case (className, bytecode) =>
        Files.write(new File(outputDir, className + ".class").toPath, bytecode)
      }
      processOption(getCommandFromArgs(args), outputDir, classFilename, root)
    } catch {
      case e: ParsingException =>
        System.err.println(e.getMessage)
        sys.exit(1)
      case e: ParserRuntimeException =>
        System.err.println(e.getMessage)
        sys.exit(1)
    }
  }

  def getFileNameFromArgs(args: Array[String]): String =
    if (args.length == 2) args(1) else args(0)

  def getCommandFromArgs(args: Array[String]): String =
    if (args.length == 2) args(0) else ""

  def processOption(command: String, outputDir: File, className: String, root: RootNode): Unit = command match {
    case "-run" => Executor.execute(outputDir, className)
    case "-declaim" => Declaimer.declaim(root, className)
    case _ =>
  }

}
