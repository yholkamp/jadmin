package net.nextpulse.jadmin;

import com.google.gson.Gson;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import net.nextpulse.jadmin.dao.AbstractDAO;
import net.nextpulse.jadmin.dao.GenericSQLDAO;
import net.nextpulse.jadmin.dsl.ResourceBuilder;
import net.nextpulse.jadmin.exceptions.NotFoundException;
import net.nextpulse.jadmin.filters.Filters;
import net.nextpulse.jadmin.helpers.Path;
import net.nextpulse.jadmin.helpers.ResourceDecorator;
import net.nextpulse.jadmin.schema.GenericSQLSchemaProvider;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;
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
 * Main class for the JAdmin library. Provides user access to the configuration and performs the required initialization.
 *
 * @author yholkamp
 */
public class JAdmin {

  private static final Logger logger = LogManager.getLogger();
  private static final Gson gson = new Gson();
  private Map<String, Resource> resources = new HashMap<>();
  private Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
  private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(freemarkerConfiguration);
  private Service spark;
  /**
   * Indicates whether the application has been initialized, after initialization certain settings may not be changed.
   */
  private boolean initialized = false;

  public JAdmin() {
    freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(JAdmin.class, "/JAdmin"));
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
   * @param prefix url prefix to use, i.e. '/admin'
   */
  public void init(String prefix) {
    if(initialized) {
      throw new IllegalStateException("JAdmin was already initialized.");
    }
    initializeRoutes(prefix);
    initialized = true;
  }

  /**
   * Initializes the internal Spark instance.
   *
   * @param prefix path prefix to use, i.e. '/admin'
   */
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
    spark.get("*", ((request, response) -> {
      throw new NotFoundException();
    }));

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

    logger.info("JAdmin started, listening for traffic on http://localhost:{}{}", port, prefix);
  }

  /**
   * Adds a new resource with a custom DAO and schema provider class to the admin application and returns a
   * configuration object to allow for further customization.
   * Note that the resourceName will be case sensitive for certain database systems.
   *
   * @param resourceName           name of the resource
   * @param dataAccessObject       object providing CRUD methods for the resource
   * @param resourceSchemaProvider method that provides schema information for the resource
   * @return ResourceBuilder instance for further configuration
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
   * Adds a new resource to the admin application using the default JDBC DAO and schema provider via the provided DataSource.
   * Returns a configuration object to allow for further customization.
   * Note that the resourceName will be case sensitive for certain database systems.
   *
   * @param resourceName table name of the resource
   * @param dataSource   SQL datasource to use
   * @return ResourceBuilder instance for further configuration
   */
  public ResourceBuilder resource(String resourceName, DataSource dataSource) {
    return resource(resourceName, new GenericSQLDAO(dataSource, resourceName), new GenericSQLSchemaProvider(dataSource, resourceName));
  }

  /**
   * Returns the Freemarker Configuration object, allowing the configuration of alternative template loaders.
   *
   * @return Freemarker Configuration object
   */
  @SuppressWarnings("unused")
  public Configuration getFreemarkerConfiguration() {
    return freemarkerConfiguration;
  }

  /**
   * Returns all resources currently known to JAdmin.
   *
   * @return map of all resources keyed by name
   */
  public Map<String, Resource> getResources() {
    return resources;
  }

  /**
   * Halts the JAdmin interface.
   */
  public void stop() {
    spark.stop();
  }
}

