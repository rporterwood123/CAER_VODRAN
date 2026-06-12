package org.arnoldc

import java.io.File
import java.lang.reflect.InvocationTargetException

object Executor {

  def execute(directory: File, className: String) = {
    val classLoader = new java.net.URLClassLoader(Array(directory.toURI.toURL), getClass.getClassLoader)
    val mainMethod = classLoader.loadClass(className).getMethod("main", classOf[Array[String]])
    try {
      mainMethod.invoke(null, Array.empty[String])
    } catch {
      // Surface the program's own exception rather than the reflection wrapper.
      case e: InvocationTargetException => throw e.getCause
    }
  }

}
