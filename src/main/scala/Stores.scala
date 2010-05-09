package implicitly

case class Token(secret: String)

object OrgStore  {
  var cache = Map("us.technically.spde" -> Token("testing"))
  def apply(key: String) = cache.get(key)
  def + (kv: (String, Token)) = cache += kv
}

object SrcStore  {
  var cache = Map.empty[String, String]
  def apply(key: String) = cache.get(key)
  def + (kv: (String, String)) = cache += kv
}