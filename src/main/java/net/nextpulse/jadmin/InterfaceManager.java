package net.nextpulse.jadmin;

import com.google.gson.Gson;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import net.nextpulse.jadmin.exceptions.NotFoundException;
import net.nextpulse.jadmin.filters.Filters;
import net.nextpulse.jadmin.helpers.I18n;
import net.nextpulse.jadmin.helpers.templatemethods.I18nTranslate;
import net.nextpulse.jadmin.helpers.Path;
import net.nextpulse.jadmin.helpers.templatemethods.I18nTranslateSimpleFallback;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Service;
import spark.staticfiles.StaticFilesConfiguration;
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
   * Flag to indicate whether JAdmin is running in stand alone mode or as part of an existing Spark app; in the latter
   * case we have to be careful not to overwrite any existing settings.
   */
  private boolean standAlone = false;
  
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
  
  InterfaceManager(Map<String, Resource> resources) {
    this.resources = resources;
    
    freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(JAdmin.class, "/jadmin/templates"));
    freemarkerConfiguration.addAutoImport("root", "template.ftl");
    // register i() as translation function
    freemarkerConfiguration.setSharedVariable("i", new I18nTranslate());
    // and register ii() as translation function where the fallback should be user friendly 
    freemarkerConfiguration.setSharedVariable("ii", new I18nTranslateSimpleFallback());
  }
  
  /**
   * Initializes the internal Spark instance.
   *
   * @param prefix prefix to use for all URLs, i.e. "/admin"
   * @param port   port to listen for connections
   */
  void initialize(String prefix, int port) {
    if(this.prefix != null) {
      throw new IllegalArgumentException("Cannot invoke the initialization code more than once");
    }
    this.standAlone = true;
    this.prefix = prefix;
    this.spark = Service.ignite().port(port);
    
    configureSpark();
    logger.info("JAdmin started, listening for traffic on http://localhost:{}{}", port, this.prefix);
  }
  
  /**
   * Initializes the internal Spark instance.
   *
   * @param prefix prefix to use for all URLs, i.e. "/admin"
   * @param spark  spark instance
   */
  void initialize(String prefix, Service spark) {
    if(this.prefix != null) {
      throw new IllegalArgumentException("Cannot invoke the initialization code more than once");
    }
    this.prefix = prefix;
    this.spark = spark;
    configureSpark();
    
    if(!standAlone) {
      logger.info("JAdmin started, attached to an existing Spark app");
    }
  }
  
  /**
   * Configures the internal filters and routes.
   */
  private void configureSpark() {
    // Set language-specific template values
    freemarkerConfiguration.setBooleanFormat(I18n.get("format.boolean"));
    freemarkerConfiguration.setDateTimeFormat(I18n.get("format.datetime"));
    freemarkerConfiguration.setDateFormat(I18n.get("format.date"));
    freemarkerConfiguration.setDateFormat(I18n.get("format.time"));
    
    // ensure the urls are consistently without trailing slash
    configureFilters();
    configureRoutes();
    configureExceptionHandlers();
    
    // configure the static file location for JAdmin as before filter, as we cannot rely on the static file location 
    // when JAdmin isn't running in standalone mode.
    StaticFilesConfiguration staticFilesConfiguration = new StaticFilesConfiguration();
    staticFilesConfiguration.configure("/jadmin/public");
    spark.before((request, response) -> {
      if(staticFilesConfiguration.consume(request.raw(), response.raw())) {
        throw spark.halt();
      }
    });
  }
  
  /**
   * Configures the before/after filters in Spark, such as validation that the route exists, removing trailing slashes
   * from the URL for consistency and adding a gzip header to the output.
   */
  private void configureFilters() {
    spark.before(prefix + "/*", Filters.removeTrailingSlashes);
    
    // ensure that only valid formPages may be loaded
    spark.before(prefix + Path.Route.EDIT_ROW, Filters.validateTable(resources));
    spark.before(prefix + Path.Route.LIST_ROWS, Filters.validateTable(resources));
  }
  
  /**
   * Sets up the Spark configuration for the available HTTP routes.
   */
  private void configureRoutes() {
    CrudController controller = new CrudController(prefix, resources);
    spark.get(prefix + Path.Route.LIST_ROWS, controller.listRoute, freeMarkerEngine);
    spark.get(prefix + Path.Route.CREATE_ROW, controller.createRoute, freeMarkerEngine);
    spark.post(prefix + Path.Route.CREATE_ROW, controller.createPostRoute, gson::toJson);
    spark.get(prefix + Path.Route.EDIT_ROW, controller.editRoute, freeMarkerEngine);
    spark.post(prefix + Path.Route.EDIT_ROW, controller.editPostRoute, gson::toJson);
    spark.delete(prefix + Path.Route.DELETE_ROW, controller.deleteRoute, gson::toJson);
    spark.get(prefix + Path.Route.ADMIN_INDEX, controller.dashboardRoute, freeMarkerEngine);
    
    spark.get(prefix + Path.Route.WILDCARD, (request, response) -> {
      throw new NotFoundException();
    });
    
    if(standAlone) {
      spark.get("*", ((request, response) -> {
        throw new NotFoundException();
      }));
    }
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
    
    if(standAlone) {
      spark.exception(Exception.class, (e, request, response) -> {
        logger.error("Caught an error, whoops", e);
        response.body("<h1>Internal error</h1><pre>" + ExceptionUtils.getStackTrace(e) + "</pre>");
      });
    }
  }
  
  /**
   * Halts the Spark server.
   */
  public void stop() {
    spark.stop();
  }
  
  /**
   * @return the internal spark instance
   */
  public Service getSpark() {
    return spark;
  }
  
  /**
   * @return the Freemarker configuration that's being used
   */
  public Configuration getFreemarkerConfiguration() {
    return freemarkerConfiguration;
  }
}
