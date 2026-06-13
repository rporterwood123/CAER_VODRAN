package org.arnoldc.ast

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes._
import org.arnoldc.SymbolTable
import org.parboiled.errors.ParsingException

// `value` holds the raw source text of a string literal, with backslash escape
// sequences still intact (the grammar captures it verbatim). The actual runtime
// string is produced by decoding the escapes in `generate`. Internally-built
// StringNodes (empty string, default messages) contain no backslashes, so for
// them decoding is a no-op.
case class StringNode(value: String) extends OperandNode {
  def generate(mv: MethodVisitor, symbolTable: SymbolTable) {
    mv.visitLdcInsn(StringNode.decodeEscapes(value))
  }
}

object StringNode {
  // Turn the raw literal text into its runtime value, expanding the supported
  // escape sequences (\n \t \r \" \\ \0 and 4-hex-digit unicode). An unrecognized or
  // malformed escape is a compile-time error, surfaced as a ParsingException
  // like other invalid-program rejections.
  def decodeEscapes(raw: String): String = {
    val sb = new StringBuilder
    var i = 0
    val n = raw.length
    while (i < n) {
      val c = raw.charAt(i)
      if (c != '\\') {
        sb.append(c)
        i += 1
      } else if (i + 1 >= n) {
        throw new ParsingException("DANGLING BACKSLASH IN STRING LITERAL")
      } else {
        raw.charAt(i + 1) match {
          case 'n'  => sb.append('\n'); i += 2
          case 't'  => sb.append('\t'); i += 2
          case 'r'  => sb.append('\r'); i += 2
          case '"'  => sb.append('"'); i += 2
          case '\\' => sb.append('\\'); i += 2
          case '0'  => sb.append(0.toChar); i += 2
          case 'u'  =>
            if (i + 6 > n)
              throw new ParsingException(
                "MALFORMED UNICODE ESCAPE '\\" + "u" + raw.substring(i + 2) + "'")
            val hex = raw.substring(i + 2, i + 6)
            val code =
              try Integer.parseInt(hex, 16)
              catch {
                case _: NumberFormatException =>
                  throw new ParsingException("MALFORMED UNICODE ESCAPE '\\" + "u" + hex + "'")
              }
            sb.append(code.toChar)
            i += 6
          case other =>
            throw new ParsingException("INVALID ESCAPE SEQUENCE '\\" + other + "'")
        }
      }
    }
    sb.toString
  }
}
