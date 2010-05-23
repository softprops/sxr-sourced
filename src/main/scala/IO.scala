package implicitly

trait IO {
  def bytes(in: java.io.InputStream) = {
    val bos = new java.io.ByteArrayOutputStream
    val ba = new Array[Byte](4096)
    def read {
      val len = in.read(ba)
      if (len > 0) bos.write(ba, 0, len)
      if (len >= 0) read
    }
    read
    in.close
    bos.toByteArray
  }
}