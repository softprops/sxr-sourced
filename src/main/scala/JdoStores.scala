package implicitly

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

import com.google.appengine.api.datastore.{Key, KeyFactory}

trait Managed {
  def manager: PersistenceManager
}

trait DefaultManager extends Managed {
  private lazy val fact = JDOHelper.getPersistenceManagerFactory("transactions-optional")
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
  
  def update(k: KeyClass, fn: V => Any)
  
  def delete(k: KeyClass)
}

/** String key to V value */
trait JdoStore[V] extends Store[V] with DefaultManager {
  
  val domainCls: Class[V]
  
  type KeyClass
  
  def save(v: V) = withManager { m => 
    m.makePersistent(v)
  }
  
  def get(k: KeyClass) = withManager { m =>
    m.getObjectById(domainCls.getClass, k) match {
      case null => None
      case v => Some(v.asInstanceOf[V])
    }
  }
  
  def update(k: KeyClass, fn: V => Any) = withManager { m =>
    val v = m.getObjectById(domainCls.getClass, k).asInstanceOf[V]
    fn(v)
  }
  
  def delete(k: KeyClass) = withManager { m =>
    get(k) match {
      case Some(v) => m.deletePersistent(v)
      case _ => ()
    } 
  }
}