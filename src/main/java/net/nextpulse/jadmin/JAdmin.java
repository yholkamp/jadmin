package net.nextpulse.jadmin;

import freemarker.template.Configuration;
import net.nextpulse.jadmin.dao.AbstractDAO;
import net.nextpulse.jadmin.dao.GenericSQLDAO;
import net.nextpulse.jadmin.dsl.ResourceBuilder;
import net.nextpulse.jadmin.helpers.ResourceDecorator;
import net.nextpulse.jadmin.schema.GenericSQLSchemaProvider;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Service;

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
  /**
   * Port to listen on for inbound connections.
   */
  private int port = 8282;
  /**
   * MAp of resources associated with JAdmin, using the resource name as key, linking to an object with the configuration.
   */
  private Map<String, Resource> resources = new HashMap<>();
  /**
   * Reference to the interface handling object for this JAdmin instance.
   */
  private InterfaceManager interfaceManager = new InterfaceManager(resources);
  /**
   * Indicates whether the application has been initialized, after initialization certain settings may not be changed.
   */
  private boolean initialized = false;

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
    logger.debug("Initializing JAdmin");
    if(initialized) {
      logger.error("JAdmin was already initialized");
      throw new IllegalStateException("JAdmin was already initialized.");
    }
    interfaceManager.initialize(prefix, port);
    initialized = true;
  }
  
  /**
   * Initializes the Spark Admin application with a custom URL prefix.
   *
   * @param prefix url prefix to use, i.e. '/admin'
   * @param port port to use for jadmin
   */
  public void init(String prefix, int port) {
    this.port = port;
    init(prefix);
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
    logger.trace("Adding resource {} with {} and {}", resourceName, dataAccessObject.getClass().getSimpleName(), resourceSchemaProvider.getClass().getSimpleName());
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
    return interfaceManager.getFreemarkerConfiguration();
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
    interfaceManager.stop();
  }

  /**
   * Getter for the internal SparkJava instance used by the admin panel, may be used to supply additional configuration.
   *
   * @return    the SparkJava instance used by the admin panel
   */
  public Service getSpark() {
    return interfaceManager.getSpark();
  }
}

