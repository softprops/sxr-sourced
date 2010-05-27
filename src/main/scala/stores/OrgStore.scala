package implicitly.stores

import implicitly.models.Token

object OrgStore extends jdo.JdoStore[Token] {
  override val domainCls = classOf[Token]
  type KeyClass = String
  def apply(key: String) = get(key)
  def + (orgId: String, orgKey: String) = {
    val t = new Token
    t.org = orgId
    t.secret = orgKey
    save(t)
  }
}