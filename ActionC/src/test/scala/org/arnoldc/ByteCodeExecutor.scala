package org.arnoldc

import java.io.{PrintStream, ByteArrayOutputStream}

class ByteCodeExecutor extends ClassLoader {
  val originalOutputStream = System.out

  def getOutput(classes: Map[String, Array[Byte]], className: String): String = {

    val outputRedirectionStream = new ByteArrayOutputStream()

   System.setOut(new PrintStream(outputRedirectionStream))

    invokeMainMethod(classes, className)
    System.setOut(originalOutputStream)
    outputRedirectionStream.toString
  }

  def invokeMainMethod(classes: Map[String, Array[Byte]], className: String) = {
    val loader = new ByteCodeExecutor()
    var mainClass: Class[_] = null
    classes.foreach { case (name, bytecode) =>
      val defined = loader.defineClass(name, bytecode, 0, bytecode.length)
      if (name == className) mainClass = defined
    }
    val testInstance = mainClass.newInstance().asInstanceOf[ {def main(test: Array[String])}]
    testInstance.main(null)
  }
}
