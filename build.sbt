
name := "linkerd-static-namer"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "io.buoyant" %% "linkerd-core" % "1.5.1" % Provided
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
