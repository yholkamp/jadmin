package net.nextpulse.jadmin.schema;

import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.ColumnType;
import net.nextpulse.jadmin.dao.DataAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
  private List<ColumnDefinition> keyColumns = null;
  private List<ColumnDefinition> columnDefinitions = null;

  public GenericSQLSchemaProvider(DataSource dataSource, String tableName) {
    this.dataSource = dataSource;
    this.tableName = tableName;
  }

  @Override
  public List<ColumnDefinition> getKeyColumns() throws DataAccessException {
    if(keyColumns == null) {
      logger.trace("Retrieving key columns for {}", tableName);
      keyColumns = getColumnDefinitions().stream().filter(ColumnDefinition::isKeyColumn).collect(Collectors.toList());
    }
    return keyColumns;
  }

  @Override
  public List<ColumnDefinition> getColumnDefinitions() throws DataAccessException {
    logger.trace("Retrieving column definitions for {}", tableName);

    // only retrieve the definitions if we haven't already done so
    if(columnDefinitions == null) {
      columnDefinitions = new ArrayList<>();
      Set<String> primaryKeys = new HashSet<>();

      try(Connection conn = dataSource.getConnection()) {
        // find the primary key columns
        ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toLowerCase());
        while(primaryKeyResultSet.next()) {
          String columnName = primaryKeyResultSet.getString(COLUMN_NAME);
          primaryKeys.add(columnName);
        }

        // iterate over all columns and mark the primary key columns as such
        ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), conn.getSchema(), tableName.toLowerCase(), "%");
        while(rs.next()) {
          ColumnDefinition columnDefinition = new ColumnDefinition();
          String columnName = rs.getString(COLUMN_NAME);
          String typeName = rs.getString(TYPE_NAME).toLowerCase();
          columnDefinition.setName(columnName);
          ColumnType columnType = sqlTypeToColumnType(typeName);
          columnDefinition.setType(columnType);
          columnDefinition.setKeyColumn(primaryKeys.contains(columnName));
          
          // TODO: check the IS_NULLABLE field & set constraints accordingly
          columnDefinitions.add(columnDefinition);
        }
      } catch(SQLException e) {
        throw new DataAccessException(e);
      }
    }

    return columnDefinitions;
  }

  /**
   * Converts a SQL type to one of the input types supported by JAdmin.
   *
   * @param typeName typeName returned by the DBMS
   * @return enum value used internally for data conversion and presentation
   */
  protected ColumnType sqlTypeToColumnType(String typeName) {
    // TODO: support a wider range of types
    switch(typeName) {
      case "int":
      case "int unsigned":
      case "int4":
      case "int32":
      case "serial":
      case "integer":
      case "bigint":
      case "bigint unsigned":
        return ColumnType.integer;

      case "tinyint":
      case "bool":
      case "bit":
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
