package implicitly

import unfiltered.request._
import unfiltered.response._

/** Public Sourced `api` */
class Api extends Urls with Requests with unfiltered.Plan {
  import stores.DocStore
  import javax.servlet.http.{HttpServletRequest => Req}

  case class  Src(org: String, proj: String, vers: String, url: String, createdAt: java.util.Date) {
    def asJson = """{"org":"%s","project":"%s","version":"%s", "url":"%s", "createdAt":%s}""" format(
      org, proj, vers, url, "Date(%s)" format(createdAt.getTime))
  }
    
  def filter = {
    case GET(Path(Seg("api" :: "recent" :: Nil), Params(params, req))) =>
      val Index = ("^"+hostUrl(req).replace(".", "[.]")+"/(.+)/(.+)/(.+)/index\\.html$").r
      val js = DocStore.recent("text/html") { _.flatMap {
        case Array(Index(org, proj, vers), created: java.util.Date) => Some(Src(org, proj, vers, (hostUrl(req) :: org :: proj :: vers :: "index.html" :: Nil) mkString("/"), created) asJson)
        case _ => None
      } take(10) mkString("[", ",", "]") }
      
      ContentType("application/json") ~> ResponseString(
         params("callback") match {
            case Seq(cb) => "%s(%s)" format(cb, js)
            case _ => js
          }
      )
  }
}

/** Sourced - serving scala for the _good_ of mankind **/
class Sourced extends Responses with Urls with Requests with Auth with unfiltered.Plan {
  import stores.{DocStore, OrgStore}
  import javax.servlet.http.{HttpServletRequest => Req}
  
  def filter = {
    
    case PUT(Path(Seg(org :: project :: version :: srcName :: _), Params(params, RequestContentType(contentTypes, req)))) => 
      params("sig") match {
        case Seq(sig) => req match {
          case Bytes(body, _) =>
            val uri = url(req)
            val docContentType = contentTypes.headOption.getOrElse(contentType(uri))
            authorize(sig, org, uri, body) match {
              case true => {
                DocStore + (uri, docContentType, body)
                Status(201) ~> ContentType(docContentType) ~> ResponseBytes(body)
              }
              case _ => Status(401)
            }
          case _ => Status(400) ~> ResponseString("request body required")
        }
        case _ => Status(400) ~> ResponseString("sig required")
      }
  
      case GET(Path(Seg(org :: project :: version :: srcName :: _), req)) =>
        DocStore(url(req)) match {
          case Some(src) => 
            Status(200) ~> ContentType(src.contentType) ~> ResponseBytes(src.doc.getBytes)
            
          case _ => NotFound
        }
    
      case GET(Path(Seg("admin" ::  Nil), req)) => adminPage(req) {
          <form action="setkey" method="post">
            <input type="text" name="orgId" />
            <input type="submit" value="Generate Token" />
          </form>
        }
      
      case GET(Path("/sxr.links", _)) =>
        val LinkIndex = "^(.+)link\\.index\\.gz$".r
        ContentType("text/plain") ~> ResponseString(
          DocStore.withUrls("application/x-gzip") { _.flatMap {
            case LinkIndex(base) => Some(base)
            case _ => None
          } mkString "\n" }
        )
  
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
    unfiltered.server.Http(8080)
      .filterAt("/api/*")(new Api)
      .filter(new Sourced)
      .run
  }
}
