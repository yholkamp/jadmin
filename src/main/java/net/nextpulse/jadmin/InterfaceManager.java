package net.nextpulse.jadmin;

import com.google.gson.Gson;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import net.nextpulse.jadmin.exceptions.NotFoundException;
import net.nextpulse.jadmin.filters.Filters;
import net.nextpulse.jadmin.helpers.Path;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Map;

/**
 * Top level handler for all things related to the interface with the outside world. This class initializes the Spark
 * instance that's used to expose the admin panel to the user and provides access to configuration.
 */
public class InterfaceManager {
  private static final Logger logger = LogManager.getLogger();
  private static final Gson gson = new Gson();

  /**
   * Spark instance used to present the UI
   */
  private Service spark;
  /**
   * Optional URL prefix to use for all resources, i.e. "/admin"
   */
  private String prefix = null;
  private Map<String, Resource> resources;
  private Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
  private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(freemarkerConfiguration);

  public InterfaceManager(Map<String, Resource> resources) {
    this.resources = resources;

    freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(JAdmin.class, "/jadmin"));
    freemarkerConfiguration.addAutoImport("root", "template.ftl");
    freemarkerConfiguration.setBooleanFormat("enabled,disabled");
  }

  /**
   * Initializes the internal Spark instance.
   * @param prefix    prefix to use for all URLs, i.e. "/admin"
   * @param port      port to listen for connections
   */
  public void initialize(String prefix, int port) {
    if(this.prefix != null) {
      throw new IllegalArgumentException("Cannot invoke the initialization code more than once");
    }
    this.prefix = prefix;
    spark = Service.ignite().port(port);
    spark.staticFiles.location("/public");

    // ensure the urls are consistently without trailing slash
    configureFilters();
    configureRoutes();
    configureExceptionHandlers();

    logger.info("JAdmin started, listening for traffic on http://localhost:{}{}", port, this.prefix);
  }

  /**
   * Configures the before/after filters in Spark, such as validation that the route exists, removing trailing slashes
   * from the URL for consistency and adding a gzip header to the output.
   */
  private void configureFilters() {
    spark.before(prefix + "/*", Filters.removeTrailingSlashes);
    spark.after(prefix + "/*", Filters.addGzipHeader);

    // ensure that only valid formPages may be loaded
    spark.before(prefix + Path.Route.EDIT_ROW, Filters.validateTable(resources));
    spark.before(prefix + Path.Route.INDEX, Filters.validateTable(resources));
  }

  /**
   * Sets up the Spark configuration for the available HTTP routes.
   */
  private void configureRoutes() {
    CrudController controller = new CrudController(prefix, resources);
    spark.get(prefix + Path.Route.INDEX, controller.listRoute, freeMarkerEngine);
    spark.get(prefix + Path.Route.CREATE_ROW, controller.createRoute, freeMarkerEngine);
    spark.post(prefix + Path.Route.CREATE_ROW, controller.createPostRoute, gson::toJson);
    spark.get(prefix + Path.Route.EDIT_ROW, controller.editRoute, freeMarkerEngine);
    spark.post(prefix + Path.Route.EDIT_ROW, controller.editPostRoute, gson::toJson);
    spark.get(prefix + Path.Route.ADMIN_INDEX, controller.dashboardRoute, freeMarkerEngine);

    spark.get(prefix + Path.Route.WILDCARD, (request, response) -> {
      throw new NotFoundException();
    });

    // TODO: only add this route when not using an existing spark instance
    spark.get("*", ((request, response) -> {
      throw new NotFoundException();
    }));
  }

  /**
   * Configures the handlers that are invoked when an exception occurs.
   */
  private void configureExceptionHandlers() {
    // either show the debug screen or handle exceptions
    spark.exception(NotFoundException.class, (e, request, response) -> {
      logger.error(request.uri() + " does not exist");
      response.status(404);
      response.body("Not found.");
    });

    spark.exception(Exception.class, (e, request, response) -> {
      logger.error("Caught an error, whoops", e);
      response.body("<h1>Internal error</h1><pre>" + ExceptionUtils.getStackTrace(e) + "</pre>");
    });
  }

  /**
   * Halts the Spark server.
   */
  public void stop() {
    spark.stop();
  }

  /**
   *
   * @return the internal spark instance
   */
  public Service getSpark() {
    return spark;
  }

  /**
   *
   * @return the Freemarker configuration that's being used
   */
  public Configuration getFreemarkerConfiguration() {
    return freemarkerConfiguration;
  }
}
