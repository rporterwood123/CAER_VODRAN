package org.arnoldc

import org.parboiled.errors.ParsingException

class InstanceMethodTest extends ArnoldGeneratorTest {

  it should "reject passing a string argument to an instance method at compile time" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "COMMANDER IN CHIEF speak\n" +
        "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE what\n" +
        "TALK TO THE HAND what\n" +
        "DISMISSED SOLDIER\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "I HAVE COME HERE TO CHEW BUBBLEGUM msg\n" +
        "AND KICK ASS \"hi\"\n" +
        "DO IT NOW hero.speak msg\n" +
        "YOU HAVE BEEN TERMINATED\n"
    a[ParsingException] should be thrownBy getOutput(code)
  }

  it should "call an instance method that mutates and returns a field via this" in {
    val code =
      "MY NAME IS MAXIMUS Player\n" +
        "OPEN TO THE PUBLIC health\n" +
        "IT'S ALIVE\n" +
        "GET TO THE CHOPPER LOOK AT ME.health\n" +
        "HERE IS MY INVITATION 100\n" +
        "ENOUGH TALK\n" +
        "BIRTH COMPLETE\n" +
        "COMMANDER IN CHIEF takeDamage\n" +
        "I NEED YOUR CLOTHES YOUR BOOTS AND YOUR MOTORCYCLE amount\n" +
        "GIVE THESE PEOPLE AIR\n" +
        "GET TO THE CHOPPER LOOK AT ME.health\n" +
        "HERE IS MY INVITATION LOOK AT ME.health\n" +
        "GET DOWN amount\n" +
        "ENOUGH TALK\n" +
        "I'LL BE BACK LOOK AT ME.health\n" +
        "DISMISSED SOLDIER\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH hero AS Player\n" +
        "HEY CHRISTMAS TREE newHealth\n" +
        "YOU SET US UP 0\n" +
        "GET YOUR ASS TO MARS newHealth\n" +
        "DO IT NOW hero.takeDamage 25\n" +
        "TALK TO THE HAND newHealth\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("75\n")
  }

  it should "call a void instance method that prints" in {
    val code =
      "MY NAME IS MAXIMUS Greeter\n" +
        "COMMANDER IN CHIEF greet\n" +
        "TALK TO THE HAND \"hello from method\"\n" +
        "DISMISSED SOLDIER\n" +
        "STRENGTH AND HONOR\n" +
        "IT'S SHOWTIME\n" +
        "WELCOME TO EARTH g AS Greeter\n" +
        "DO IT NOW g.greet\n" +
        "YOU HAVE BEEN TERMINATED\n"
    getOutput(code) should equal("hello from method\n")
  }
}
