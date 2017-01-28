package net.nextpulse.sparkadmin;

import net.nextpulse.sparkadmin.dao.DatabaseEntry;
import net.nextpulse.sparkadmin.helpers.Path;
import net.nextpulse.sparkadmin.views.*;
import spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yholkamp
 */
public class CrudController {

  private final String prefix;
  private final Map<String, Resource> resources;

  public TemplateViewRoute listRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    // TODO: implement pagination on the list page
    List<DatabaseEntry> rows = resource.getDao().selectMultiple(0, 20);

    ListView viewModel = new ListView(resource, rows, resource.getIndexColumns(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(viewModel, Path.Template.LIST);
  };

  public TemplateViewRoute editRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    Optional<DatabaseEntry> editedObjectOption = resource.getDao().selectOne((Object[]) request.splat());

    DatabaseEntry editedObject = editedObjectOption.orElseThrow(NotFoundException::new);
    EditView editView = new EditView(resource, editedObject, createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, "edit.ftl");
  };

  public Route editPostRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
    resource.getDao().update(postEntry);
    return new EditPost(true, null);
  };

  public TemplateViewRoute createRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    EditView editView = new EditView(resource, DatabaseEntry.buildEmpty(), createTemplateObject(resource.getTableName()));
    return new ModelAndView(editView, "edit.ftl");
  };

  public Route createPostRoute = (request, response) -> {
    Resource resource = request.attribute("resourceSchemaProvider");
    FormPostEntry postEntry = extractFormPostEntry(request, resource);
    resource.getDao().insert(postEntry);
    return new EditPost(true, null);
  };

  public TemplateViewRoute dashboardRoute = (request, response) -> new ModelAndView(new DashboardViewObject(createTemplateObject(null)), "index.ftl");

  public CrudController(String prefix, Map<String, Resource> resources) {
    this.prefix = prefix;
    this.resources = resources;
  }

  private TemplateObject createTemplateObject(String table) {
    return new TemplateObject(prefix, new ArrayList<>(resources.keySet()), table);
  }

  /**
   * Construct a new FormPostEntry from the user provided data, filtered down to only include editable fields and the object keys.
   *
   * @param request
   * @param resource
   * @return
   */
  private FormPostEntry extractFormPostEntry(Request request, Resource resource) {
    FormPostEntry postEntry = new FormPostEntry();

    // TODO: rework the editable property so it's included in the ColumnDefinition
    for(ColumnDefinition columnDefinition : resource.getColumnDefinitions()) {
      // copy the key values that are present
      if(resource.getPrimaryKeys().contains(columnDefinition.getName())) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addKeyValue(columnDefinition, columnQueryMap.value());
        }
      }
      // copy any editable fields that are present
      if(resource.getEditableColumns().contains(columnDefinition.getName())) {
        QueryParamsMap columnQueryMap = request.queryMap().get(columnDefinition.getName());
        if(columnQueryMap.hasValue()) {
          postEntry.addValue(columnDefinition, columnQueryMap.value());
        }
      }
    }
    return postEntry;
  }
}
