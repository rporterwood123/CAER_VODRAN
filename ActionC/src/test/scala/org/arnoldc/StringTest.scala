package org.arnoldc

class StringTest extends ArnoldGeneratorTest {

  it should "declare, initialize, and print a string variable" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM greeting\n" +
        "AND KICK ASS \"Hello World\"\n" +
        "TALK TO THE HAND greeting\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("Hello World\n")
  }

  it should "concatenate multiple string literals" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM g\n" +
        "AND KICK ASS \"Hello \"\n" +
        "AND KICK ASS \"World\"\n" +
        "TALK TO THE HAND g\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("Hello World\n")
  }

  it should "support the empty string literal" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM e\n" +
        "AND KICK ASS AND I'M ALL OUT OF BUBBLEGUM\n" +
        "TALK TO THE HAND e\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("\n")
  }

  it should "concatenate a string variable with a literal" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM a\n" +
        "AND KICK ASS \"foo\"\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM b\n" +
        "AND KICK ASS a\n" +
        "AND KICK ASS \"bar\"\n" +
        "TALK TO THE HAND b\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("foobar\n")
  }
}
