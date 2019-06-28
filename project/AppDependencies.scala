import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrapPlayVersion     = "0.35.0"
  private val domainVersion            = "5.6.0-play-26"
  private val playHmrcApiVersion       = "3.4.0-play-26"
  private val wireMockVersion          = "2.20.0"
  private val emailAdressVersion       = "2.2.0"
  private val scalamockVersion         = "4.1.0"
  private val mockitoVersion           = "2.20.0"
  private val scalatestplusPlayVersion = "3.1.2"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % bootstrapPlayVersion,
    "uk.gov.hmrc" %% "play-hmrc-api"     % playHmrcApiVersion,
    "uk.gov.hmrc" %% "domain"            % domainVersion,
    "uk.gov.hmrc" %% "emailaddress"      % emailAdressVersion
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
          "org.pegdown"            % "pegdown"             % "1.6.0"                  % scope
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
    def overrides(): Set[ModuleID] = Set(
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
  val overrides: Set[ModuleID] = IntegrationTest.overrides
}
