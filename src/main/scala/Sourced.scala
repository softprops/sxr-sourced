package implicitly

import unfiltered.request._
import unfiltered.response._

/** Sourced - serving scala for the _good_ of mankind **/
class Sourced extends Responses with Urls with Requests with Auth with IO with unfiltered.Plan {
  import scala.io.Source.{ fromBytes => <<< }
  import stores.{DocStore, OrgStore}
  import javax.servlet.http.{HttpServletRequest => Req}
  
  def filter = {
    
    case PUT(Path(Seg(org :: project :: version :: srcName :: _), req)) => 
      req.getParameter("sig") match {
        case null => Status(400) ~> ResponseString("sig required")
        case sig => {
          bytes(req.getInputStream) match {
            case arr: Array[Byte] if arr.isEmpty => Status(400) ~> ResponseString("request body required")
            case body => {
              val uri = url(req)
              authorize(
                java.net.URLDecoder.decode(sig), org, uri, body
              ) match {
                case true => {
                  val src = <<<(body).mkString
                  DocStore + (uri -> src)
                  Status(201) ~> ContentType(contentType(uri)) ~> ResponseString(src)
                }
                case _ => Status(401)
              }
            }
          }
        }
      }
  
      case GET(Path(Seg(org :: project :: version :: srcName :: _), req)) =>
        DocStore(url(req)) match {
          case Some(src) => Status(200) ~> ResponseString(src.doc.getValue) ~> ContentType(contentType(src.url))
          case _ => NotFound
        }
    
      case GET(Path(Seg("admin" ::  Nil), req)) => adminPage(req) {
          <form action="setkey" method="post">
            <input type="text" name="orgId" />
            <input type="submit" value="Generate Token" />
          </form>
        }
  
      case POST(Path(Seg("setkey" :: Nil), req)) =>
        req.getParameter("orgId") match {
          case null => Status(400) ~> ResponseString("orgId required")
          case orgId => {
            val key = generateKey
            OrgStore + (orgId, key)
            adminPage(req) {
              <div> { key } </div>
            }
          }
        }
  }
}

object SourcedServer {
  def main(args: Array[String]) {
    unfiltered.server.Http(8080).filter(new Sourced).start
  }
}