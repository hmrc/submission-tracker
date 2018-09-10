import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrapPlayVersion = "1.7.0"
  private val authClientVersion = "2.6.0"
  private val domainVersion = "5.2.0"
  private val playHmrcApiVersion = "3.2.0"
  private val hmrcTestVersion = "3.0.0"
  private val wireMockVersion = "2.9.0"
  private val reactiveCircuitBreakerVersion = "3.2.0"
  private val emailAdressVersion = "2.2.0"
  private val scalamockVersion = "4.0.0"
  private val mockitoVersion = "2.11.0"
  private val scalatestplusPlayVersion = "2.0.1"

  val compile = Seq(

    ws,
    "uk.gov.hmrc" %% "bootstrap-play-25" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "auth-client" % authClientVersion,
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
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalamock" %% "scalamock" % scalamockVersion % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply(): Seq[ModuleID] = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "com.github.tomakehurst" % "wiremock" % wireMockVersion % scope,
        "org.mockito" % "mockito-core" % mockitoVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplusPlayVersion % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}

