package implicitly

import stores.OrgStore
import models.Token

trait Auth extends IO {
  import org.apache.commons.codec.binary.Base64.encodeBase64
  import javax.crypto
  import java.io.{InputStream => JIO}
   
  lazy val random = new scala.util.Random
  
  def authorize(sig: String, orgId: String, path: String, content: JIO) =
    OrgStore(orgId) match { 
      case Some(token) => sig == sign(token.secret, path, content)
      case _ => {
        println("unknown org %s " format orgId) // we prob want to log this
        false
      }
    }
  
  def sign(secret: String, path: String, content: JIO) = {
    implicit def str2bytes(str: String) = str.getBytes("utf8")
    val SHA1 = "HmacSHA1"
    val key = new crypto.spec.SecretKeySpec(secret, SHA1)
    val mac = crypto.Mac.getInstance(SHA1)
    mac.init(key)
    mac.update(path)
    streamedBytesFrom(content, 1024) { mac.update }
    new String(encodeBase64(mac.doFinal))
  }
  
  def generateKey = {
    val ary = new Array[Byte](30)
    random.nextBytes(ary)
    new String(encodeBase64(ary, false))
  }
}