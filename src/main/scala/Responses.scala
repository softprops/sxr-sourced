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
  case class SrcCreatedResponse(loc: String, contentType: String) extends LiftResponse {
    def toResponse = InMemoryResponse(Array(), ("Location" -> loc) :: ("Content-Type" -> contentType) :: Nil, Nil, 201)
  }
  
  def adminPage(req: Req)(out: => scala.xml.Node) = {
    val svc = com.google.appengine.api.users.UserServiceFactory.getUserService
    if (svc.isUserLoggedIn && svc.isUserAdmin)
      XhtmlResponse(adminTemplate(out, svc.createLogoutURL(url(req))), net.liftweb.common.Empty, List("Content-Type" -> "text/html; charset=utf-8"), Nil, 200, false)
    else RedirectResponse(svc.createLoginURL(url(req)))
  }
  
  def adminTemplate(content: => scala.xml.Node, outUrl: String) = <html>
    <head><title>implicit.ly sourced</title><link rel="stylesheet" type="text/css" href="/css/implicitly.css"/></head>
    <body><div id="header"><div id="auth"><a href={outUrl}>Logout</a></div></div><div id="container">{content}</div></body>
  </html>
}