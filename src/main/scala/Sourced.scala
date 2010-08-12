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

trait Encoding {
  import java.net.URLEncoder.encode
  def urlencode(str: String) = encode(str, "utf8")
}

/** Sourced - serving scala for the _good_ of mankind */
class Sourced extends Responses with Urls with Requests with Auth with Encoding with unfiltered.Plan {
  import stores.{DocStore, OrgStore}
  import javax.servlet.http.{HttpServletRequest => Req}
  import com.google.appengine.api.blobstore._
  
  val blobs = BlobstoreServiceFactory.getBlobstoreService
  val blogInfoFact = new BlobInfoFactory
  
  def filter = {
    
    case POST(Path(Seg(org :: project :: version :: srcName :: _), req)) => 
      Ok ~> ResponseString(blobs.createUploadUrl(
        "/upload?org=%s&path=%s" format (urlencode(org), urlencode(url(req)))
      ))
    
    case Path("/upload", Params(params,  req)) =>
      /** send a redirect back to a handler that will respond, as appengine requires */
      def response(status: Int, msg: String) = 
        Redirect("/uploaded?status=%d&msg=%s" format (status, urlencode(msg)))
        ( for {
          q <- Params.Query[Unit](params)
          sig<- q("sig") required(())
          orgId <- q("org") required(())
          path <- q("path") required(())
        } yield {
          blobs.getUploadedBlobs(req).get("file") match {
            case null => response(400, "file is required")
            case blobKey =>
              authorize(sig.get, orgId.get, path.get, new BlobstoreInputStream(blobKey)) match {
                case true => {
                  DocStore + (path.get, blogInfoFact.loadBlobInfo(blobKey).getContentType, blobKey.getKeyString)
                  response(201, "success")
                }
                case _ =>
                  blobs.delete(blobKey)
                  response(401, "%s is not a valid sig" format sig.get)
              }
          }
        } ) orElse { fails =>
          response(400, fails map { fl => "%s is required".format(fl.name) } mkString ". ")
        }

    case GET(Path("/uploaded", Params(p, _))) =>
      ( for {
        q <- Params.Query[Unit](p)
        status <- q("status") is Params.int required(())
        msg <- q("msg") required(())
      } yield Status(status.get) ~> ResponseString(msg.get)
      ) orElse { _ => InternalServerError }

    case GET(Path(Seg(org :: project :: version :: srcName :: _), req)) =>
      DocStore(url(req)) map { src =>
        Option(src.doc) map { doc =>
          Ok ~> ContentType(src.contentType) ~> ResponseBytes(doc.getBytes)
        } getOrElse BlobResponder(src.blobKey, blobs)
      } getOrElse NotFound
  
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
