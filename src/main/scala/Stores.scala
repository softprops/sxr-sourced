package implicitly
 
import implicitly.jdo._
import javax.jdo.annotations._

@PersistenceCapable{val identityType = IdentityType.APPLICATION, val detachable="true"}
class Token() {
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  var org : String = _

  @Persistent
  var secret : String = ""
}

@PersistenceCapable{val identityType = IdentityType.APPLICATION, val detachable="true"}
class Doc() {
  @PrimaryKey
  @Persistent{val valueStrategy = IdGeneratorStrategy.IDENTITY}
  var url : String = _

  @Persistent
  var doc : String = ""
}

object OrgStore extends JdoStore[Token] {
  override val domainCls = classOf[Token]
  type KeyClass = String
  def apply(key:String) = get(key)
  def + (kv: (String, Token)) = {
    val t = new Token
    t.org = kv._1
    t.secret = kv._2.secret
    save(t)
  }
}

object SrcStore extends JdoStore[Doc] {
  override val domainCls = classOf[Doc]
  type KeyClass = String
  def apply(key:String) = get(key)
  def + (kv: (String, String)) = {
    val d = new Doc
    d.url = kv._1
    d.doc = kv._2
    save(d)
  }
}
