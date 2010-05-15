package implicitly.stores

/** Interface for a KeyClass -> V store */
trait Store[V] {
  val domainCls: Class[V]
  
  type KeyClass
  
  def save(v: V)

  def get(k: KeyClass): Option[V]
  
  def update(k: KeyClass, fn: Option[V] => Any)
  
  def delete(k: KeyClass)
}
