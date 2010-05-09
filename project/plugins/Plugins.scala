import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  override def managedStyle = ManagedStyle.Maven
	val gae = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.1-SNAPSHOT"
}