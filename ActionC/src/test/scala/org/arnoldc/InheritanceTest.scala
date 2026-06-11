package org.arnoldc

class InheritanceTest extends ArnoldGeneratorTest {

  it should "inherit fields and run the parent constructor" in {
    val code =
      "MY NAME IS MAXIMUS Vehicle\n" +
        "OPEN TO THE PUBLIC speed\n" +
        "IT'S ALIVE\n" +
        "GET TO THE CHOPPER LOOK AT ME.speed\n" +
        "HERE IS MY INVITATION 60\n" +
        "ENOUGH TALK\n" +
        "BIRTH COMPLETE\n" +
        "STRENGTH AND HONOR\n" +
        "MY NAME IS MAXIMUS Car LIKE FATHER LIKE SON Vehicle\n" +
        "OPEN TO THE PUBLIC gear\n" +
        "IT'S ALIVE\n" +
        "GET TO THE CHOPPER LOOK AT ME.gear\n" +
        "HERE IS MY INVITATION 1\n" +
        "ENOUGH TALK\n" +
        "BIRTH COMPLETE\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH myCar AS Car\n" +
        "TALK TO THE HAND myCar.speed\n" +
        "TALK TO THE HAND myCar.gear\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("60\n1\n")
  }

  it should "inherit a parent instance method" in {
    val code =
      "MY NAME IS MAXIMUS Animal\n" +
        "COMMANDER IN CHIEF speak\n" +
        "GIVE THESE PEOPLE AIR\n" +
        "I'LL BE BACK 42\n" +
        "DISMISSED SOLDIER\n" +
        "STRENGTH AND HONOR\n" +
        "MY NAME IS MAXIMUS Dog LIKE FATHER LIKE SON Animal\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH d AS Dog\n" +
        "HEY CHRISTMAS TREE n\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS n\n" +
        "DO IT NOW d.speak\n" +
        "TALK TO THE HAND n\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("42\n")
  }
}
