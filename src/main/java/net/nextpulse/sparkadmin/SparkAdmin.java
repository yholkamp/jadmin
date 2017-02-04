package net.nextpulse.sparkadmin;

import com.google.gson.Gson;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import net.nextpulse.sparkadmin.dao.AbstractDAO;
import net.nextpulse.sparkadmin.dao.GenericSQLDAO;
import net.nextpulse.sparkadmin.dsl.ResourceBuilder;
import net.nextpulse.sparkadmin.exceptions.NotFoundException;
import net.nextpulse.sparkadmin.filters.Filters;
import net.nextpulse.sparkadmin.helpers.Path;
import net.nextpulse.sparkadmin.helpers.ResourceDecorator;
import net.nextpulse.sparkadmin.schema.GenericSQLSchemaProvider;
import net.nextpulse.sparkadmin.schema.ResourceSchemaProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Main class for the SparkAdmin
 *
 * @author yholkamp
 */
public class SparkAdmin {

  private static final Logger logger = LogManager.getLogger();

  private Map<String, Resource> resources = new HashMap<>();
  private Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
  private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(freemarkerConfiguration);
  private static final Gson gson = new Gson();
  private Service spark;
  /**
   * Indicates whether the application has been initialized, after initialization certain settings may not be changed.
   */
  private boolean initialized = false;

  public SparkAdmin() {
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
    if(initialized) {
      throw new IllegalStateException("SparkAdmin was already initialized.");
    }
    initializeRoutes(prefix);
    initialized = true;
  }

  private void initializeRoutes(String prefix) {
    int port = 8282;
    spark = Service.ignite().port(port);
    spark.staticFiles.location("/public");

    // ensure the urls are consistently without trailing slash
    spark.before(prefix + "/*", Filters.removeTrailingSlashes);

    // ensure that only valid formPages may be loaded
    spark.before(prefix + Path.Route.EDIT_ROW, Filters.validateTable(resources));
    spark.before(prefix + Path.Route.INDEX, Filters.validateTable(resources));

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

    spark.after(prefix + "/*", Filters.addGzipHeader);

    // TODO: only add this route when not using an existing spark instance
    spark.get("*", ((request, response) -> {throw new NotFoundException();}));

    // either show the debug screen or handle exceptions
    spark.exception(NotFoundException.class, (e, request, response) -> {
      logger.error(request.uri() + " does not exist");
      response.status(404);
      response.body("Not found.");
    });

    spark.exception(Exception.class, (e, request, response) -> {
      logger.error("Caught an error, whoops", e);
      response.body("<h1>Internal error</h1><pre>"+ ExceptionUtils.getStackTrace(e) + "</pre>");
    });

    logger.info("SparkAdmin started on port {}", port);
  }

  /**
   * Adds a new resourceSchemaProvider to the admin application and returns a configuration object to allow for further customization.
   * Note that the resourceName will be case sensitive for certain database systems.
   *
   * @param resourceName            name of the resourceSchemaProvider
   * @param dataAccessObject        object providing CRUD methods for the resourceSchemaProvider
   * @param resourceSchemaProvider  method that provides schema information for the resource
   * @return  ResourceBuilder instance for further configuration
   */
  public ResourceBuilder resource(String resourceName, AbstractDAO dataAccessObject, ResourceSchemaProvider resourceSchemaProvider) {
    if(StringUtils.isBlank(resourceName)) {
      throw new IllegalArgumentException("Provided resourceName was null.");
    }

    // create a new resourceSchemaProvider and look up the table properties
    Resource resource = new Resource(resourceName);
    resource.setDao(dataAccessObject);
    dataAccessObject.initialize(resourceSchemaProvider);

    // load the default columns for this resourceSchemaProvider
    new ResourceDecorator().accept(resource, resourceSchemaProvider);

    this.resources.put(resource.getTableName(), resource);
    return new ResourceBuilder(resource);
  }

  /**
   * Adds a new resourceSchemaProvider to the admin application and returns a configuration object to allow for further customization.
   * Note that the resourceName will be case sensitive for certain database systems.
   *
   * @param resourceName  table name of the resourceSchemaProvider
   * @param dataSource    SQL datasource to use
   * @return  ResourceBuilder instance for further configuration
   */
  public ResourceBuilder resource(String resourceName, DataSource dataSource) {
    return resource(resourceName, new GenericSQLDAO(dataSource, resourceName), new GenericSQLSchemaProvider(dataSource, resourceName));
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

  /**
   * Halts the SparkAdmin interface.
   */
  public void stop() {
    spark.stop();
  }
}

