package implicitly.stores.jdo

import implicitly.stores.Store

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager, Query}

import com.google.appengine.api.datastore.{Key, KeyFactory}

/** KeyClass key to V value */
trait JdoStore[V] extends Store[V] with DefaultManager {
  
  val domainCls: Class[V]
  
  type KeyClass
  
  def save(v: V) = withManager { m =>
    m.makePersistent(v)
  }
  
  def get(k: KeyClass) = withManager { m => 
    try {
      m.getObjectById(domainCls, k) match {
        case null => None
        case v => Some(v.asInstanceOf[V])
      }
    } catch {
      case e: javax.jdo.JDOObjectNotFoundException => None
    }
  }
  
  def update(k: KeyClass, fn: Option[V] => Any) = withManager { m =>
    fn(get(k))
  }
  
  def delete(k: KeyClass) = withManager { m =>
    get(k) match {
      case Some(v) => m.deletePersistent(v)
      case _ => ()
    } 
  }
  
  protected def query[T](q: String)(f: Query => T) = withManager { m =>
    f(m.newQuery(q)) 
  }
}