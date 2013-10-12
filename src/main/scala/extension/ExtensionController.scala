package extension

import org.scalatra.ScalatraFilter
import scala.collection.mutable.ListBuffer
import javax.servlet.{FilterChain, ServletResponse, ServletRequest}

/**
 * The controller to bridge controllers which are plugged in.
 * This controller is mapped to "/extension".
 */
class ExtensionController extends ScalatraFilter {

   override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
     val wrappedChain = new FilterChain {
       private val rest = new ListBuffer[ScalatraFilter]
       rest.appendAll(ExtensionManager.controllers)

       def doFilter(request: ServletRequest, response: ServletResponse): Unit = {
         if(rest.nonEmpty){
           rest.remove(0).doFilter(request, response, this)
         } else {
           chain.doFilter(request, response)
         }
       }
     }

     wrappedChain.doFilter(request, response)
   }

 }
