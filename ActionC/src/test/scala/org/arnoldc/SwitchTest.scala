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
}
