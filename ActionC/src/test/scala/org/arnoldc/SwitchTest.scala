package org.arnoldc

class SwitchTest extends ArnoldGeneratorTest {

  private def switchOn(value: Int): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP " + value + "\n" +
        "CHOOSE YOUR DESTINY x\n" +
        "WHAT IF I TOLD YOU 1\n" +
        "TALK TO THE HAND \"one\"\n" +
        "WHAT IF I TOLD YOU 2\n" +
        "TALK TO THE HAND \"two\"\n" +
        "SAME OLD SAME OLD\n" +
        "TALK TO THE HAND \"other\"\n" +
        "FINISH HIM\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "select the matching case" in {
    switchOn(1) should equal("one\n")
  }

  it should "select a different matching case" in {
    switchOn(2) should equal("two\n")
  }

  it should "fall to the default clause when nothing matches" in {
    switchOn(9) should equal("other\n")
  }

  it should "not fall through to the next case" in {
    // value 1 must print only "one", never "two"
    switchOn(1) should not include "two"
  }

  it should "do nothing when no case matches and there is no default" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP 7\n" +
        "CHOOSE YOUR DESTINY x\n" +
        "WHAT IF I TOLD YOU 1\n" +
        "TALK TO THE HAND \"one\"\n" +
        "FINISH HIM\n" +
        "TALK TO THE HAND \"after\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("after\n")
  }

  it should "support negative case values" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP -3\n" +
        "CHOOSE YOUR DESTINY x\n" +
        "WHAT IF I TOLD YOU -3\n" +
        "TALK TO THE HAND \"neg\"\n" +
        "FINISH HIM\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("neg\n")
  }

  it should "exit the switch early with GET OUT" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE x\n" +
        "YOU SET US UP 2\n" +
        "CHOOSE YOUR DESTINY x\n" +
        "WHAT IF I TOLD YOU 2\n" +
        "TALK TO THE HAND \"before\"\n" +
        "GET OUT\n" +
        "TALK TO THE HAND \"after\"\n" +
        "FINISH HIM\n" +
        "TALK TO THE HAND \"done\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("before\ndone\n")
  }

  it should "break only the switch when nested in a loop" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 3\n" +
        "CHOOSE YOUR DESTINY i\n" +
        "WHAT IF I TOLD YOU 2\n" +
        "TALK TO THE HAND \"hit\"\n" +
        "GET OUT\n" +
        "TALK TO THE HAND \"skipped\"\n" +
        "FINISH HIM\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\nhit\n2\n3\n")
  }

  it should "let KEEP MOVING inside a switch continue the enclosing loop" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S ROCK i FROM 1 TO 3\n" +
        "CHOOSE YOUR DESTINY i\n" +
        "WHAT IF I TOLD YOU 2\n" +
        "KEEP MOVING\n" +
        "FINISH HIM\n" +
        "TALK TO THE HAND i\n" +
        "GAME OVER MAN GAME OVER\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("1\n3\n")
  }
}
