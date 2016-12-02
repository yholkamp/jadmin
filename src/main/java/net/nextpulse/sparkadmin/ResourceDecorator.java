package net.nextpulse.sparkadmin;

import net.nextpulse.sparkadmin.elements.FormButtons;
import net.nextpulse.sparkadmin.elements.FormInput;
import net.nextpulse.sparkadmin.elements.FormInputGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class that decorates a Resource object using type information retrieved from the provided DBMS.
 *
 * @author yholkamp
 */
public class ResourceDecorator {
  private static final Logger logger = LogManager.getLogger();
  private static final String COLUMN_NAME = "COLUMN_NAME";
  private static final String TYPE_NAME = "TYPE_NAME";

  private final DataSource dataSource;

  public ResourceDecorator(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Decorates the provided resource with information provided by the DBMS.
   *
   * @param resource    object to decorate
   */
  public void decorate(Resource resource) {
    logger.trace("Decorating {}", resource.getTableName());
    try(Connection conn = dataSource.getConnection()) {
      retrievePrimaryKeys(conn, resource);
      retrieveColumns(conn, resource);
      populateResource(resource);
    } catch(SQLException e) {
      logger.error("Could not retrieve column names for table " + resource.getTableName(), e);
    }
  }

  /**
   * Initializes the provided resource with input fields for all non-primary key fields and index columns for all columns.
   * @param resource
   */
  private static void populateResource(Resource resource) {
    FormInputGroup inputGroup = new FormInputGroup();

    resource.getColumnDefinitions().forEach(x -> {
      logger.trace("Adding default for {} col {}", resource.getTableName(), x.getName());
      if(!resource.getPrimaryKeys().contains(x.getName())) {
        inputGroup.getInputs().add(new FormInput(x.getName(), x.getType()));
        resource.getEditableColumns().add(x.getName());
      }
      resource.getIndexColumns().add(x.getName());
    });
    resource.getFormPage().add(inputGroup);
    resource.getFormPage().add(new FormButtons());
  }

  private static void retrieveColumns(Connection conn, Resource resource) throws SQLException {
    ResultSet rs = conn.getMetaData().getColumns(null, null, resource.getTableName().toLowerCase(), "%");
    while(rs.next()) {
      ColumnDefinition columnDefinition = new ColumnDefinition();
      String columnName = rs.getString(COLUMN_NAME);
      String typeName = rs.getString(TYPE_NAME);
      columnDefinition.setName(columnName);
      columnDefinition.setStringType(typeName);
      resource.getColumnDefinitions().add(columnDefinition);
    }
  }

  private static void retrievePrimaryKeys(Connection conn, Resource resource) throws SQLException {
    ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(null, null, resource.getTableName().toLowerCase());
    while(primaryKeyResultSet.next()) {
      String columnName = primaryKeyResultSet.getString(COLUMN_NAME);
      resource.getPrimaryKeys().add(columnName);
    }
  }
}
