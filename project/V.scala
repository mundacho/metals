import sbt._

object V {
  val scala210 = "2.10.7"
  val scala211 = "2.11.12"
  val scala212 = "2.12.16"
  val scala213 = "2.13.8"
  val scala3 = "3.2.0"
  val nextScala3RC = "3.2.1-RC1"
  val sbtScala = "2.12.14"
  val ammonite212Version = "2.12.16"
  val ammonite213Version = "2.13.8"
  val ammonite3Version = "3.1.3"

  val ammonite = "2.5.4-19-cd76521f"
  val betterMonadicFor = "0.3.1"
  val bloop = "1.5.3-28-373a64c9"
  val bloopNightly = bloop
  val bsp = "2.1.0-M1"
  val coursier = "2.1.0-M6"
  val coursierInterfaces = "1.0.8"
  val debugAdapter = "2.2.0"
  val genyVersion = "0.7.1"
  val gradleBloop = bloop
  val java8Compat = "1.0.2"
  val javaSemanticdb = "0.7.4"
  val jsoup = "1.15.3"
  val kindProjector = "0.13.2"
  val lsp4jV = "0.15.0"
  val mavenBloop = bloop
  val mill = "0.10.7"
  val mdoc = "2.3.3"
  val munit = "1.0.0-M6"
  val organizeImportRule = "0.6.0"
  val pprint = "0.7.3"
  val sbtBloop = bloop
  val sbtJdiTools = "1.1.1"
  val scalaCli = "0.1.12"
  val scalafix = "0.10.1"
  val scalafmt = "3.5.3"
  val scalameta = "4.5.13"
  val scribe = "3.10.3"
  val semanticdb = scalameta
  val qdox = "2.0.1"

  val guava = "com.google.guava" % "guava" % "31.1-jre"
  val lsp4j = "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % lsp4jV
  val dap4j = "org.eclipse.lsp4j" % "org.eclipse.lsp4j.debug" % lsp4jV

  def isNightliesEnabled: Boolean =
    sys.env.get("CI").isDefined || sys.env.get("NIGHTLIES").isDefined

  // List of supported Scala versions in SemanticDB. Needs to be manually updated
  // for every SemanticDB upgrade.
  def supportedScalaBinaryVersions =
    supportedScalaVersions.iterator
      .map(CrossVersion.partialVersion)
      .collect {
        case Some((3, _)) => "3"
        case Some((a, b)) => s"$a.$b"
      }
      .toList
      .distinct

  // Scala 2
  def deprecatedScala2Versions = Seq(
    scala211,
    "2.12.8",
    "2.12.9",
    "2.12.10",
    "2.13.1",
    "2.13.2",
    "2.13.3",
    "2.13.4",
  )

  def nonDeprecatedScala2Versions = Seq(
    scala213,
    scala212,
    "2.12.15",
    "2.12.14",
    "2.12.13",
    "2.12.12",
    "2.12.11",
    "2.13.5",
    "2.13.6",
    "2.13.7",
  )
  def scala2Versions = nonDeprecatedScala2Versions ++ deprecatedScala2Versions

  // The minimum sbt version that uses a non-deprecated Scala version.
  // Currently uses Scala 2.12.12 - update upon deprecation.
  def minimumSupportedSbtVersion = "1.4.0"

  // Scala 3
  def nonDeprecatedScala3Versions =
    Seq(nextScala3RC, scala3, "3.1.3", "3.1.2", "3.1.1")
  def deprecatedScala3Versions =
    Seq("3.2.0-RC4", "3.2.0-RC3", "3.1.0", "3.0.2", "3.0.1", "3.0.0")
  def scala3Versions = nonDeprecatedScala3Versions ++ deprecatedScala3Versions

  lazy val nightlyScala3DottyVersions = {
    if (isNightliesEnabled)
      Scala3NightlyVersions.nightlyReleasesAfter(scala3)
    else
      Nil
  }

  def nightlyScala3Versions = nightlyScala3DottyVersions.map(_.toString)

  def supportedScalaVersions = scala2Versions ++ scala3Versions
  def nonDeprecatedScalaVersions =
    nonDeprecatedScala2Versions ++ nonDeprecatedScala3Versions
  def deprecatedScalaVersions =
    deprecatedScala2Versions ++ deprecatedScala3Versions

  val quickPublishScalaVersions =
    Set(
      scala211,
      sbtScala,
      scala212,
      ammonite212Version,
      scala213,
      ammonite213Version,
      scala3,
      ammonite3Version,
    ).toList
}
