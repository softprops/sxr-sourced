package implicitly

trait IO {
  def use[A <: { def close(): Unit }, B](closable: A)(f: A => B): B =
    try { f(closable) } finally { closable.close() }
  
  def bytesFrom[T](input: java.io.InputStream)(f: (Array[Byte] => T)) =
    use(input) { in =>
      use(new java.io.ByteArrayOutputStream) { out =>
        val buffer = new Array[Byte](1024)
        def stm: Stream[Int] = Stream.cons(in.read(buffer), stm)
        stm.takeWhile(_ != -1)
           .foreach { out.write(buffer, 0 , _) }
        f(out.toByteArray)
      }
    }
}