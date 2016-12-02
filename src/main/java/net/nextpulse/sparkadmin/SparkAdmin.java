package net.nextpulse.sparkadmin;

import com.google.gson.Gson;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import net.nextpulse.sparkadmin.dsl.ResourceBuilder;
import net.nextpulse.sparkadmin.filters.Filters;
import net.nextpulse.sparkadmin.helpers.Path;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.route.RouteOverview;
import spark.template.freemarker.FreeMarkerEngine;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Main class for the SparkAdmin
 *
 * @author yholkamp
 */
public class SparkAdmin {

  private static final Logger logger = LogManager.getLogger();

  private DataSource dataSource;
  private Map<String, Resource> resources = new HashMap<>();
  private ResourceDecorator resourceDecorator;
  private Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
  private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(freemarkerConfiguration);
  private Gson gson = new Gson();
  /**
   * Indicates whether the application has been initialized, after initialization certain settings may not be changed.
   */
  private boolean initialized = false;

  public SparkAdmin(DataSource dataSource) {
    this.dataSource = dataSource;
    resourceDecorator = new ResourceDecorator(dataSource);

    freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(SparkAdmin.class, "/sparkadmin"));
    freemarkerConfiguration.addAutoImport("root", "template.ftl");
    freemarkerConfiguration.setBooleanFormat("enabled,disabled");
  }

  /**
   * Initializes the Spark Admin application with the default /admin URL prefix.
   */
  public void init() {
    init("/admin");
  }

  /**
   * Initializes the Spark Admin application with a custom URL prefix.
   *
   * @param prefix  url prefix to use, i.e. '/admin'
   */
  public void init(String prefix) {
    initializeRoutes(prefix);
    initialized = true;
  }

  private void initializeRoutes(String prefix) {
    // ensure the urls are consistently without trailing slash
    before(prefix + "/*", Filters.removeTrailingSlashes);

    // ensure that only valid formPages may be loaded
    before(prefix + Path.Route.EDIT_ROW, Filters.validateTable(resources));
    before(prefix + Path.Route.INDEX, Filters.validateTable(resources));

    CrudController controller = new CrudController(dataSource, prefix, resources);
    get(prefix + Path.Route.INDEX, controller.indexRoute, freeMarkerEngine);
    get(prefix + Path.Route.EDIT_ROW, controller.editRoute, freeMarkerEngine);
    post(prefix + Path.Route.EDIT_ROW, controller.editPostRoute, gson::toJson);
    get(prefix + Path.Route.ADMIN_INDEX, (request, response) -> "WIP");

    get(prefix + Path.Route.WILDCARD, (request, response) -> {
      throw new NotFoundException();
    });

    after(prefix + "/*", Filters.addGzipHeader);
    RouteOverview.enableRouteOverview();

    // either show the debug screen or handle exceptions
    exception(NotFoundException.class, (e, request, response) -> {
      response.status(404);
      response.body("Not found.");
    });

    exception(Exception.class, (e, request, response) -> {
      logger.error("Caught an error, whoops", e);
      response.body("<h1>Internal error</h1><pre>"+ ExceptionUtils.getStackTrace(e) + "</pre>");
    });
  }

  /**
   * Adds a new resource to the admin application and returns a configuration object to allow for further customization.
   *
   * @param resourceName  table name of the resource
   * @return  ResourceBuilder instance for further configuration
   */
  public ResourceBuilder resource(String resourceName) {
    if(StringUtils.isBlank(resourceName)) {
      throw new IllegalArgumentException("Provided resourceName was null.");
    }

    // create a new resource and look up the table properties
    // note that the resourceName may be case sensitive for certain database systems
    Resource resource = new Resource(resourceName);
    resourceDecorator.decorate(resource);
    this.resources.put(resource.getTableName(), resource);
    return new ResourceBuilder(resource);
  }

  /**
   * Returns the Freemarker Configuration object, allowing the configuration of alternative template loaders.
   * @return  Freemarker Configuration object
   */
  @SuppressWarnings("unused")
  public Configuration getFreemarkerConfiguration() {
    return freemarkerConfiguration;
  }

  /**
   *
   * @return
   */
  public Map<String, Resource> getResources() {
    return resources;
  }
}

