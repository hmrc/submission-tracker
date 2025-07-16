import play.sbt.PlayImport.PlayKeys.playDefaultPort

val appName: String = "submission-tracker"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    Seq(play.sbt.PlayScala, SbtDistributablesPlugin, ScoverageSbtPlugin): _*
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.domain._",
      "uk.gov.hmrc.submissiontracker.binder.Binders._",
      "uk.gov.hmrc.submissiontracker.domain.types._",
      "uk.gov.hmrc.submissiontracker.domain.types.JourneyId._",
      "uk.gov.hmrc.submissiontracker.domain.types.IdType._"

)
  )
  .settings(
    majorVersion := 1,
    scalaVersion := "3.6.4",
    playDefaultPort := 8232,
    libraryDependencies ++= AppDependencies(),
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    coverageMinimumStmtTotal := 82,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageExcludedPackages := "<empty>;.*Routes.*;app.*;.*prod;.*definition;.*testOnlyDoNotUseInAppConf;.*com.kenshoo.*;.*javascript.*;.*BuildInfo;.*Reverse.*;.*binder.*;package.scala"
  )
