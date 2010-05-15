package implicitly

import stores.OrgStore
import models.Token

trait Auth {
  import org.apache.commons.codec.binary.Base64.encodeBase64
  import javax.crypto
   
  val SHA1 = "HmacSHA1"
  lazy val random = new scala.util.Random
  
  def authorize(sig: String, orgId: String, path: String, content: Array[Byte]) =
    OrgStore(orgId) match { 
      case Some(token) => sig == sign(token, path, content)
      case _ => false
    }
  
  def sign(token: Token, path: String, content: Array[Byte]) = {
    implicit def str2bytes(str: String) = str.getBytes("utf8")
    val secret = token.secret
    val key = new crypto.spec.SecretKeySpec(secret, SHA1)
    val mac = crypto.Mac.getInstance(SHA1)
    mac.init(key)
    mac.update(path)
    new String(encodeBase64(mac.doFinal(content)))
  }
  
  def generateKey = {
    val ary = new Array[Byte](30)
    random.nextBytes(ary)
    new String(encodeBase64(ary, false))
  }
}