package implicitly.stores

import implicitly.models.Doc
import com.google.appengine.api.datastore.Blob

object DocStore extends jdo.JdoStore[Doc] {
  override val domainCls = classOf[Doc]
  type KeyClass = String
  def apply(key: String) = get(key)
  def + (url: String, contentType: String, content: Array[Byte]) = {
    val d = new Doc
    d.url = url
    d.contentType = contentType
    d.doc = new Blob(content)
    save(d)
  }
  def withUrls[T](contentType: String)(f: Iterable[String] => T) = withManager { m =>
    import scala.collection.JavaConversions._
    f({
      val q = m.newQuery("select url from " + domainCls.getName)
      q.setFilter("contentType == contentTypeP")
      q.declareParameters("String contentTypeP")
      q.execute(contentType).asInstanceOf[java.util.List[String]]
    })
  }
}
