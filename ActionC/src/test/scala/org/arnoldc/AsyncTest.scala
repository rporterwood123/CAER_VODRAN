package org.arnoldc

import org.parboiled.errors.ParsingException

class AsyncTest extends ArnoldGeneratorTest {

  it should "reject referencing a variable declared outside the async block" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE base\n" +
        "YOU SET US UP 42\n" +
        "COVER ME job\n" +
        "I'LL BE BACK base\n" +
        "MISSION COMPLETE\n" +
        "HOLD THE LINE job\n" +
        "YOU HAVE BEEN TERMINATED\n"
    // The block runs on its own JVM frame: outer locals aren't capturable, so the
    // reference must fail at compile time, not produce broken bytecode.
    a[ParsingException] should be thrownBy getOutput(code)
  }

  it should "run an async block, await it, and read the result" in {
    val code =
      "IT'S SHOWTIME\n" +
        "COVER ME calc\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP 42\n" +
        "I'LL BE BACK x\n" +
        "MISSION COMPLETE\n" +
        "HOLD THE LINE calc\n" +
        "HEY CHRISTMAS TREE answer\n" +
        "YOU SET US UP calc.result\n" +
        "TALK TO THE HAND answer\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("42\n")
  }

  it should "complete the await even when the async block throws" in {
    val code =
      "IT'S SHOWTIME\n" +
        "COVER ME job\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER x\n" +
        "HERE IS MY INVITATION 1\n" +
        "HE HAD TO SPLIT 0\n" +
        "ENOUGH TALK\n" +
        "I'LL BE BACK x\n" +
        "MISSION COMPLETE\n" +
        "HOLD THE LINE job\n" +
        "TALK TO THE HAND \"after await\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    // Run on a watchdog thread: with the bug, HOLD THE LINE spins forever.
    @volatile var output: Option[String] = None
    val runner = new Thread(new Runnable { def run() { output = Some(getOutput(code)) } })
    runner.setDaemon(true)
    runner.start()
    runner.join(10000)
    withClue("await never completed after the async block threw: ") {
      output should be(defined)
    }
    output.get should equal("after await\n")
  }

  it should "run async blocks on daemon threads so the JVM can exit without them" in {
    val code =
      "IT'S SHOWTIME\n" +
        "COVER ME background\n" +
        "CHILL OUT FOR 3000\n" +
        "MISSION COMPLETE\n" +
        "TALK TO THE HAND \"main done\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("main done\n")

    import scala.collection.JavaConverters._
    def findAsyncThread: Option[Thread] =
      Thread.getAllStackTraces.asScala.collectFirst {
        case (t, frames) if frames.exists(_.getClassName.startsWith("Hello$async$")) => t
      }
    // The block sleeps for 3s, so its thread must still be findable right after main returns.
    val deadline = System.currentTimeMillis + 2000
    var found = findAsyncThread
    while (found.isEmpty && System.currentTimeMillis < deadline) {
      Thread.sleep(10)
      found = findAsyncThread
    }
    withClue("could not locate the running async thread: ") { found should be(defined) }
    found.get.isDaemon should be(true)
  }

  it should "compute a result in the async block before awaiting" in {
    val code =
      "IT'S SHOWTIME\n" +
        "COVER ME task\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER r\n" +
        "HERE IS MY INVITATION 6\n" +
        "YOU'RE FIRED 7\n" +
        "ENOUGH TALK\n" +
        "I'LL BE BACK r\n" +
        "MISSION COMPLETE\n" +
        "HOLD THE LINE task\n" +
        "HEY CHRISTMAS TREE answer\n" +
        "YOU SET US UP task.result\n" +
        "TALK TO THE HAND answer\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("42\n")
  }
}
