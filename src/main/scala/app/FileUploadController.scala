package app

import _root_.util.{Keys, FileUtil}
import util.ControlUtil._
import org.scalatra._
import org.scalatra.servlet.{MultipartConfig, FileUploadSupport}
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.util.Base64
import java.util.Date

/**
 * Provides Ajax based file upload functionality.
 *
 * This servlet saves uploaded file as temporary file and returns the unique id.
 * You can get uploaded file using [[app.FileUploadControllerBase#getTemporaryFile()]] with this id.
 */
class FileUploadController extends ScalatraServlet
  with FileUploadSupport with FlashMapSupport with FileUploadControllerBase {

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(20 * 1024 * 1024)))

  post("/image"){
    fileParams.get("file") match {
      case Some(file) if(FileUtil.isImage(file.name)) => defining(generateFileId){ fileId =>
        FileUtils.writeByteArrayToFile(getTemporaryFile(fileId), file.get)
        session += Keys.Session.Upload(fileId) -> file.name
        Ok(fileId)
      }
      case None => BadRequest
    }
  }
  
  post("/file"){
    fileParams.get("file") match {
      case Some(file) => defining(generateFileId){ fileId =>
        FileUtils.writeByteArrayToFile(getTemporaryFile(fileId), file.get)
        session += Keys.Session.Upload(fileId) -> file.name
        Ok(fileId)
      }
      case None => BadRequest
    }
  }
  
  post("/canvas") {
    val data = params("data").replaceAll("^data:.*?;base64,(.*)$", "$1")
    if (data != "") {
      defining(generateFileId){ fileId =>
        FileUtils.writeByteArrayToFile(getTemporaryFile(fileId), Base64.decode(data));
        session += Keys.Session.Upload(fileId) -> "%tY-%<tm-%<td_%<tH-%<tM-%<tS.png".format(new Date)
        Ok(fileId)
      }
    } else BadRequest
  }
}
