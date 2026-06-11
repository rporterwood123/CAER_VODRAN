package org.arnoldc

class InstanceMethodTest extends ArnoldGeneratorTest {

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
        "GIVE THESE PEOPLE AIR\n" +
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
