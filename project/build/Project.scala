import sbt._

class Project(info: ProjectInfo) extends DefaultWebProject(info) {
  val snapshots = ScalaToolsSnapshots  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  val appengineRepo = "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
  
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"
  val lift = "net.liftweb" % "lift-webkit" % "2.0-M5"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val codec = "commons-codec" % "commons-codec" % "1.4"
  
  val gaeTools = "com.google.appengine" % "appengine-tools-api" % "1.3.3.1"
}
