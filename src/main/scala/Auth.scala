package implicitly

trait Auth {
  import org.apache.commons.codec.binary.Base64.encodeBase64
  import javax.crypto
   
  val SHA1 = "HmacSHA1"
  
  def authorize(sig: String, orgId: String, path: String, content: String) =
    OrgStore(orgId) match { 
      case Some(token) => sig == sign(token, path, content)
      case _ => false
    }
  
  def sign(token: Token, path: String, content: String) = {
    val secret = token.secret
    val key = new crypto.spec.SecretKeySpec(bytes(secret), SHA1)
    val msg = (path :: content :: Nil) mkString ""
    val sig = {
      val mac = crypto.Mac.getInstance(SHA1)
      mac.init(key)
      new String(encodeBase64(mac.doFinal(bytes(msg))))
    }
    sig
  }
  
  private def bytes(str: String) = str.getBytes("UTF-8")
}