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
      
      JsonContent ~> ResponseString(
         params("callback") match {
            case Seq(cb) => "%s(%s)" format(cb, js)
            case _ => js
          }
      )
  }
}

/** Sourced - serving scala for the _good_ of mankind */
class Sourced extends Responses with Urls with Requests with Auth with IO with unfiltered.Plan {
  import stores.{DocStore, OrgStore}
  import javax.servlet.http.{HttpServletRequest => Req}
  import com.google.appengine.api.blobstore._
  
  val blobs = BlobstoreServiceFactory.getBlobstoreService
  
  def filter = {
    
    case POST(Path(Seg(org :: project :: version :: srcName :: _), Params(params, RequestContentType(contentTypes, req)))) => 
      val path = url(req)
      val docContentType = contentTypes.headOption.getOrElse(contentType(path))
      Ok ~> ResponseString(blobs.createUploadUrl("/uploads?path=%s&contentType=%s" format(path, docContentType)))
    
    case POST(Path("/uploads", Params(params,  RequestContentType(contentTypes, req)))) => 
      Params.Query[String](params) { q =>
        for {
          sig <- q("sig") required("sig")
          orgId <- q("orgId") required("orgId")
          path <- q("path") required("path")
        } yield {
          blobs.getUploadedBlobs(req).get("src") match {
            case null => BadRequest ~> ResponseString("src file is required")
            case blobKey =>
              bytesFrom(new BlobstoreInputStream(blobKey)){ bytes =>
                authorize(sig.get, orgId.get, path.get, bytes) match {
                  case true => {
                    val docContentType = contentTypes.headOption.getOrElse(contentType(path.get))
                    DocStore + (path.get, docContentType, blobKey.getKeyString)
                    Created ~> ContentType(docContentType)
                  }
                  case _ =>
                    blobs.delete(blobKey)
                    Unauthorized
                }
              }  
          }
        }
      } orElse { errors =>
        BadRequest ~> ResponseString(errors map("%s is required" format(_)) mkString ". ")
      }
  
      case GET(Path(Seg(org :: project :: version :: srcName :: _), req)) =>
        DocStore(url(req)) match {
          case Some(src) =>
            Ok ~> ContentType(src.contentType) ~> (src.doc match{
              case null => BlobResponder(src.blobKey, blobs)
              case _ => ResponseBytes(src.doc.getBytes)
            })
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
        PlainTextContent ~> ResponseString(
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
          case _ => BadRequest ~> ResponseString("orgId required")
        }
  }
}
