package implicitly

import net.liftweb.http._
import net.liftweb.http.rest._

import stores.{DocStore, OrgStore}

/** Sourced - serving scala for the _good_ of mankind **/
object Sourced extends RestHelper with Responses with Urls with Requests with Auth {
  import net.liftweb.json._
  import net.liftweb.common._
  import net.liftweb.util.Helpers.tryo
  import scala.io.Source.{ fromInputStream => <<< }
  import java.io.ByteArrayInputStream
  
  serve {
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
            DocStore + (url -> src)
            SrcCreatedResponse(url, contentType(url))
          }
          case _ => UnauthorizedResponse(url)
        }
    case req @Req(org :: project :: version :: srcName :: _, _, GetRequest) =>
      DocStore(url(req)) match {
        case Some(src) => SrcResponse(src.doc.getValue, contentType(src.url), Nil, 200)
        case _ => NotFoundResponse("%s not found" format url(req))
      }
    case req @Req("admin" :: Nil, _, GetRequest) => adminPage(req) {
        <form action="setkey" method="post">
          <input type="text" name="orgId" />
          <input type="submit" value="Generate Token" />
        </form>
    }
    case req @Req("setkey" :: Nil, _, PostRequest) => 
      for {
        orgId <- req.param("orgId") ?~ "sig required" ~> 400
      } yield {
        adminPage(req) {
          val key = generateKey
          OrgStore + (orgId, key)
          <div> { key } </div>
        }
      }
    case req => NotFoundResponse("%s not found" format url(req))
  }
}