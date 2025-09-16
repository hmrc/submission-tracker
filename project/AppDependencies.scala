import sbt._

object AppDependencies {
  import play.sbt.PlayImport._

  private val bootstrapPlayVersion = "10.1.0"
  private val domainVersion        = "13.0.0"
  private val playHmrcApiVersion   = "8.3.0"
  private val scalamockVersion     = "7.5.0"
  private val refinedVersion       = "0.11.3"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "play-hmrc-api-play-30"     % playHmrcApiVersion,
    "uk.gov.hmrc" %% "domain-play-30"            % domainVersion,
    "eu.timepit"  %% "refined"                   % refinedVersion
  )

  trait TestDependencies {
    lazy val scope: String        = "test"
    lazy val test:  Seq[ModuleID] = ???
  }

  object Test {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val test = Seq(
          "org.scalamock" %% "scalamock"              % scalamockVersion     % scope,
          "uk.gov.hmrc"   %% "bootstrap-test-play-30" % bootstrapPlayVersion % scope
        )
      }.test
  }

  object IntegrationTest {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val scope: String = "it"

        override lazy val test = Seq(
          "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlayVersion % scope
        )
      }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
