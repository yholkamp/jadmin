package net.nextpulse.jadmin;

import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.resource.ClassPathResource;
import spark.staticfiles.MimeType;
import spark.utils.GzipUtils;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles serving static files.
 */
public class StaticFileServer {
  private static final Logger logger = LogManager.getLogger();
  private static final String RESOURCE_PATH = "/jadmin/public/";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String STATIC_FILE_PATH_REGEX = "\\A%s(?:/css/|/fonts/|/js/|/favicon\\.ico).+\\z";
  private final Pattern allowedPrefixes;
  private final String prefix;
  
  public StaticFileServer(String prefix) {
    this.prefix = prefix;
    allowedPrefixes = Pattern.compile(String.format(STATIC_FILE_PATH_REGEX, prefix));
  }
  
  /**
   * Given a request and response object, attempts to serve a static file if a matching file might be available.
   *
   * @param request  user's request
   * @param response
   * @return true if a response was sent to the client
   */
  public boolean consume(HttpServletRequest request, HttpServletResponse response) {
    if(matchesStaticPath(request.getPathInfo())) {
      String filePath = RESOURCE_PATH + request.getPathInfo().replace(prefix, "");
      String simplifiedPath = Files.simplifyPath(filePath);
      
      // make sure we don't allow path traversal (i.e. visiting /jadmin/css/../../conf/database.properties)
      if(!simplifiedPath.startsWith(RESOURCE_PATH)) {
        return false;
      }
      
      ClassPathResource resource = new ClassPathResource(simplifiedPath);
      if(resource.exists()) {
        response.setHeader(CONTENT_TYPE, MimeType.fromResource(resource));
        
        try(OutputStream wrappedOutputStream = GzipUtils.checkAndWrap(request, response, false)) {
          IOUtils.copy(resource.getInputStream(), wrappedOutputStream);
          wrappedOutputStream.flush();
          logger.trace("Served {} from disk", simplifiedPath);
          return true;
        } catch(IOException e) {
          logger.error("Failed to copy resource {}", simplifiedPath, e);
        }
      }
    }
    return false;
  }
  
  /**
   * @param pathInfo
   * @return true iff the request has path that matches one of the accepted static file locations
   */
  protected boolean matchesStaticPath(String pathInfo) {
    Matcher matcher = allowedPrefixes.matcher(pathInfo);
    return matcher.matches();
  }
}
