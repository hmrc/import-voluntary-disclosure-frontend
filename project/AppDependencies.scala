import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.7.0"
  private val playVersion      = "play-30"

  val mailDependencies: Seq[ModuleID] = Seq(
    "jakarta.mail"      % "jakarta.mail-api" % "2.1.5",
    "org.eclipse.angus" % "angus-mail"       % "2.0.5"
  )

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc"                  %% s"play-frontend-hmrc-$playVersion" % "12.31.0",
    "uk.gov.hmrc.mongo"            %% s"hmrc-mongo-$playVersion"         % "2.12.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"             % "2.21.0"
  ) ++ mailDependencies

  val test: Seq[ModuleID] = (Seq(
    "org.scalamock"          %% "scalamock"                    % "7.5.5",
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.jsoup"               % "jsoup"                        % "1.22.1",
    "org.playframework"      %% "play-test"                    % current,
    "com.vladsch.flexmark"    % "flexmark-all"                 % "0.64.8",
    "org.scalatestplus.play" %% "scalatestplus-play"           % "7.0.2",
    "org.scalatest"          %% "scalatest"                    % "3.2.19",
  ) ++ mailDependencies).map(_ % Test)
}
