package org.arnoldc

import org.objectweb.asm.Opcodes._

// The type of a variable/value, carrying the JVM details needed to load, store,
// and describe it. ActionC values occupy a single JVM local slot each (int and
// 32-bit float are single-slot; strings/arrays/objects are references), so the
// one-slot-per-variable address allocator in SymbolTable remains valid.
sealed trait VariableType {
  def loadOpcode: Int
  def storeOpcode: Int
  def descriptor: String
}

// An array type also knows how to load/store its elements and its element's own
// scalar type (used for int->float coercion and print/arithmetic dispatch).
sealed trait ArrayVariableType extends VariableType {
  def elementType: VariableType
  def elementLoadOpcode: Int
  def elementStoreOpcode: Int
}

object VariableType {

  case object IntType extends VariableType {
    val loadOpcode = ILOAD
    val storeOpcode = ISTORE
    val descriptor = "I"
  }

  case object FloatType extends VariableType {
    val loadOpcode = FLOAD
    val storeOpcode = FSTORE
    val descriptor = "F"
  }

  case object StringType extends VariableType {
    val loadOpcode = ALOAD
    val storeOpcode = ASTORE
    val descriptor = "Ljava/lang/String;"
  }

  case object IntArrayType extends ArrayVariableType {
    val loadOpcode = ALOAD
    val storeOpcode = ASTORE
    val descriptor = "[I"
    val elementType = IntType
    val elementLoadOpcode = IALOAD
    val elementStoreOpcode = IASTORE
  }

  case object FloatArrayType extends ArrayVariableType {
    val loadOpcode = ALOAD
    val storeOpcode = ASTORE
    val descriptor = "[F"
    val elementType = FloatType
    val elementLoadOpcode = FALOAD
    val elementStoreOpcode = FASTORE
  }

  case object StringArrayType extends ArrayVariableType {
    val loadOpcode = ALOAD
    val storeOpcode = ASTORE
    val descriptor = "[Ljava/lang/String;"
    val elementType = StringType
    val elementLoadOpcode = AALOAD
    val elementStoreOpcode = AASTORE
  }

  case class ObjectType(className: String) extends VariableType {
    val loadOpcode = ALOAD
    val storeOpcode = ASTORE
    val descriptor = "L" + className + ";"
  }
}
