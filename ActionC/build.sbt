name := "ActionC"

version := "1.0"

scalaVersion := "2.12.18"

libraryDependencies += "org.ow2.asm" % "asm-commons" % "9.6"

libraryDependencies += "org.parboiled" %% "parboiled-scala" % "1.3.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"

Test / parallelExecution := false

// Assembly settings for creating fat JAR
assembly / assemblyJarName := "ActionC.jar"
assembly / mainClass := Some("org.arnoldc.ArnoldC")

// The ASM jars each ship a module-info.class; drop them in the fat jar.
assembly / assemblyMergeStrategy := {
  case "module-info.class" => MergeStrategy.discard
  case x =>
    val old = (assembly / assemblyMergeStrategy).value
    old(x)
}
