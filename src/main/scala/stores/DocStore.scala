package implicitly.stores

import implicitly.models.Doc
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
  def withUrls[T](f: Iterable[String] => T) = withManager { m =>
    import scala.collection.JavaConversions._
    f(m.newQuery("select url from " + domainCls.getName).execute().asInstanceOf[java.util.List[String]])
  }
}
