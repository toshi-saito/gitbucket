package extension

import javax.servlet.ServletContextEvent
import org.slf4j.LoggerFactory
import java.util.ServiceLoader
import org.osgi.framework.launch.{Framework, FrameworkFactory}
import util.Directory._
import java.io.{File, FileFilter}

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

    event.getServletContext.setAttribute(classOf[Framework].getName, framework)
    logger.info("Felix is started.")

    // Install OSGi bundles
    val bundleContext = framework.getBundleContext
    val dir = getExtensionsDir()
    if(dir.exists()){
      dir.listFiles(new FileFilter {
        def accept(file: File): Boolean = file.getName.endsWith(".jar")
      }).foreach { file =>
        logger.info(s"Installing ${file.getName}...")
        try {
          val bundle = bundleContext.installBundle(file.toURI.toURL.toString)
          bundle.start()
        } catch {
          case ex: Throwable => logger.error(s"Failed to install ${file.getName}.", ex)
        }
      }
    }
  }

  def shutdown(event: ServletContextEvent): Unit = {
    val framework = event.getServletContext.getAttribute(classOf[Framework].getName).asInstanceOf[Framework]
    framework.stop
    logger.info("Felix is stopped.")
  }

}
