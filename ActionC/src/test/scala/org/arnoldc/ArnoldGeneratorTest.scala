package org.arnoldc

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

abstract  class ArnoldGeneratorTest extends AnyFlatSpec with Matchers {

  val arnoldGenerator = new ArnoldGenerator
  val byteCodeExecutor = new ByteCodeExecutor
  var className = "Hello"

  def getOutput(arnoldCode: String): String = {
    val (classes, root) = arnoldGenerator.generate(arnoldCode, className)
    byteCodeExecutor.getOutput(classes, className)
  }

}