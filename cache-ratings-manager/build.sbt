name := """cache-ratings-manager"""
organization := "ch.xavier"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

maintainer := "xavier@myEmailStaysPrivateThankYou.org"

packageName in Universal := s"${name.value}"

libraryDependencies ++= Seq(
  guice,
  ws,
//  ehcache,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0",
  "com.geteventstore" %% "eventstore-client" % "7.0.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.newmotion" %% "akka-rabbitmq" % "5.1.2",
  "com.kenshoo" %% "metrics-play" % "2.7.3_0.8.2"
)
