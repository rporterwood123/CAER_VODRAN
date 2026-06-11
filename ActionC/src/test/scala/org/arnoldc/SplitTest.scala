package org.arnoldc

class SplitTest extends ArnoldGeneratorTest {

  private val csv =
    "I HAVE COME HERE TO CHEW BUBBLEGUM csv\n" +
      "AND KICK ASS \"alpha,beta,gamma\"\n" +
      "DIVIDE AND CONQUER parts csv \",\"\n"

  it should "split a string into an array (DIVIDE AND CONQUER)" in {
    val code =
      "IT'S SHOWTIME\n" + csv +
        "TALK TO THE HAND HOW MANY OF THEM parts\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("3\n")
  }

  it should "read an element of a split result" in {
    val code =
      "IT'S SHOWTIME\n" + csv +
        "TALK TO THE HAND GET IN LINE parts AT 1\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("beta\n")
  }

  it should "concatenate a split element into a string" in {
    val code =
      "IT'S SHOWTIME\n" + csv +
        "I HAVE COME HERE TO CHEW BUBBLEGUM out\n" +
        "AND KICK ASS \"first=\"\n" +
        "AND KICK ASS GET IN LINE parts AT 0\n" +
        "TALK TO THE HAND out\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("first=alpha\n")
  }

  it should "split on a literal dot delimiter" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM ip\n" +
        "AND KICK ASS \"10.0.0.1\"\n" +
        "DIVIDE AND CONQUER octets ip \".\"\n" +
        "TALK TO THE HAND HOW MANY OF THEM octets\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("4\n")
  }
}
