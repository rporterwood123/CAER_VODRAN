package org.arnoldc

class AsyncTest extends ArnoldGeneratorTest {

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
