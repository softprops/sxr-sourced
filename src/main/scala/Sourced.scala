package implicitly

import net.liftweb.http._
import net.liftweb.http.rest._

/** Sourced - serving scala for the _good_ of mankind **/
object Sourced extends RestHelper with Responses with UrlHelpers with Auth {
  import net.liftweb.json._
  import net.liftweb.common._
  import net.liftweb.util.Helpers.tryo
  import scala.io.Source.{ fromInputStream => <<< }
  import java.io.ByteArrayInputStream
  
  val extContentTypes = Map(
    "js" -> "text/javascript; charset=utf-8", 
    "css" -> "text/css; charset=utf-8", 
    "html" -> "text/html; charset=utf-8"
  )
  
  def contentType(url: String) = extContentTypes.get(url.split("[.]").last) match {
    case Some(ct) => ct
    case _ => "text/html; charset=utf-8"
  }
  
  serve {
    // PUT /<org-id>/<project-name>/<version>/<sourcename>.html
    case req @Req(org :: project :: version :: srcName :: Nil, _, PutRequest) =>
      for {
        sig <- req.param("sig") ?~ "sig required" ~> 400
        body <- req.body ?~ "src required" ~> 400
        url <- Full(url(req))
      } yield 
        authorize(
          sig, org, url, body
        ) match {
          case true => {
            val src = <<<(new ByteArrayInputStream(body)).getLines.mkString
            SrcStore + (url -> src)
            SrcCreatedResponse(url, "text/html")
          }
          case _ => UnauthorizedResponse(url)
        }
    // GET /<org-id>/<project-name>/<version>/<sourcename>.html
    case req @Req(org :: project :: version :: srcName :: _, _, GetRequest) =>
      SrcStore(url(req)) match {
        
        case Some(src) => SrcResponse(src.doc.getValue, contentType(src.url), Nil, 200)
        case _ => NotFoundResponse("%s not found" format url(req))
      }
    case req @Req("admin" :: Nil, _, GetRequest) => adminPage(req) {
      <html><body>
        <form action="setkey" method="post">
          <input type="text" name="orgId" />
          <input type="submit" />
        </form>
      </body></html>
    }
    case req @Req("setkey" :: Nil, _, PostRequest) => 
      for {
        orgId <- req.param("orgId") ?~ "sig required" ~> 400
      } yield {
        adminPage(req) {
          val key = generateKey
          OrgStore + (orgId, key)
          <html><body> { key } </body></html>
        }
      }
    case req => NotFoundResponse("%s not found" format url(req))
  }
}