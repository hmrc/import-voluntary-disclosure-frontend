import play.core.PlayVersion.current
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.6.0"
  private val playVersion      = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc"                  %% s"play-frontend-hmrc-$playVersion" % "9.2.0",
    "uk.gov.hmrc.mongo"            %% s"hmrc-mongo-$playVersion"         % "1.7.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"             % "2.17.0",
    "com.sun.mail"                  % "javax.mail"                       % "1.6.2"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalamock"          %% "scalamock"                    % "5.2.0",
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.jsoup"               % "jsoup"                        % "1.17.2",
    "org.playframework"      %% "play-test"                    % current
  ).map(_ % Test)
}
