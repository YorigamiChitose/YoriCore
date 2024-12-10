import mill._
import mill.define.Sources
import mill.modules.Util
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.TestModule.ScalaTest
import mill.scalalib._

object playground extends SbtModule with ScalafmtModule { m =>
  override def millSourcePath = os.pwd / "src"
  override def scalaVersion   = "2.13.14"
  override def scalacOptions  = Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit"
  )

  override def sources = T.sources {
    super.sources() ++ Seq(PathRef(millSourcePath / "main"))
  }

  override def resources = T.sources {
    super.resources() ++ Seq(PathRef(millSourcePath / "main" / "scala" / "resources"))
  }

  override def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:6.5.0"
  )

  override def scalacPluginIvyDeps = Agg(
    ivy"org.chipsalliance:::chisel-plugin:6.5.0"
  )

  object test extends SbtTests with TestModule.ScalaTest with ScalafmtModule {
    override def sources = T.sources {
      super.sources() ++ Seq(PathRef(this.millSourcePath / "test"))
    }
    override def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"edu.berkeley.cs::chiseltest:6.0.0"
    )
  }

  def repositoriesTask = T.task {
    Seq(
      coursier.MavenRepository("https://repo.scala-sbt.org/scalasbt/maven-releases"),
      coursier.MavenRepository("https://oss.sonatype.org/content/repositories/releases"),
      coursier.MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
    ) ++ super.repositoriesTask()
  }
}
