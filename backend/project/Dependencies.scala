import sbt._

object Dependencies {
  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.13" % Test,
    "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0" % Test,
     "com.holdenkarau" %% "spark-testing-base" % "3.5.0_1.4.7" % Test
  )

  val circeVersion = "0.13.0"
  val pureconfigVersion = "0.15.0"
  val sparkVersion = "3.2.1"

  lazy val core = Seq(

    // support for JSON formats
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,
    "com.github.scopt" %% "scopt" % "4.0.0",

    // support for typesafe configuration
    "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,

    // parallel collections
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    
    // spark
    // "org.apache.spark" %% "spark-sql" % sparkVersion % Provided, // for submiting spark app as a job to cluster
    "org.apache.spark" %% "spark-sql" % sparkVersion, // for simple standalone spark app

     
    // spark - excluding slf4j-log4j12 to avoid SLF4J multiple bindings issue
    "org.apache.spark" %% "spark-sql" % sparkVersion exclude("org.slf4j", "slf4j-log4j12"),
    "com.lihaoyi" %% "requests" % "0.6.5", 
    
    // logging
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}
