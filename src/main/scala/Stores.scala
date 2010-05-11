package implicitly
 
import javax.jdo.annotations._
import com.google.appengine.api.datastore.{Text => BigString}

@PersistenceCapable{val identityType = IdentityType.APPLICATION, val detachable="true"}
class Token() {
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  var org: String = _

  @Persistent
  var secret: String = ""
  
  @Persistent
  var createdAt: java.util.Date = new java.util.Date()
}

@PersistenceCapable{val identityType = IdentityType.APPLICATION, val detachable="true"}
class Doc() {
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  var url: String = _

  @Persistent
  var doc: BigString = new BigString("")
  
  @Persistent
   var createdAt: java.util.Date = new java.util.Date()
}

object OrgStore extends JdoStore[Token] {
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

object SrcStore extends JdoStore[Doc] {
  override val domainCls = classOf[Doc]
  type KeyClass = String
  def apply(key: String) = get(key)
  def + (kv: (String, String)) = {
    val d = new Doc
    d.url = kv._1
    d.doc = new BigString(kv._2)
    save(d)
  }
}
