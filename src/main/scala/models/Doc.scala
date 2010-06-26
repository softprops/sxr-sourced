package implicitly.models

import javax.jdo.annotations._
import com.google.appengine.api.datastore.Blob

@PersistenceCapable( identityType = IdentityType.APPLICATION, detachable="true" )
class Doc() {
  @PrimaryKey
  @Persistent( valueStrategy = IdGeneratorStrategy.IDENTITY )
  var url: String = _

  @Persistent
  var contentType: String = _

  @Persistent
  var doc: Blob = _
  
  @Persistent
  var createdAt: java.util.Date = new java.util.Date()
}
