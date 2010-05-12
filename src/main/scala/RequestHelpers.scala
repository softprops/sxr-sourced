package implicitly

trait RequestHelpers {
  import net.liftweb.http.{Req, LiftResponse}
  import net.liftweb.http.provider.HTTPRequest
  import net.liftweb.http.provider.servlet.HTTPRequestServlet
  import net.liftweb.common.{Full, Box}
  
  val extContentTypes = Map(
    "js" -> "text/javascript; charset=utf-8", 
    "css" -> "text/css; charset=utf-8", 
    "html" -> "text/html; charset=utf-8"
  )
  
  def contentType(url: String) = extContentTypes.get(url.split("[.]").last) match {
    case Some(ct) => ct
    case _ => "text/html; charset=utf-8"
  }
  
  def containerRequest(r: Req): Box[HTTPRequest] =
       Full(r).flatMap(r => Box !! r.request)
}