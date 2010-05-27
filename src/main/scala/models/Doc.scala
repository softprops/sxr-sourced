package implicitly.models

import javax.jdo.annotations._
import com.google.appengine.api.datastore.{Text => BigString}

@PersistenceCapable{ val identityType = IdentityType.APPLICATION, val detachable="true" }
class Doc() {
  @PrimaryKey
  @Persistent{ val valueStrategy = IdGeneratorStrategy.IDENTITY }
  var url: String = _

  @Persistent
  var doc: BigString = new BigString("")
  
  @Persistent
  var createdAt: java.util.Date = new java.util.Date()
}