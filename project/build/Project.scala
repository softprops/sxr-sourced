import sbt._

class Project(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {
  //val snapshots = ScalaToolsSnapshots  
  val snapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  val appengineRepo = "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
  
  // unfiltered
  val uf = "net.databinder" %% "unfiltered" % "0.1.4-SNAPSHOT"

  // security
  val codec = "commons-codec" % "commons-codec" % "1.4"
   
  // persistence
  val jdo = "javax.jdo" % "jdo2-api" % "2.3-ea"
}
