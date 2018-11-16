name := "linkerd-static-namer"

version := "1.5.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq("io.buoyant" %% "linkerd-core" % version.value % Provided)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
