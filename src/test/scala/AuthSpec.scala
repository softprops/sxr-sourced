package implicitly

import org.specs._
 
object AuthSpec extends Specification with Auth {
  "Auth" should {
    "sign a request" in {
      val secret = "09vy9uOblAm/XNpJXWqtCuOmPnog93P2UsuVPEsx"
      val path = "http://localhost:8080/us.technically.spde/spde-core/0.3.1-SNAPSHOT/linked.js"
      val content = scala.io.Source.fromFile(
        new java.io.File("src/test/resources/linked.js")
      ).getLines.mkString("").getBytes
      val sig = java.net.URLDecoder.decode("Trd0QZEjE60a%2FcQUI2a5575JY2I%3D")
      sign(secret, path, content) must_== sig
    }
  }
}