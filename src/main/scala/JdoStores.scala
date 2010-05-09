package implicitly

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

import com.google.appengine.api.datastore.{Key, KeyFactory}

trait Managed {
  def manager: PersistenceManager
}

object ManagerFactory {
  lazy val get = JDOHelper.getPersistenceManagerFactory("transactions-optional")
}

trait DefaultManager extends Managed {
  private lazy val fact = ManagerFactory get
  def manager = fact getPersistenceManager
  def withManager[T](fn: PersistenceManager => T): T = {
    val pm = manager
    try {
      fn(pm)
    } finally {
      pm.close
    }
  }
}

trait Store[V] {
  val domainCls: Class[V]
  
  type KeyClass
  
  def save(v: V)
  
  def get(k: KeyClass): Option[V]
  
  def update(k: KeyClass, fn: Option[V] => Any)
  
  def delete(k: KeyClass)
}

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
}