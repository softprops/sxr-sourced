package implicitly

trait IO {
  /** `ARM` for closables */
  def use[A <: { def close(): Unit }, B](closable: A)(f: A => B): B =
    try { f(closable) } finally { closable.close() }
  
  /** reads input fully then returns bytes */
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
    
  /** streams bytes as they are read from input in chunks of chunkSize */
  def streamedBytesFrom[T](input: java.io.InputStream, chunkSize: Int)(f: (Array[Byte] => T)) =
    use(input) { in =>
      use(new java.io.ByteArrayOutputStream) { out =>
        val buffer = new Array[Byte](chunkSize)
        def stm: Stream[Int] = Stream.cons(in.read(buffer), stm)
        stm.takeWhile(_ != -1)
           .foreach { n => f(buffer.slice(0, n)) }
      }
    }
}