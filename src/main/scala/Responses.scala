package implicitly

/** Custom Response types */
trait Responses {
  import net.liftweb.http._
  
  /** A `looser` response for source documents */
  case class SrcResponse(src: String, contentType: String, headers: List[(String, String)], code: Int) extends LiftResponse {
    def toResponse = {
      val bytes = src.getBytes("UTF-8")
      InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) :: ("Content-Type", contentType) :: headers, Nil, code)
    }
  }
  /** Empty created response with location header */
  case class SrcCreatedResponse(loc: String, mime: String) extends LiftResponse {
    def toResponse = InMemoryResponse(Array(), ("Location" -> loc) :: ("Content-Type" -> mime) :: Nil, Nil, 201)
  }
  def adminResponse(out: scala.xml.Node) =
    XhtmlResponse(out, net.liftweb.common.Empty, List("Content-Type" -> "text/html; charset=utf-8"), Nil, 200, false)
}