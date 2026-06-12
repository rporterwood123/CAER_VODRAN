package org.arnoldc
import java.io._

class InputTest extends ArnoldGeneratorTest{

  it should "read integer from input" in {
    writeToFile(path, "123")
    val code =
      "IT'S SHOWTIME\n" +
      "TALK TO THE HAND \"Input a number:\"\n" +
      "HEY CHRISTMAS TREE result\n" +
      "YOU SET US UP 0\n" +
      "GET YOUR ASS TO MARS result\n" +
      "DO IT NOW\n" +
      "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY\n" +
      "TALK TO THE HAND result\n" +
      "TALK TO THE HAND \"Bye\"\n" +
      "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("Input a number:\n123\nBye\n")
  }

  it should "read two integers from piped input" in {
    writeToFile(path, "5\n7\n")
    val code =
      "IT'S SHOWTIME\n" +
      "HEY CHRISTMAS TREE a\n" +
      "YOU SET US UP 0\n" +
      "HEY CHRISTMAS TREE b\n" +
      "YOU SET US UP 0\n" +
      "GET YOUR ASS TO MARS a\n" +
      "DO IT NOW\n" +
      "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY\n" +
      "GET YOUR ASS TO MARS b\n" +
      "DO IT NOW\n" +
      "I WANT TO ASK YOU A BUNCH OF QUESTIONS AND I WANT TO HAVE THEM ANSWERED IMMEDIATELY\n" +
      "TALK TO THE HAND a\n" +
      "TALK TO THE HAND b\n" +
      "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("5\n7\n")
  }

  val path = "test.in"

  override val byteCodeExecutor = new ByteCodeExecutor{

    override def getOutput(classes: Map[String, Array[Byte]], className: String): String = {

      val originalIn = System.in
      val outputRedirectionStream = new ByteArrayOutputStream()
      val fileInput = new BufferedInputStream(new FileInputStream(path))
      System.setOut(new PrintStream(outputRedirectionStream))
      System.setIn(fileInput)

      try {
        invokeMainMethod(classes, className)
      } finally {
        System.setOut(originalOutputStream)
        System.setIn(originalIn)
      }
      outputRedirectionStream.toString
    }
  }

  def writeToFile(file:String, data:String) = {
    val out = new FileOutputStream(file)
    out.write(data.getBytes)
    out.close()
  }


}