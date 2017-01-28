package net.nextpulse.sparkadmin.schema;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.dao.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic implementation of the ResourceSchemaProvider interface, providing schema retrieval logic for SQL backed resources.
 *
 * @author yholkamp
 */
public class GenericSQLSchemaProvider implements ResourceSchemaProvider {

  private static final Logger logger = LogManager.getLogger();
  private static final String COLUMN_NAME = "COLUMN_NAME";
  private static final String TYPE_NAME = "TYPE_NAME";

  private final DataSource dataSource;
  private final String tableName;

  public GenericSQLSchemaProvider(DataSource dataSource, String tableName) {
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  @Override
  public List<ColumnDefinition> getKeyColumns() throws DataAccessException {
    List<ColumnDefinition> keys = new ArrayList<>();

    // TODO: add property to ColumnDefinition to indicate whether the field is part of the key or not
    List<ColumnDefinition> columnDefinitions = getColumnDefinitions();
    try(Connection conn = dataSource.getConnection()) {
      ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(null, null, tableName.toLowerCase());
      while(primaryKeyResultSet.next()) {
        String columnName = primaryKeyResultSet.getString(COLUMN_NAME);
        columnDefinitions.stream().filter(x -> x.getName().equals(columnName)).findFirst().map(keys::add);
      }
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }

    return keys;
  }

  @Override
  public List<ColumnDefinition> getColumnDefinitions() throws DataAccessException {
    List<ColumnDefinition> columns = new ArrayList<>();

    try(Connection conn = dataSource.getConnection()) {
      ResultSet rs = conn.getMetaData().getColumns(null, null, tableName.toLowerCase(), "%");
      while(rs.next()) {
        ColumnDefinition columnDefinition = new ColumnDefinition();
        String columnName = rs.getString(COLUMN_NAME);
        String typeName = rs.getString(TYPE_NAME);
        columnDefinition.setName(columnName);
        ColumnType columnType = sqlTypeToColumnType(typeName);
        columnDefinition.setType(columnType);
        columns.add(columnDefinition);
      }
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }

    return columns;
  }

  /**
   * Converts a SQL type to one of the input types supported by SparkAdmin.
   * @param typeName
   * @return
   */
  protected ColumnType sqlTypeToColumnType(String typeName) {
    switch(typeName) {
      case "int4":
      case "int32":
      case "serial":
        return ColumnType.integer;

      case "tinyint":
      case "bool":
        return ColumnType.bool;

      case "datetime":
      case "timestamp":
        return ColumnType.datetime;

      case "varchar":
        return ColumnType.string;

      case "text":
      case "json":
        return ColumnType.text;

      default:
        logger.error("Unsupported column type found: {}", typeName);
        return ColumnType.string;
    }
  }
}
