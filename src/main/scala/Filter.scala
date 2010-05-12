package implicitly

import javax.servlet.{ServletRequest, ServletResponse, FilterChain}
import javax.servlet.http.HttpServletRequest

class Filter extends net.liftweb.http.provider.servlet.ServletFilterProvider {
  override def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    req match {
      case req: HttpServletRequest if req.getRequestURI.startsWith("/_ah/") =>
        chain.doFilter(req, res)
      case _ => super.doFilter(req, res, chain)
    }
  }
}
