package implicitly

import net.liftweb.http.rest._

/** Provides S.*-like helpers to `stateless` dispatchers */
trait UrlHelpers { self: RequestHelpers =>
  import net.liftweb.http.Req
  
  def hostAndPath(req:Req) =
    containerRequest(req).map(r => (r.scheme, r.serverPort) match {
      case ("http", 80) => "http://" + r.serverName + req.contextPath
      case ("https", 443) => "https://" + r.serverName + req.contextPath
      case (sch, port) => sch + "://" + r.serverName + ":" + port + req.contextPath
    }) openOr ""

  def url(req: Req) = (hostAndPath(req) :: req.path.wholePath).mkString("/")
}