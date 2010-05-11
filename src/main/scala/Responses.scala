package implicitly

/** Custom Response types */
trait Responses { this: UrlHelpers =>
  import net.liftweb.http._
  import net.liftweb.http.provider.servlet.HTTPRequestServlet
  
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
  def adminPage(req: Req)(out: => scala.xml.Node) = {
    val svc = com.google.appengine.api.users.UserServiceFactory.getUserService
    if (svc.isUserLoggedIn && svc.isUserAdmin)
      XhtmlResponse(out, net.liftweb.common.Empty, List("Content-Type" -> "text/html; charset=utf-8"), Nil, 200, false)
    else RedirectResponse(svc.createLoginURL(url(req)))
  }
}