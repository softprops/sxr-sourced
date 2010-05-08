import sbt._

class Project(info: ProjectInfo) extends DefaultWebProject(info) {
  //val snapshots = ScalaToolsSnapshots  
  val snapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  
  // testing
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1-SNAPSHOT" % "test"
  val jettytester = "org.mortbay.jetty" % "jetty-servlet-tester" % "7.0.0pre3" % "test->default"
  
  // required because Ivy doesn't pull repositories from poms
  val smackRepo = "m2-repository-smack" at "http://maven.reucon.com/public"
  val appengineRepo = "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
  
  // web
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"
  val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
  
  // lift
  val lift = "net.liftweb" % "lift-webkit" % "2.0-M5"
  
  // security
  val codec = "commons-codec" % "commons-codec" % "1.4"
  
  // gae
  val gae = "com.google.appengine" % "appengine" % "1.3.0"
  val gaeTools = "com.google.appengine" % "appengine-tools-api" % "1.3.3.1"
  val gaeApi = "com.google.appengine" % "appengine-api-1.0-sdk" % "1.3.3.1"
  val gaeApiStubs = "com.google.appengine" % "appengine-api-stubs" % "1.3.3.1"
  
  // blobs store
  val gaeApiLabs = "com.google.appengine" % "appengine-api-labs" % "1.3.3.1"
  
  // persistence
  val jdo = "javax.jdo" % "jdo2-api" % "2.3-ea"
  val orm = "com.google.appengine.orm" % "datanucleus-appengine" % "1.0.6.final"
  val datanucleusCore = "com.google.appengine" % "datanucleus-core" % "1.1.5"
  val datanucleusJpa = "com.google.appengine" % "datanucleus-jpa" % "1.1.5"
}