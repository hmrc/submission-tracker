import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val microserviceBootstrapVersion = "6.9.0"
  private val domainVersion = "5.0.0"
  private val playHmrcApiVersion = "2.1.0"
  private val hmrcTestVersion = "3.0.0"
  private val wireMockVersion = "2.2.2"
  private val cucumberVersion = "1.2.5"
  private val reactiveCircuitBreakerVersion = "3.1.0"
  private val emailAdressVersion = "2.1.0"

  val compile = Seq(

    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "play-hmrc-api" % playHmrcApiVersion,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "reactive-circuit-breaker" % reactiveCircuitBreakerVersion,
    "uk.gov.hmrc" %% "emailaddress" % emailAdressVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "info.cukes" %% "cucumber-scala" % cucumberVersion % scope,
        "info.cukes" % "cucumber-junit" % cucumberVersion % scope,
        "com.github.tomakehurst" % "wiremock" % wireMockVersion % scope,
        "org.mockito" % "mockito-core" % "2.11.0" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

