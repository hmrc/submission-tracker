import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrapPlayVersion     = "5.1.0"
  private val domainVersion            = "5.11.0-play-27"
  private val playHmrcApiVersion       = "6.2.0-play-27"
  private val wireMockVersion          = "2.21.0"
  private val emailAdressVersion       = "3.4.0"
  private val scalamockVersion         = "4.1.0"
  private val mockitoVersion           = "3.2.4"
  private val scalatestplusPlayVersion = "4.0.3"
  private val refinedVersion           = "0.9.4"
  private val playJsonJodaVersion      = "2.6.14"
  private val pegdownVersion           = "1.6.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-27" % bootstrapPlayVersion,
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
          "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplusPlayVersion % scope,
          "org.scalamock"          %% "scalamock"          % scalamockVersion         % scope,
          "org.pegdown"            % "pegdown"             % pegdownVersion           % scope
        )
      }.test
  }

  object IntegrationTest {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val scope: String = "it"

        override lazy val test = Seq(
          "com.typesafe.play"      %% "play-test"          % PlayVersion.current      % scope,
          "com.github.tomakehurst" % "wiremock"            % wireMockVersion          % scope,
          "org.mockito"            % "mockito-core"        % mockitoVersion           % scope,
          "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplusPlayVersion % scope
        )
      }.test

    // Transitive dependencies in scalatest/scalatestplusplay drag in a newer version of jetty that is not
    // compatible with wiremock, so we need to pin the jetty stuff to the older version.
    // see https://groups.google.com/forum/#!topic/play-framework/HAIM1ukUCnI
    val jettyVersion = "9.2.13.v20150730"

    def overrides(): Seq[ModuleID] = Seq(
      "org.eclipse.jetty"           % "jetty-server"       % jettyVersion,
      "org.eclipse.jetty"           % "jetty-servlet"      % jettyVersion,
      "org.eclipse.jetty"           % "jetty-security"     % jettyVersion,
      "org.eclipse.jetty"           % "jetty-servlets"     % jettyVersion,
      "org.eclipse.jetty"           % "jetty-continuation" % jettyVersion,
      "org.eclipse.jetty"           % "jetty-webapp"       % jettyVersion,
      "org.eclipse.jetty"           % "jetty-xml"          % jettyVersion,
      "org.eclipse.jetty"           % "jetty-client"       % jettyVersion,
      "org.eclipse.jetty"           % "jetty-http"         % jettyVersion,
      "org.eclipse.jetty"           % "jetty-io"           % jettyVersion,
      "org.eclipse.jetty"           % "jetty-util"         % jettyVersion,
      "org.eclipse.jetty.websocket" % "websocket-api"      % jettyVersion,
      "org.eclipse.jetty.websocket" % "websocket-common"   % jettyVersion,
      "org.eclipse.jetty.websocket" % "websocket-client"   % jettyVersion
    )
  }

  def apply():   Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
  val overrides: Seq[ModuleID] = IntegrationTest.overrides()
}
