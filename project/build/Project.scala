import sbt._

class Project(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {
  lazy val specs = specsDependency
  def specsDependency =
    if (buildScalaVersion startsWith "2.7.")
      "org.scala-tools.testing" % "specs" % "1.6.2.2"
    else
      "org.scala-tools.testing" %% "specs" % "1.6.5"
  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  val appengineRepo = "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
  
  // unfiltered
  val uf = "net.databinder" %% "unfiltered" % "0.1.4"
  val serletApi = "javax.servlet" % "servlet-api" % "2.3" % "provided"
  // security
  val codec = "commons-codec" % "commons-codec" % "1.4"
   
  // persistence
  val jdo = "javax.jdo" % "jdo2-api" % "2.3-ea"
}
