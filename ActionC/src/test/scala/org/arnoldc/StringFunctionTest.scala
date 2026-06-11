package org.arnoldc

class StringFunctionTest extends ArnoldGeneratorTest {

  // Build a string via a string-returning function and print it.
  private def stringFn(source: String, fnApplied: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"" + source + "\"\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM out\n" +
        "AND KICK ASS " + fnApplied + "\n" +
        "TALK TO THE HAND out\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  // Apply an int-returning string function and print the int.
  private def intFn(source: String, fnApplied: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"" + source + "\"\n" +
        "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP " + fnApplied + "\n" +
        "TALK TO THE HAND n\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "compute string length" in {
    intFn("hello", "HOW LONG IS THIS THING s") should equal("5\n")
  }

  it should "uppercase a string" in {
    stringFn("hello", "SAY IT LOUDER s") should equal("HELLO\n")
  }

  it should "lowercase a string" in {
    stringFn("HELLO", "KEEP YOUR VOICE DOWN s") should equal("hello\n")
  }

  it should "trim a string" in {
    stringFn("  hi  ", "CUT THE FAT FROM s") should equal("hi\n")
  }

  it should "take a substring" in {
    stringFn("hello", "GIVE ME A PIECE OF s FROM 1 TO 3") should equal("el\n")
  }

  it should "detect a contained substring" in {
    intFn("hello world", "YOU TALKING TO ME ABOUT s \"world\"") should equal("1\n")
  }

  it should "report a missing substring as not contained" in {
    intFn("hello world", "YOU TALKING TO ME ABOUT s \"xyz\"") should equal("0\n")
  }

  it should "find the index of a substring" in {
    intFn("hello", "WHERE IS IT IN s \"l\"") should equal("2\n")
  }
}
