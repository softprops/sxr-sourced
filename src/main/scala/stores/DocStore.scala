package implicitly.stores

import implicitly.models.Doc
import com.google.appengine.api.datastore.Blob
import javax.jdo.annotations._

object DocStore extends jdo.JdoStore[Doc] with jdo.DefaultManager {
  override val domainCls = classOf[Doc]
  type KeyClass = String
  def apply(key: String) = get(key)
  
  @Deprecated
  def + (url: String, contentType: String, content: Array[Byte]) = {
    val d = new Doc
    d.url = url
    d.contentType = contentType
    d.doc = new Blob(content)
    save(d)
  }

  def + (url: String, contentType: String, blobKey: String) = {
    val d = new Doc
    d.url = url
    d.contentType = contentType
    d.blobKey = blobKey
    save(d)
  }



  @Transactional
  def withUrls[T](contentType: String)(f: Iterable[String] => T) = 
      f(query("select url from " + domainCls.getName) { q =>
      import scala.collection.JavaConversions._
        q.setFilter("contentType == contentTypeP")
        q.declareParameters("String contentTypeP")
        val l = q.execute(contentType).asInstanceOf[java.util.List[String]]
        l.size // important: read l to ensure objects are loaded before pm closes
        l
      })

  @Transactional
  def recent[T](contentType: String)(f: Iterable[Array[java.lang.Object]] => T) = 
      f(query("select url, createdAt from " + domainCls.getName) { q =>
      import scala.collection.JavaConversions._
        q.setFilter("contentType == contentTypeP")
        q.declareParameters("String contentTypeP")
        q.setOrdering("createdAt desc")
        val l = q.execute(contentType).asInstanceOf[java.util.List[Array[java.lang.Object]]]
        l.size // important: read l to ensure objects are loaded before pm closes
        l
      })
}
