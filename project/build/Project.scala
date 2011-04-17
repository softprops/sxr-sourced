import sbt._

class Project(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {
  lazy val specs = specsDependency
  def specsDependency =
      "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5"
  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  val appengineRepo = "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
  
  // unfiltered
  val uf = "net.databinder" %% "unfiltered-filter" % "0.3.2"
  val serletApi = "javax.servlet" % "servlet-api" % "2.3" % "provided"

  // security
  val codec = "commons-codec" % "commons-codec" % "1.4"
   
  // persistence
  val jdo = "javax.jdo" % "jdo2-api" % "2.3-eb"
}
