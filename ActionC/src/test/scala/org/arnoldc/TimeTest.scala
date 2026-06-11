package org.arnoldc

class TimeTest extends ArnoldGeneratorTest {

  it should "sleep for a duration and continue (CHILL OUT FOR)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "CHILL OUT FOR 5\n" +
        "TALK TO THE HAND \"done\"\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("done\n")
  }

  it should "read the current time as an integer (WHAT TIME IS IT)" in {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE t\n" +
        "YOU SET US UP WHAT TIME IS IT\n" +
        "TALK TO THE HAND t\n" +
        "YOU HAVE BEEN TERMINATED\n"
    val out = getOutput(code).trim
    noException should be thrownBy out.toInt
  }
}
