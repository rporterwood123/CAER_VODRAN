package org.arnoldc

class StringMethodTest extends ArnoldGeneratorTest {

  private def printString(expr: String): String =
    "IT'S SHOWTIME\n" +
      "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
      "AND KICK ASS \"foo bar foo\"\n" +
      "I HAVE COME HERE TO CHEW BUBBLEGUM out\n" +
      "AND KICK ASS " + expr + "\n" +
      "TALK TO THE HAND out\n" +
      "YOU HAVE BEEN TERMINATED\n"

  private def printInt(expr: String): String =
    "IT'S SHOWTIME\n" +
      "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
      "AND KICK ASS \"foo bar foo\"\n" +
      "HEY CHRISTMAS TREE n\n" +
      "YOU SET US UP " + expr + "\n" +
      "TALK TO THE HAND n\n" +
      "YOU HAVE BEEN TERMINATED\n"

  it should "replace all occurrences of a substring (GET A NEW ONE)" in {
    getOutput(printString("GET A NEW ONE s \"foo\" \"baz\"")) should equal("baz bar baz\n")
  }

  it should "report a matching prefix (FIRST BLOOD)" in {
    getOutput(printInt("FIRST BLOOD s \"foo\"")) should equal("1\n")
  }

  it should "report a non-matching prefix (FIRST BLOOD)" in {
    getOutput(printInt("FIRST BLOOD s \"bar\"")) should equal("0\n")
  }

  it should "report a matching suffix (LAST MAN STANDING)" in {
    getOutput(printInt("LAST MAN STANDING s \"foo\"")) should equal("1\n")
  }

  it should "extract a single character as a string (SHOW ME THE ONE AT)" in {
    getOutput(printString("SHOW ME THE ONE AT s 4")) should equal("b\n")
  }

  it should "reverse a string (PUT IT IN REVERSE)" in {
    getOutput(printString("PUT IT IN REVERSE s")) should equal("oof rab oof\n")
  }
}
