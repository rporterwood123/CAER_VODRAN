package org.arnoldc

class AssertTest extends ArnoldGeneratorTest {

  it should "continue past an assertion whose condition is true" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AM THE LAW @NO PROBLEMO\n" +
        "TALK TO THE HAND \"passed\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("passed\n")
  }

  it should "continue when an asserted variable condition is true" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE ok\n" +
        "YOU SET US UP 1\n" +
        "I AM THE LAW ok \"must be ok\"\n" +
        "TALK TO THE HAND \"passed\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("passed\n")
  }

  it should "throw when an assertion condition is false" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I AM THE LAW @I LIED \"this should fail\"\n" +
        "TALK TO THE HAND \"unreachable\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    assertThrows[Throwable] {
      getOutput(code)
    }
  }
}
