package org.arnoldc

class OOPTest extends ArnoldGeneratorTest {

  it should "create an instance and read a default (zero) field" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "OPEN TO THE PUBLIC health\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "TALK TO THE HAND hero.health\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("0\n")
  }

  it should "write and read an instance field" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "OPEN TO THE PUBLIC score\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "GET TO THE CHOPPER hero.score\n" +
        "HERE IS MY INVITATION 500\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND hero.score\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("500\n")
  }

  it should "initialize fields in a constructor using bare field names" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "OPEN TO THE PUBLIC health\n" +
        "IT'S ALIVE\n" +
        "GET TO THE CHOPPER health\n" +
        "HERE IS MY INVITATION 100\n" +
        "ENOUGH TALK\n" +
        "BIRTH COMPLETE\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "TALK TO THE HAND hero.health\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("100\n")
  }

  it should "support multiple independent instances and classes" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "OPEN TO THE PUBLIC health\n" +
        "STRENGTH AND HONOR\n" +
        "MY NAME IS MAXIMUS Enemy\n" +
        "OPEN TO THE PUBLIC damage\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "WELCOME TO EARTH monster AS Enemy\n" +
        "GET TO THE CHOPPER hero.health\n" +
        "HERE IS MY INVITATION 100\n" +
        "ENOUGH TALK\n" +
        "GET TO THE CHOPPER monster.damage\n" +
        "HERE IS MY INVITATION 25\n" +
        "ENOUGH TALK\n" +
        "TALK TO THE HAND hero.health\n" +
        "TALK TO THE HAND monster.damage\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("100\n25\n")
  }
}
