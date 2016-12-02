package net.nextpulse.sparkadmin.filters;

import com.google.common.base.Joiner;
import net.nextpulse.sparkadmin.NotFoundException;
import net.nextpulse.sparkadmin.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Filters that run before or after certain requests handlers.
 */
public class Filters {

  private static final Logger logger = LogManager.getLogger();

  /**
   * Removes the trailing slash from the URL when present.
   */
  public static Filter removeTrailingSlashes = (Request request, Response response) -> {
    if(request.pathInfo().endsWith("/")) {
      response.redirect(request.pathInfo().substring(0, request.pathInfo().length() - 1));
    }
  };

  /**
   * Enables GZIP for all responses
   */
  public static Filter addGzipHeader = (Request request, Response response) -> {
    response.header("Content-Encoding", "gzip");
  };

  /**
   * Ensures that the 'table' variable in the URL exists and sets the corresponding resource in the 'resource' attribute. Throws a NotFoundException if the resource was not found.
   * @param resources   available resources
   * @return  a filter object
   */
  public static Filter validateTable(Map<String, Resource> resources) {
    return (Request request, Response response) -> {
      if(request.params(":table") != null) {
        String table = request.params(":table");
        if(!resources.containsKey(table)) {
          logger.error("Did not find table " + table + " in the list of supported tables: " + Joiner.on(", ").join(resources.keySet()));
          throw new NotFoundException();
        } else {
          request.attribute("resource", resources.get(table));
        }
      }
    };
  }
}