package implicitly.models
 
import javax.jdo.annotations._

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