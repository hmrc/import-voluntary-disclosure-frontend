import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.6.0",
    "uk.gov.hmrc" %% "play-frontend-hmrc" % "0.79.0-play-28",
    "uk.gov.hmrc" %% "play-frontend-govuk" % "0.79.0-play-28",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.51.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.3"
  )

  val test = Seq(
    "org.scalamock" %% "scalamock" % "5.1.0" % Test,
    "com.github.tomakehurst" % "wiremock-jre8" % "2.27.2" % "test, it",
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.4.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.8" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % "test, it",
    "org.jsoup" % "jsoup" % "1.13.1" % Test,
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test, it"
  )
}
