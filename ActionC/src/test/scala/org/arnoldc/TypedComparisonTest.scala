package org.arnoldc

import org.parboiled.errors.ParsingException

class TypedComparisonTest extends ArnoldGeneratorTest {

  private def compareStrings(a: String, b: String, op: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"" + a + "\"\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM t\n" +
        "AND KICK ASS \"" + b + "\"\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER r\n" +
        "HERE IS MY INVITATION s\n" +
        op + " t\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND r\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  private def compareFloats(a: String, b: String, op: String): String = {
    val code =
      "IT'S SHOWTIME\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER r\n" +
        "HERE IS MY INVITATION " + a + "\n" +
        op + " " + b + "\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND r\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code)
  }

  it should "evaluate equal strings as equal" in {
    compareStrings("hello", "hello", "YOU ARE NOT YOU YOU ARE ME") should equal("1\n")
  }

  it should "evaluate different strings as not equal" in {
    compareStrings("hello", "world", "YOU ARE NOT YOU YOU ARE ME") should equal("0\n")
  }

  it should "evaluate string inequality" in {
    compareStrings("hello", "world", "IT'S JUST BEEN REVOKED") should equal("1\n")
    compareStrings("same", "same", "IT'S JUST BEEN REVOKED") should equal("0\n")
  }

  it should "reject comparing a string with a number at compile time" in {
    val code =
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"hello\"\n" +
        "HEY CHRISTMAS TREE r\n" +
        "YOU SET US UP 0\n" +
        "GET TO THE CHOPPER r\n" +
        "HERE IS MY INVITATION s\n" +
        "YOU ARE NOT YOU YOU ARE ME 5\n" +
        "ENOUGH TALK\n" +
        "YOU HAVE BEEN TERMINATED\n"
    a[ParsingException] should be thrownBy getOutput(code)
  }

  it should "reject ordering comparisons on strings at compile time" in {
    a[ParsingException] should be thrownBy
      compareStrings("a", "b", "LET OFF SOME STEAM BENNET")
  }

  it should "compare floats for equality" in {
    compareFloats("1.5", "1.5", "YOU ARE NOT YOU YOU ARE ME") should equal("1\n")
    compareFloats("1.5", "2.5", "YOU ARE NOT YOU YOU ARE ME") should equal("0\n")
  }

  it should "compare floats for inequality" in {
    compareFloats("1.5", "2.5", "IT'S JUST BEEN REVOKED") should equal("1\n")
  }

  it should "compare floats with greater than" in {
    compareFloats("2.5", "1.5", "LET OFF SOME STEAM BENNET") should equal("1\n")
    compareFloats("1.5", "2.5", "LET OFF SOME STEAM BENNET") should equal("0\n")
  }

  it should "compare floats with less than" in {
    compareFloats("1.5", "2.5", "YOU'RE THE DISEASE AND I'M THE CURE") should equal("1\n")
  }

  it should "compare floats with greater or equal and less or equal" in {
    compareFloats("1.5", "1.5", "I'M GETTING TOO OLD FOR THIS") should equal("1\n")
    compareFloats("1.5", "1.5", "BENEATH YOU") should equal("1\n")
    compareFloats("1.5", "2.5", "I'M GETTING TOO OLD FOR THIS") should equal("0\n")
  }

  it should "promote an int operand when compared with a float" in {
    compareFloats("2", "1.5", "LET OFF SOME STEAM BENNET") should equal("1\n")
    compareFloats("1.5", "2", "YOU'RE THE DISEASE AND I'M THE CURE") should equal("1\n")
  }
}
