import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Tests.{Group, SubProcess}

val appName: String = "submission-tracker"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, ScoverageSbtPlugin): _*
  )
  .disablePlugins(JUnitXmlReportPlugin)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.domain._",
      "uk.gov.hmrc.submissiontracker.binder.Binders._",
      "uk.gov.hmrc.submissiontracker.domain.types._",
      "uk.gov.hmrc.submissiontracker.domain.types.ModelTypes._"
    )
  )
  .settings(
    majorVersion := 1,
    scalaVersion := "2.12.15",
    playDefaultPort := 8232,
    libraryDependencies ++= AppDependencies(),
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers += Resolver.jcenterRepo,
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    IntegrationTest / testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    coverageMinimumStmtTotal := 90,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageExcludedPackages := "<empty>;.*Routes.*;app.*;.*prod;.*definition;.*testOnlyDoNotUseInAppConf;.*com.kenshoo.*;.*javascript.*;.*BuildInfo;.*Reverse.*;.*binder.*"
  )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
  tests map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))))
  }
