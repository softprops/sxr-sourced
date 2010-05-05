import sbt._

class Project(info: ProjectInfo) extends DefaultWebProject(info) {
  val snapshots = ScalaToolsSnapshots  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  // ensure that all new lift dependencies are fetched
  val nexusRepo = "nexus" at "https://nexus.griddynamics.net/nexus/content/groups/public"
  
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"
  val lift = "net.liftweb" % "lift-webkit" % "2.0-SNAPSHOT" % "compile"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  val codec = "commons-codec" % "commons-codec" % "1.4"
  
  val gaeTools = "com.google.appengine" % "appengine-tools-sdk" % "1.3.3.1"
  val gae = "com.google.appengine" % "appengine" % "1.3.3.1"
}