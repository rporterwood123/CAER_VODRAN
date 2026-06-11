package org.arnoldc

import java.nio.file.{Files, Paths}

class FileIOTest extends ArnoldGeneratorTest {

  private def cleanup(paths: String*): Unit =
    paths.foreach(p => Files.deleteIfExists(Paths.get(p)))

  it should "write a file and read it back" in {
    val path = "actionc_io_roundtrip.txt"
    try {
      val code =
        "IT'S SHOWTIME\n" +
          "WRITE THAT DOWN \"hello file\" TO \"" + path + "\"\n" +
          "I HAVE COME HERE TO CHEW BUBBLEGUM content\n" +
          "AND KICK ASS WHAT'S IN THE BOX \"" + path + "\"\n" +
          "TALK TO THE HAND content\n" +
          "YOU HAVE BEEN TERMINATED\n"
      getOutput(code) should equal("hello file\n")
    } finally {
      cleanup(path)
    }
  }

  it should "report existence and delete a file" in {
    val path = "actionc_io_exists.txt"
    try {
      val code =
        "IT'S SHOWTIME\n" +
          "WRITE THAT DOWN \"x\" TO \"" + path + "\"\n" +
          "HEY CHRISTMAS TREE e1\n" +
          "YOU SET US UP HONEY I'M HOME \"" + path + "\"\n" +
          "TALK TO THE HAND e1\n" +
          "SEAL THE EXITS \"" + path + "\"\n" +
          "HEY CHRISTMAS TREE e2\n" +
          "YOU SET US UP HONEY I'M HOME \"" + path + "\"\n" +
          "TALK TO THE HAND e2\n" +
          "YOU HAVE BEEN TERMINATED\n"
      getOutput(code) should equal("1\n0\n")
    } finally {
      cleanup(path)
    }
  }
}
