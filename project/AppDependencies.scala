import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.15.0"

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"                  %% "play-frontend-hmrc"         % "7.0.0-play-28",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"         % "1.3.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.13.4",
    "com.sun.mail"                  % "javax.mail"                 % "1.6.2"
  )

  val test = Seq(
    "org.scalamock"          %% "scalamock"              % "5.2.0"          % Test,
    "com.github.tomakehurst"  % "wiremock-jre8"          % "2.33.2"         % "test, it",
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.13"         % "test, it",
    "org.jsoup"               % "jsoup"                  % "1.15.3"         % Test,
    "com.typesafe.play"      %% "play-test"              % current          % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2"         % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"          % "test, it"
  )
}
