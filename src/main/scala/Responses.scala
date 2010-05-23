package implicitly

/** Custom Response types */
trait Responses { this: Urls =>
  import javax.servlet.http.{HttpServletRequest => Req}
  import unfiltered.response._
  
  def adminPage(req: Req)(out: => scala.xml.Node) = {
    val svc = com.google.appengine.api.users.UserServiceFactory.getUserService
    if (svc.isUserLoggedIn && svc.isUserAdmin)
      Html(
        adminTemplate(out, svc.createLogoutURL(url(req)))
      )
    else Redirect(svc.createLoginURL(url(req)))
  }
  
  def adminTemplate(content: => scala.xml.Node, outUrl: String) = <html>
    <head><title>implicit.ly sourced</title><link rel="stylesheet" type="text/css" href="/css/implicitly.css"/></head>
    <body><div id="header"><div id="auth"><a href={outUrl}>Logout</a></div></div><div id="container">{content}</div></body>
  </html>
}