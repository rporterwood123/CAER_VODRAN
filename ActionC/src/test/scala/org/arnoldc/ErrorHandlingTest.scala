package org.arnoldc

class ErrorHandlingTest extends ArnoldGeneratorTest {

  it should "catch a thrown error and expose its message" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "TALK TO THE HAND \"before\"\n" +
        "WELCOME TO THE PARTY PAL \"boom\"\n" +
        "TALK TO THE HAND \"after throw\"\n" +
        "GOTCHA err\n" +
        "TALK TO THE HAND \"caught\"\n" +
        "TALK TO THE HAND err\n" +
        "THAT'S A WRAP\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("before\ncaught\nboom\n")
  }

  it should "dispatch an exception to the innermost enclosing catch" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "WELCOME TO THE PARTY PAL \"inner boom\"\n" +
        "GOTCHA inner\n" +
        "TALK TO THE HAND \"inner caught\"\n" +
        "THAT'S A WRAP\n" +
        "GOTCHA outer\n" +
        "TALK TO THE HAND \"outer caught\"\n" +
        "THAT'S A WRAP\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("inner caught\n")
  }

  it should "run the finally block when the catch body itself throws" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "WELCOME TO THE PARTY PAL \"first\"\n" +
        "GOTCHA inner\n" +
        "WELCOME TO THE PARTY PAL \"second\"\n" +
        "CLEAN UP ON AISLE FIVE\n" +
        "TALK TO THE HAND \"FINALLY RAN\"\n" +
        "THAT'S A WRAP\n" +
        "GOTCHA outer\n" +
        "TALK TO THE HAND outer\n" +
        "THAT'S A WRAP\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("FINALLY RAN\nsecond\n")
  }

  it should "run the finally block when no exception occurs" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "TALK TO THE HAND \"ok\"\n" +
        "GOTCHA err\n" +
        "TALK TO THE HAND \"should not run\"\n" +
        "CLEAN UP ON AISLE FIVE\n" +
        "TALK TO THE HAND \"cleanup\"\n" +
        "THAT'S A WRAP\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("ok\ncleanup\n")
  }

  it should "run the finally block after catching an exception" in {
    val code =
      "IT'S SHOWTIME\n" +
        "LET'S SEE WHAT YOU'VE GOT\n" +
        "WELCOME TO THE PARTY PAL \"x\"\n" +
        "GOTCHA err\n" +
        "TALK TO THE HAND \"handled\"\n" +
        "CLEAN UP ON AISLE FIVE\n" +
        "TALK TO THE HAND \"cleanup\"\n" +
        "THAT'S A WRAP\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("handled\ncleanup\n")
  }
}
