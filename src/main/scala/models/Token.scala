package implicitly.models
 
import javax.jdo.annotations._

@PersistenceCapable( identityType = IdentityType.APPLICATION, detachable="true")
class Token() {
  @PrimaryKey
  @Persistent( valueStrategy = IdGeneratorStrategy.IDENTITY)
  var org: String = _

  @Persistent
  var secret: String = ""
  
  @Persistent
  var createdAt: java.util.Date = new java.util.Date()
}
