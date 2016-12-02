package net.nextpulse.sparkadmin;

import net.nextpulse.sparkadmin.helpers.Path;
import net.nextpulse.sparkadmin.views.EditPost;
import net.nextpulse.sparkadmin.views.EditView;
import net.nextpulse.sparkadmin.views.ListView;
import net.nextpulse.sparkadmin.views.TemplateObject;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Route;
import spark.TemplateViewRoute;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author yholkamp
 */
public class CrudController {

  private static final Logger logger = LogManager.getLogger();
  private final String prefix;
  private final Map<String, Resource> resources;

  private DataSource dataSource;

  public TemplateViewRoute indexRoute = (request, response) -> {
    Resource model = request.attribute("resource");

    // TODO: add configurable pagination
    List<Map<String, Object>> rows = new ArrayList<>();
    String tableName = model.getTableName();
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s LIMIT %d OFFSET %d", tableName, 20, 0));
      ResultSet results = statement.executeQuery();
      while(results.next()) {
        Map<String, Object> row = new BasicRowProcessor().toMap(results);
        rows.add(row);
      }
    }

    ListView viewModel = new ListView(model, rows, model.getIndexColumns(), createTemplateObject(tableName));
    return new ModelAndView(viewModel, Path.Template.LIST);
  };

  public TemplateViewRoute editRoute = (request, response) -> {
    Resource model = request.attribute("resource");

    String[] primaryKeys = request.splat();
    // TODO: support multiple PKs
    Integer id = Integer.valueOf(primaryKeys[0]);
    Map<String, Object> editedObject = Collections.emptyMap();
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s WHERE id = ? LIMIT 1", model.getTableName()));
      statement.setInt(1, id);
      ResultSet results = statement.executeQuery();

      if(results.next()) {
        editedObject = new BasicRowProcessor().toMap(results);
      }
    }

    EditView editView = new EditView(model, editedObject, createTemplateObject(model.getTableName()));
    return new ModelAndView(editView, "edit.ftl");
  };

  public Route editPostRoute = (request, response) -> {
    Resource model = request.attribute("resource");
    String[] primaryKeys = request.splat();
    // TODO: ensure the URL and post primary keys are equal
    QueryParamsMap dataMap = request.queryMap();

    DatabaseManager manager = new DatabaseManager(dataSource);
    manager.update(model, dataMap);
    return new EditPost(true, null);
  };

  public CrudController(DataSource dataSource, String prefix, Map<String, Resource> resources) {
    this.dataSource = dataSource;
    this.prefix = prefix;
    this.resources = resources;
  }

  private TemplateObject createTemplateObject(String table) {
    return new TemplateObject(prefix, new ArrayList<>(resources.keySet()), table);
  }
}

class DatabaseManager {

  private static final Logger logger = LogManager.getLogger();

  private final DataSource dataSource;

  public DatabaseManager(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Generates a string containing x = ?, concatenated with spaces.
   *
   * @param input
   * @return
   */
  protected String createSQLPortion(Collection<String> input) {
    return input.stream()
        .map(x -> String.format("%s = ?", x))
        .reduce((s, s2) -> s + ", " + s2).orElseThrow(IllegalArgumentException::new);
  }

  /**
   * Updates a row of the provided resource type using the provided data.
   *
   * @param resource resource to update
   * @param data     data to use for the update
   * @throws SQLException
   */
  public void update(Resource resource, QueryParamsMap data) throws SQLException {
    // TODO: test
    // TODO: test changing invalid fields/not allowed models
    try(Connection conn = dataSource.getConnection()) {
      List<String> editableFields = new ArrayList<>(resource.getEditableColumns());
      // construct the SQL query
      String query = String.format("UPDATE %s SET %s WHERE %s", resource.getTableName(), createSQLPortion(editableFields), createSQLPortion(resource.getPrimaryKeys()));
      logger.debug("Prepared statement SQL: {}", query);
      PreparedStatement statement = conn.prepareStatement(query);

      List<String> queryColumnOrder = new ArrayList<>(editableFields);
      queryColumnOrder.addAll(resource.getPrimaryKeys());
      for(int counter = 0; counter < queryColumnOrder.size(); counter++) {
        String column = queryColumnOrder.get(counter);
        QueryParamsMap value = data.get(column);
        ColumnDefinition columnDefinition = resource.getColumnDefinitions().stream().filter(x -> x.getName().equals(column)).findFirst().orElseThrow(IllegalArgumentException::new);
        setValue(statement, counter+1, value, columnDefinition);
      }

      logger.info("Query: {}", statement.toString());
      int updatedRows = statement.executeUpdate();
      if(updatedRows != 1) {
        throw new SQLException("Updated " + updatedRows + ", expected 1");
      }
    }
  }

  /**
   * Query updater that attempts to use the most specific setX method based on the provided input.
   *
   * @param statement         statement to fill
   * @param index             index of the parameter to configure
   * @param value             user-provided value
   * @param columnDefinition  column definition, used to obtain type information
   * @throws SQLException     exception that may be thrown by {@link java.sql.PreparedStatement#setObject(int, Object)} and others
   */
  protected void setValue(PreparedStatement statement, int index, QueryParamsMap value, ColumnDefinition columnDefinition) throws SQLException {
    if(!value.hasValue()) {
      // TODO: use setNull here
      logger.trace("Setting null for column {}", columnDefinition.getName());
      statement.setObject(index, null);
    } else {
      switch(columnDefinition.getType()) {
        case integer:
          statement.setInt(index, value.integerValue());
          break;
        case bool:
          statement.setBoolean(index, value.booleanValue());
          break;
        case datetime:
          // TODO: handle input-to-date conversion
          statement.setObject(index, value.value());
          break;
        case varchar:
        case text:
          statement.setString(index, value.value());
          break;
        default:
          logger.error("Unsupported column definition type {} found, setting without type checking", columnDefinition.getType());
          statement.setObject(index, value.value());
          break;
      }
    }
  }


}