package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dao.DataAccessException;
import net.nextpulse.jadmin.dao.DatabaseEntry;
import net.nextpulse.jadmin.dsl.InputTransformer;
import net.nextpulse.jadmin.dsl.InvalidInputException;
import net.nextpulse.jadmin.exceptions.NotFoundException;
import net.nextpulse.jadmin.helpers.DataPresentationHelper;
import net.nextpulse.jadmin.helpers.Path;
import net.nextpulse.jadmin.views.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.*;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The main class of the JAdmin library when it comes to handling the different HTTP endpoints that are supported.
 * Implements all CRUD actions (create, read, update, delete).
 *
 * @author yholkamp
 */
public class CrudController {
  private static final Logger logger = LogManager.getLogger();
  
  private final String prefix;
  private final Map<String, Resource> resources;
  
  /**
   * Lists the instances of a specific resource.
   */
  public TemplateViewRoute listRoute = (request, response) -> {
    logger.trace("GET {}", request.uri());
    Resource resource = request.attribute("resourceSchemaProvider");
    
    ListView viewModel = new ListView(resource, resource.getIndexColumns(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(viewModel, Path.Template.LIST);
  };
  
  /**
   * Default column to use for sorting //TODO: make this configurable per resource
   */
  private static final int DEFAULT_SORT_COLUMN = 0;
  /**
   * Default direction to use for sorting, true for ascending, false for descending //TODO: make this configurable per resource
   */
  private static final boolean DEFAULT_SORT_DIR = true;
  
  /**
   * JSON API that returns the instances found for the provided resource, providing the data feed for the DataTables
   * frontend.
   */
  public Route listJsonRoute = (request, response) -> {
    logger.trace("GET {}", request.uri());
    response.type("application/json");
    Resource resource = request.attribute("resourceSchemaProvider");
    
    // security token
    int draw = Optional.ofNullable(request.queryMap("draw").value()).map(Integer::valueOf).orElse(0);
    
    // process the data filtering parameters
    int offset = Optional.ofNullable(request.queryMap("start").value()).map(Integer::valueOf).orElse(0);
    int count = Optional.ofNullable(request.queryMap("length").value()).map(Integer::valueOf).orElse(20);
    
    QueryParamsMap sortByColumn = request.queryMap("order").get("0");
    int sortByColumnNr = Optional.ofNullable(sortByColumn.get("column").value()).map(Integer::valueOf).orElse(DEFAULT_SORT_COLUMN);
    boolean sortDirection = Optional.ofNullable(sortByColumn.get("dir").value()).map(x -> x.equals("asc")).orElse(DEFAULT_SORT_DIR);
    
    logger.trace("ListJson: offset {}, count {}, sortBy {} in {}", offset, count, sortByColumnNr, sortDirection);
    List<DatabaseEntry> rows = resource.getDao().selectMultiple(offset, count, resource.getIndexColumns().get(sortByColumnNr), sortDirection);
    logger.trace("Received {} entries from the dao", rows.size());
    // ensure we only include the columns that should be available on the list page
    List<Map<String, Object>> filteredRows = DataPresentationHelper.transformDatabaseResults(resource, rows);
    return new DataTableResponse(draw, filteredRows, resource.getDao().count());
  };
  
  /**
   * Shows the edit page for a specific resource instance.
   */
  public TemplateViewRoute editRoute = (request, response) -> {
    logger.trace("GET {}", request.uri());
    Resource resource = request.attribute("resourceSchemaProvider");
    Optional<DatabaseEntry> editedObjectOption;
    String keys = request.params(":ids");
    editedObjectOption = resource.getDao().selectOne((Object[]) keys.split("/"));
    
    DatabaseEntry editedObject = editedObjectOption.orElseThrow(NotFoundException::new);
    EditView editView = new EditView(resource, editedObject, createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, Path.Template.EDIT);
  };
  
  /**
   * Handles the submission of a specific resource instance's edit form.
   */
  public Route editPostRoute = (request, response) -> {
    logger.trace("POST {}", request.uri());
    response.type("application/json");
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
    try {
      InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
    } catch(InvalidInputException e) {
      return new EditPost(false, e.getMessage());
    }
    
    processPostData(postEntry, resource);
    
    try {
      resource.getDao().update(postEntry);
    } catch(DataAccessException e) {
      logger.error("DataAccessException while updating existing row", e);
      return new EditPost(false, e.getMessage());
    }
    return new EditPost(true, null);
  };
  
  public Route deleteRoute = (request, response) -> {
    logger.trace("DELETE {}", request.uri());
    response.type("application/json");
    
    Resource resource = request.attribute("resourceSchemaProvider");
    String keys = request.params(":ids");
    try {
      resource.getDao().delete((Object[]) keys.split("/"));
    } catch(DataAccessException e) {
      logger.error("DataAccessException while deleting row", e);
      return new EditPost(false, e.getMessage());
    }
    return new EditPost(true, null);
  };
  
  /**
   * Handles the 'new' form for a specific resource.
   */
  public TemplateViewRoute createRoute = (request, response) -> {
    logger.trace("GET {}", request.uri());
    Resource resource = request.attribute("resourceSchemaProvider");
    EditView editView = new EditView(resource, DatabaseEntry.buildEmpty(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, Path.Template.EDIT);
  };
  
  /**
   * Handles the submission of a new resource form.
   */
  public Route createPostRoute = (request, response) -> {
    logger.trace("POST {}", request.uri());
    response.type("application/json");
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
    try {
      InputValidator.validate(postEntry, resource, ValidationMode.CREATE);
    } catch(InvalidInputException e) {
      return new EditPost(false, e.getMessage());
    }
    
    try {
      resource.getDao().insert(postEntry);
    } catch(DataAccessException e) {
      logger.error("DataAccessException while inserting a new row", e);
      return new EditPost(false, e.getMessage());
    }
    return new EditPost(true, null);
  };
  
  /**
   * Renders the main dashboard offered by JAdmin.
   */
  public TemplateViewRoute dashboardRoute = (request, response) -> new ModelAndView(new DashboardViewObject(createTemplateObject(null)), Path.Template.JADMIN_INDEX);
  private static final int COUNT_PER_PAGE = 20;
  
  /**
   * Constructor for this class, used internally.
   *
   * @param prefix    path prefix to use, i.e. '/admin'
   * @param resources map of resources set up for JAdmin
   */
  public CrudController(String prefix, Map<String, Resource> resources) {
    this.prefix = prefix;
    this.resources = resources;
  }
  
  /**
   * Helper method that creates a base TemplateObject for the provided table.
   *
   * @param table internal name of the active resource
   * @return a new TemplateObject used to render the navigation in our templates
   */
  private TemplateObject createTemplateObject(String table) {
    return new TemplateObject(prefix, new ArrayList<>(resources.keySet()), table);
  }
  
  /**
   * Construct a new FormPostEntry from the user provided data, filtered down to only include editable fields and the object keys.
   *
   * @param request  incoming user request
   * @param resource resource this request was made for
   * @return a FormPostEntry representing the user input, filtered to only include editable fields
   */
  protected static FormPostEntry extractFormPostEntry(Request request, Resource resource) {
    FormPostEntry postEntry = new FormPostEntry();
    
    for(ColumnDefinition columnDefinition : resource.getColumnDefinitions()) {
      // copy the key values that are present
      if(columnDefinition.isKeyColumn()) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addKeyValue(columnDefinition.getName(), columnQueryMap.value());
        }
      }
      // copy any editable fields that are present
      if(columnDefinition.isEditable()) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addValue(columnDefinition.getName(), columnQueryMap.value());
        }
      }
    }
    return postEntry;
  }
  
  /**
   * Processes the user post data using the optional post-processing method specified for the column.
   *
   * @param postEntry user post data that may require processing
   * @param resource
   */
  private void processPostData(FormPostEntry postEntry, Resource resource) {
    // create a map of column name to transformation function
    Map<String, InputTransformer> definitionMap = resource.getColumnDefinitions().stream()
        .filter(x -> x.getInputTransformer() != null)
        .collect(Collectors.toMap(ColumnDefinition::getName, ColumnDefinition::getInputTransformer));
    
    // apply the functions
    postEntry.getValues().forEach((key, value) -> {
      if(definitionMap.containsKey(key)) {
        String transformedInput = definitionMap.get(key).apply(value);
        postEntry.getValues().put(key, transformedInput);
      }
    });
  }
  
  /**
   * Extracts an int from a string, catching any exceptions and falling back to 0.
   *
   * @param string   string to process
   * @param fallback value to return if no valid number was provided
   * @return the number found in 'string' or 'fallback' if it was null, empty or could not be parsed
   */
  private int extractIntFromString(String string, int fallback) {
    if(StringUtils.isNotBlank(string)) {
      try {
        return Integer.valueOf(string);
      } catch(NumberFormatException e) {
        // no-op, the user sent us bad data
      }
    }
    return fallback;
  }
}
