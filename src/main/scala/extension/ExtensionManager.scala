package extension

import javax.servlet.ServletContextEvent
import org.slf4j.LoggerFactory
import java.util.ServiceLoader
import org.osgi.framework.launch.{Framework, FrameworkFactory}

object ExtensionManager {

  private val logger = LoggerFactory.getLogger(ExtensionManager.getClass)

  def start(event: ServletContextEvent): Unit = {
    val factoryLoader = ServiceLoader.load(classOf[FrameworkFactory])
    val config = new java.util.HashMap[String, String]

    import scala.collection.JavaConverters._
    val framework = factoryLoader.iterator.asScala.collectFirst { case factory =>
      factory.newFramework(config)
    }.get
    framework.init
    framework.start

    event.getServletContext.setAttribute("felix", framework)
    logger.info("Felix is started.")

    // TODO install OSGi bundles
    try {
      val bundleContext = framework.getBundleContext
      val bundle = bundleContext.installBundle(new java.io.File(
        "C:\\Users\\takezoe\\Desktop\\plugins\\test_1.0.0.201310031710.jar").toURI.toURL.toString)
      bundle.start()
    } catch {
      case ex: Throwable => ex.printStackTrace()
    }
  }

  def shutdown(event: ServletContextEvent): Unit = {
    val framework = event.getServletContext.getAttribute("felix").asInstanceOf[Framework]
    framework.stop
    logger.info("Felix is stopped.")
  }

}
