package implicitly

//import net.liftweb.http.rest._

/** Provides S.*-like helpers to `stateless` dispatchers */
trait Urls { self: Requests =>
  import javax.servlet.http.{HttpServletRequest => Req}
     
  def hostUrl(r: Req) = host(r)

  def url(r: Req) = hostAndPath(r)
  
  private def host(r: Req) =
      (r.getScheme, r.getServerPort) match {
       case ("http", 80) => "http://" + r.getServerName
       case ("https", 443) => "https://" + r.getServerName
       case (sch, port) => sch + "://" + r.getServerName + ":" + port
     }
     
  private def hostAndPath(r: Req) =
      (r.getScheme, r.getServerPort) match {
       case ("http", 80) => "http://" + r.getServerName + r.getServletPath
       case ("https", 443) => "https://" + r.getServerName + r.getServletPath
       case (sch, port) => sch + "://" + r.getServerName + ":" + port + r.getServletPath
     }
}