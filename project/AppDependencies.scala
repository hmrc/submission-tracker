import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrapPlayVersion     = "5.24.0"
  private val domainVersion            = "8.1.0-play-28"
  private val playHmrcApiVersion       = "7.0.0-play-28"
  private val wireMockVersion          = "2.21.0"
  private val emailAdressVersion       = "3.6.0"
  private val scalamockVersion         = "4.1.0"
  private val mockitoVersion           = "3.2.4"
  private val scalatestplusPlayVersion = "4.0.3"
  private val refinedVersion           = "0.9.4"
  private val playJsonJodaVersion      = "2.6.14"
  private val pegdownVersion           = "1.6.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-hmrc-api"             % playHmrcApiVersion,
    "uk.gov.hmrc"       %% "domain"                    % domainVersion,
    "uk.gov.hmrc"       %% "emailaddress"              % emailAdressVersion,
    "eu.timepit"        %% "refined"                   % refinedVersion,
    "com.typesafe.play" %% "play-json-joda"            % playJsonJodaVersion
  )

  trait TestDependencies {
    lazy val scope: String        = "test"
    lazy val test:  Seq[ModuleID] = ???
  }

  object Test {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val test = Seq(
          "org.scalatestplus.play" %% "scalatestplus-play"     % scalatestplusPlayVersion % scope,
          "org.scalamock"          %% "scalamock"              % scalamockVersion         % scope,
          "org.pegdown"            % "pegdown"                 % pegdownVersion           % scope,
          "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlayVersion     % scope
        )
      }.test
  }

  object IntegrationTest {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val scope: String = "it"

        override lazy val test = Seq(
          "com.typesafe.play"      %% "play-test"              % PlayVersion.current      % scope,
          "com.github.tomakehurst" % "wiremock"                % wireMockVersion          % scope,
          "org.mockito"            % "mockito-core"            % mockitoVersion           % scope,
          "org.scalatestplus.play" %% "scalatestplus-play"     % scalatestplusPlayVersion % scope,
          "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlayVersion     % scope
        )
      }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
