package org.arnoldc

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.file.Files

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CliTest extends AnyFlatSpec with Matchers {

  it should "compile and run a source file in a subdirectory" in {
    val dir = Files.createTempDirectory("actionc-cli")
    val sub = Files.createDirectories(dir.resolve("sub"))
    val src = sub.resolve("greet.actionc")
    Files.write(src,
      ("IT'S SHOWTIME\n" +
        "TALK TO THE HAND \"hi from subdir\"\n" +
        "YOU HAVE BEEN TERMINATED\n").getBytes("UTF-8"))

    val captured = new ByteArrayOutputStream()
    val original = System.out
    System.setOut(new PrintStream(captured))
    try ArnoldC.main(Array("-run", src.toString))
    finally System.setOut(original)

    captured.toString should equal("hi from subdir\n")
    // The .class file lands next to the source, named after its basename.
    Files.exists(sub.resolve("greet.class")) should be(true)
  }

  it should "name the program source in runtime stack traces" in {
    val code =
      "IT'S SHOWTIME\n" +
        "WELCOME TO THE PARTY PAL \"boom\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    val (classes, _) = new ArnoldGenerator().generate(code, "Hello")
    val thrown = intercept[Exception] {
      new ByteCodeExecutor().invokeMainMethod(classes, "Hello")
    }
    val cause = thrown match {
      case e: java.lang.reflect.InvocationTargetException => e.getCause
      case other => other
    }
    cause.getStackTrace.head.getFileName should equal("Hello.actionc")
  }
}
