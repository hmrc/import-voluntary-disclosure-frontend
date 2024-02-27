import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val bootstrapVersion = "8.4.0"

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"                  %% "play-frontend-hmrc-play-30"         % bootstrapVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % "1.7.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.16.1",
    "com.sun.mail"                  % "javax.mail"                 % "1.6.2"
  )

  val test = Seq(
    "org.scalamock"          %% "scalamock"              % "5.2.0"        ,
    "com.github.tomakehurst"  % "wiremock-jre8"          % "3.0.1"        ,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion ,
    "org.scalatest"          %% "scalatest"              % "3.2.17"         ,
    "org.jsoup"               % "jsoup"                  % "1.15.3"        ,
    "org.playframework"      %% "play-test"              % current         ,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.64.8"         ,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.1"
  ).map(_ % Test)
}
