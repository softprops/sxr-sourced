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
  
  serve {
    // PUT /<org-id>/<project-name>/<version>/<sourcename>.html
    case req @Req(org :: project :: version :: srcName :: Nil, _, PutRequest) =>
      lazy val body = req.body.map(b => <<<(new ByteArrayInputStream(b)))
      for {
        sig <- req.param("sig") ?~ "sig required" ~> 400
        file <- body
        val lns = file.getLines.mkString("")
        src <- (if(lns.isEmpty) Empty else Full(lns)) ?~ "src required" ~> 400
        url <- Full(url(req))
      } yield 
        authorize(
          sig, org, url, src
        ) match {
          case true => {
            SrcStore + (url -> src)
            SrcCreatedResponse(url, "text/html")
          }
          case _ => UnauthorizedResponse(url)
        }
    // GET /<org-id>/<project-name>/<version>/<sourcename>.html
    case req @Req(org :: project :: version :: srcName :: _, ".scala.html", GetRequest) =>
      SrcStore(url(req)) match {
        case Some(src) => SrcResponse(src, Nil, 200)
        case _ => NotFoundResponse("%s not found" format url(req))
      }
    case _ => NotFoundResponse("not found")
  }
}