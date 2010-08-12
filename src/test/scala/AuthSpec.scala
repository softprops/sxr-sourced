package implicitly

import org.specs._
 
object AuthSpec extends Specification with Auth {
  "Auth" should {
    "sign a request" in {
      val secret = "09vy9uOblAm/XNpJXWqtCuOmPnog93P2UsuVPEsx"
      val path = "http://localhost:8080/us.technically.spde/spde-core/0.3.1-SNAPSHOT/linked.js"
      val sig = java.net.URLDecoder.decode("Trd0QZEjE60a%2FcQUI2a5575JY2I%3D", "utf8")
      sign(secret, path, new java.io.FileInputStream(new java.io.File("src/test/resources/linked.js"))) must_== sig   
    }
  }
}