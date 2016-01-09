name := "ScalaApp"

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javacOptions ++= Seq("-Xlint:deprecation", "-encoding", "utf8","-XX:MaxPermSize=256m")

libraryDependencies ++= {
  val akkaGroup = "com.typesafe.akka"
  val akkaVersion = "2.3.12"
  val streamsVersion = "1.0"
  val scalaTestVersion = "2.2.4"
  Seq(
    akkaGroup %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core-experimental" % streamsVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % streamsVersion,
    "com.typesafe.akka" %% "akka-stream-experimental" % streamsVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % streamsVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
}

mainClass in (Compile, run) := Some("org.bluecollar.akka_http.example.WebServer")

mainClass in assembly := Some("org.bluecollar.akka_http.example.WebServer")

assemblyJarName in assembly := "scala-learn-app.jar"
