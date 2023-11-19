import Dependencies._
scalaVersion := "2.13.10"
organization := "org.ferreira.yuri"
name := "PortfolioOptimization"
// version := "0.1"



lazy val root = (project in file(".")).
  settings(
    name :=  "PortfolioOptimization",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps",
      "-language:higherKinds", // HKT required for Monads and other HKT types
      "-Wunused", // for scalafix
    ),
    Compile / run / fork := true, // cleaner to run programs in a JVM different from sbt
    run / javaOptions ++= Seq(
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/java.io=ALL-UNNAMED",
      "--add-opens=java.base/java.net=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
      "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
      "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"
    ), // needed to run Spark with Java 17
    libraryDependencies ++= Dependencies.core ++ Dependencies.scalaTest,

  )

// libraryDependencies ++= Dependencies.core ++ Dependencies.scalaTest