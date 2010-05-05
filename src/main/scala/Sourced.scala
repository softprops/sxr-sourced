package implicitly

import net.liftweb.http._
import net.liftweb.http.rest._

/** Sourced - serving scala for the _good_ of mankind **/
object Sourced extends RestHelper with Auth {
  import net.liftweb.json._
  import net.liftweb.common._
  
  /** A `looser` html response for source documents */
  case class SrcResponse(src: String, headers: List[(String, String)], code: Int) extends LiftResponse {
    def toResponse = {
      val bytes = src.getBytes("UTF-8")
      InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) :: ("Content-Type", "text/html; charset=utf-8") :: headers, Nil, code)
    }
  }
  
  serve {
    case Req(org :: project :: version :: srcName :: _, "html", PutRequest) =>
      for {
        sig <- S.param("sig") ?~ "sig required" ~> 400
        src <- S.param("file") ?~ "src body required" ~> 400
      } yield {
        authorize(
          sig, org, S.hostAndPath, src
        ) match {
          case true => {
            SrcStore + (S.hostAndPath -> src)
            CreatedResponse(<created>{S.hostAndPath}</created>, "text/html")
          }
          case _ => UnauthorizedResponse(S.hostAndPath)
        }
      }

    case Req(org :: project :: version :: srcName :: _, "html", GetRequest) =>
      SrcStore(S.hostAndPath) match {
        case Some(src) => SrcResponse(src, Nil, 200)
        case _ => NotFoundResponse("%s not found" format S.hostAndPath)
      }
  }
}