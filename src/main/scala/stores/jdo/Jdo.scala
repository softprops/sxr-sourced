package implicitly.stores.jdo

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