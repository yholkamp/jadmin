package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dao.DataAccessException;
import net.nextpulse.jadmin.dao.DatabaseEntry;
import net.nextpulse.jadmin.exceptions.NotFoundException;
import net.nextpulse.jadmin.helpers.Path;
import net.nextpulse.jadmin.views.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    Resource resource = request.attribute("resourceSchemaProvider");
    // TODO: implement pagination on the list page
    List<DatabaseEntry> rows = resource.getDao().selectMultiple(0, 20);

    ListView viewModel = new ListView(resource, rows, resource.getIndexColumns(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(viewModel, Path.Template.LIST);
  };

  /**
   * Shows the edit page for a specific resource instance.
   */
  public TemplateViewRoute editRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    Optional<DatabaseEntry> editedObjectOption;
    String keys = request.params(":ids");
    editedObjectOption = resource.getDao().selectOne((Object[]) keys.split("/"));

    DatabaseEntry editedObject = editedObjectOption.orElseThrow(NotFoundException::new);
    EditView editView = new EditView(resource, editedObject, createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, "edit.ftl");
  };

  /**
   * Handles the submission of a specific resource instance's edit form.
   */
  public Route editPostRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
    try {
      resource.getDao().update(postEntry);
    } catch(DataAccessException e) {
      logger.error("DataAccessException while updating existing row", e);
      return new EditPost(false, e.getMessage());
    }
    return new EditPost(true, null);
  };

  /**
   * Handles the 'new' form for a specific resource.
   */
  public TemplateViewRoute createRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    EditView editView = new EditView(resource, DatabaseEntry.buildEmpty(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, "edit.ftl");
  };

  /**
   * Handles the submission of a new resource form.
   */
  public Route createPostRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
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
  public TemplateViewRoute dashboardRoute = (request, response) -> new ModelAndView(new DashboardViewObject(createTemplateObject(null)), "index.ftl");

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
  private FormPostEntry extractFormPostEntry(Request request, Resource resource) {
    FormPostEntry postEntry = new FormPostEntry();

    for(ColumnDefinition columnDefinition : resource.getColumnDefinitions()) {
      // copy the key values that are present
      if(columnDefinition.isKeyColumn()) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addKeyValue(columnDefinition, columnQueryMap.value());
        }
      }
      // copy any editable fields that are present
      if(columnDefinition.isEditable()) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addValue(columnDefinition, columnQueryMap.value());
        }
      }
    }
    return postEntry;
  }
}
