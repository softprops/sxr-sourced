import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val gae = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.2"
}
