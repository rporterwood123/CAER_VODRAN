package org.arnoldc

import org.parboiled.errors.ParsingException

class StringEscapeTest extends ArnoldGeneratorTest {

  // Print a single string literal and return its captured output.
  def printLiteral(literal: String): String =
    getOutput(
      "IT'S SHOWTIME\n" +
        "TALK TO THE HAND \"" + literal + "\"\n" +
        "YOU HAVE BEEN TERMINATED\n")

  // Declare a string var from a literal and print its length.
  def lengthOf(literal: String): String =
    getOutput(
      "IT'S SHOWTIME\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM s\n" +
        "AND KICK ASS \"" + literal + "\"\n" +
        "TALK TO THE HAND HOW LONG IS THIS THING s\n" +
        "YOU HAVE BEEN TERMINATED\n")

  it should "decode \\n into a newline" in {
    printLiteral("a\\nb") should equal("a\nb\n")
  }

  it should "decode \\t into a tab" in {
    printLiteral("a\\tb") should equal("a\tb\n")
  }

  it should "decode \\r into a carriage return" in {
    printLiteral("a\\rb") should equal("a\rb\n")
  }

  it should "decode \\\" into an embedded quote without terminating the string" in {
    printLiteral("say \\\"hi\\\"") should equal("say \"hi\"\n")
  }

  it should "decode \\\\ into a single backslash" in {
    printLiteral("a\\\\b") should equal("a\\b\n")
  }

  it should "treat an escaped backslash as one character" in {
    lengthOf("a\\\\b") should equal("3\n")
  }

  it should "decode \\0 into a NUL character counted as one char" in {
    lengthOf("a\\0b") should equal("3\n")
  }

  it should "decode a \\uXXXX unicode escape" in {
    printLiteral("caf\\u00e9") should equal("café\n")
  }

  it should "leave a backslash-free string unchanged" in {
    printLiteral("plain text 123") should equal("plain text 123\n")
  }

  it should "reject an unrecognized escape at compile time" in {
    a[ParsingException] should be thrownBy printLiteral("bad \\q here")
  }

  it should "reject a malformed unicode escape at compile time" in {
    a[ParsingException] should be thrownBy printLiteral("short \\u12")
  }
}
