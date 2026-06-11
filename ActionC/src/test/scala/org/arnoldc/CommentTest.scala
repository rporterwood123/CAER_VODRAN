package org.arnoldc

class CommentTest extends ArnoldGeneratorTest {

  it should "ignore a single-line comment on its own line" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I'M BATMAN this whole line is ignored\n" +
        "TALK TO THE HAND \"hi\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("hi\n")
  }

  it should "ignore a trailing single-line comment after a statement" in {
    val code =
      "IT'S SHOWTIME\n" +
        "TALK TO THE HAND \"hi\" I'M BATMAN prints hi\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("hi\n")
  }

  it should "ignore a multi-line block comment" in {
    val code =
      "IT'S SHOWTIME\n" +
        "GATHER ROUND\n" +
        "this is a block comment\n" +
        "spanning multiple lines\n" +
        "DISMISSED\n" +
        "TALK TO THE HAND \"hi\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("hi\n")
  }

  it should "ignore consecutive comment lines between statements" in {
    val code =
      "IT'S SHOWTIME\n" +
        "TALK TO THE HAND \"one\"\n" +
        "I'M BATMAN first comment\n" +
        "I'M BATMAN second comment\n" +
        "TALK TO THE HAND \"two\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("one\ntwo\n")
  }
}
