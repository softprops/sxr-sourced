package implicitly

trait Requests {
  
  val extContentTypes = Map(
    "js" -> "text/javascript", 
    "css" -> "text/css", 
    "html" -> "text/html",
    "gz" -> "application/x-gzip"
  )
  
  def contentType(url: String) = extContentTypes.get(url.split("[.]").last) match {
    case Some(ct) => ct
    case _ => "text/html"
  }
}