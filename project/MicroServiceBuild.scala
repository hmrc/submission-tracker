import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning

object MicroServiceBuild extends Build with MicroService {
  import play.sbt.routes.RoutesKeys._

  val appName = "submission-tracker"

  override lazy val plugins: Seq[Plugins] = Seq(
    SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin
  )

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
  override lazy val playSettings : Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.domain._", "uk.gov.hmrc.submissiontracker.binder.Binders._"))

}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val microserviceBootstrapVersion = "5.4.0"
  private val playAuthVersion = "4.2.0"
  private val playHealthVersion = "2.0.0"
  private val hmrcPlayJsonLoggerVersion = "2.1.1"
  private val logbackJsonLoggerVersion = "3.0.0"
  private val playUrlBindersVersion = "2.0.0"
  private val playConfigVersion = "3.0.0"
  private val domainVersion = "4.0.0"
  private val playHmrcApiVersion = "1.2.0"

  private val scalaTestVersion = "3.0.0"
  private val pegdownVersion = "1.6.0"
  private val wireMockVersion = "2.2.2"
  private val hmrcTestVersion = "2.0.0"
  private val cucumberVersion = "1.2.5"

  val compile = Seq(

    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-hmrc-api" % playHmrcApiVersion,
    "uk.gov.hmrc" %% "play-authorisation" % playAuthVersion,
    "uk.gov.hmrc" %% "play-health" % playHealthVersion,
    "uk.gov.hmrc" %% "play-url-binders" % playUrlBindersVersion,
    "uk.gov.hmrc" %% "play-config" % playConfigVersion,
    "uk.gov.hmrc" %% "logback-json-logger" % logbackJsonLoggerVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "reactive-circuit-breaker" % "1.7.0",
    "uk.gov.hmrc" %% "emailaddress" % "1.1.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "info.cukes" %% "cucumber-scala" % cucumberVersion % scope,
        "info.cukes" % "cucumber-junit" % cucumberVersion % scope,
        "com.github.tomakehurst" % "wiremock" % wireMockVersion % scope,
        "org.mockito" % "mockito-all" % "1.9.5" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

