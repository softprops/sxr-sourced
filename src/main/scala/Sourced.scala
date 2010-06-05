package implicitly

import unfiltered.request._
import unfiltered.response._

/** Sourced - serving scala for the _good_ of mankind **/
class Sourced extends Responses with Urls with Requests with Auth with unfiltered.Plan {
  import scala.io.Source.{ fromBytes => <<< }
  import stores.{DocStore, OrgStore}
  import javax.servlet.http.{HttpServletRequest => Req}
  
  def filter = {
    
    case PUT(Path(Seg(org :: project :: version :: srcName :: _), Params(params, req))) => 
      params("sig") match {
        case Seq(sig) => req match {
          case Bytes(body, _) =>
            val uri = url(req)
            authorize(sig, org, uri, body) match {
              case true => {
                val src = <<<(body).mkString
                DocStore + (uri -> src)
                Status(201) ~> ContentType(contentType(uri)) ~> ResponseString(src)
              }
              case _ => Status(401)
            }
          case _ => Status(400) ~> ResponseString("request body required")
        }
        case _ => Status(400) ~> ResponseString("sig required")
      }
  
      case GET(Path(Seg(org :: project :: version :: srcName :: _), req)) =>
        DocStore(url(req)) match {
          case Some(src) => Status(200) ~> ContentType(contentType(src.url)) ~> ResponseString(src.doc.getValue)
          case _ => NotFound
        }
    
      case GET(Path(Seg("admin" ::  Nil), req)) => adminPage(req) {
          <form action="setkey" method="post">
            <input type="text" name="orgId" />
            <input type="submit" value="Generate Token" />
          </form>
        }
  
      case POST(Path(Seg("setkey" :: Nil), Params(params,req))) =>
        params("orgId") match {
          case Seq(orgId) => {
            val key = generateKey
            OrgStore + (orgId, key)
            adminPage(req) {
              <div> { key } </div>
            }
          }
          case _ => Status(400) ~> ResponseString("orgId required")
        }
  }
}

object SourcedServer {
  def main(args: Array[String]) {
    unfiltered.server.Http(8080).filter(new Sourced).start
  }
}
