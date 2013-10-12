package extension

import scala.collection.mutable.ListBuffer
import org.scalatra.ScalatraFilter
import org.slf4j.LoggerFactory

/**
 * Provides extension points for plug-ins.
 */
object ExtensionPoints {

  private val logger = LoggerFactory.getLogger(ExtensionPoints.getClass)

  private[extension] val controllers = new ListBuffer[ScalatraFilter]

  def registerController(controller: ScalatraFilter): Unit = {
    logger.info(s"registerController: ${controller.getClass.getName}")
    controllers.append(controller)
  }

}
