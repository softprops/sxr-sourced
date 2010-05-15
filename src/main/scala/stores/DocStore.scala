package implicitly.stores

import models.Doc
import com.google.appengine.api.datastore.{Text => BigString}

object DocStore extends jdo.JdoStore[Doc] {
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