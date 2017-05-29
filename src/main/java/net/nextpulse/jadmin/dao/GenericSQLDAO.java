package net.nextpulse.jadmin.dao;

import com.google.common.base.Joiner;
import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.FormPostEntry;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * DAO implementation for resources backed by a SQL database.
 *
 * @author yholkamp
 */
public class GenericSQLDAO extends AbstractDAO {
  private static final Logger logger = LogManager.getLogger();
  private final String tableName;
  
  private DataSource dataSource;
  
  public GenericSQLDAO(DataSource dataSource, String tableName) {
    this.dataSource = dataSource;
    this.tableName = tableName;
  }
  
  /**
   * @param keys primary key(s)
   * @return either an empty optional or one holding a DatabaseEntry matching the keys
   * @throws DataAccessException if an error occurs while accessing the database.
   */
  @Override
  public Optional<DatabaseEntry> selectOne(Object[] keys) throws DataAccessException {
    logger.trace("Selecting one {}", tableName);
    Map<String, Object> editedObject = null;
    try(Connection conn = dataSource.getConnection()) {
      String conditions = resourceSchemaProvider.getKeyColumns().stream()
          .map(x -> String.format("%s = ?", x.getName()))
          .reduce((s, s2) -> s + " AND " + s2)
          .orElseThrow(() -> new DataAccessException("Could not generate SQL condition"));
      
      PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s WHERE %s LIMIT 1", tableName, conditions));
      for(int i = 1; i <= resourceSchemaProvider.getKeyColumns().size(); i++) {
        ColumnDefinition columnDefinition = resourceSchemaProvider.getKeyColumns().get(i - 1);
        setValue(statement, i, (String) keys[i - 1], columnDefinition, columnDefinition.getName());
      }
      logger.debug("Executing statement {}", statement.toString());
      ResultSet results = statement.executeQuery();
      
      if(results.next()) {
        editedObject = new BasicRowProcessor().toMap(results);
      }
    } catch(SQLException e) {
      logger.error("Exception occurred while executing");
      throw new DataAccessException(e);
    }
    return editedObject == null ? Optional.empty() : Optional.of(DatabaseEntry.buildFrom(editedObject));
  }
  
  /**
   * @param offset        number of objects to skip
   * @param count         number of objects to retrieve
   * @param sortColumn    column to sort the values by
   * @param sortDirection direction to sort, true for ascending, false for descending
   * @return list of entries of up to count long
   * @throws DataAccessException if an error occurs while accessing the database.
   */
  @Override
  public List<DatabaseEntry> selectMultiple(long offset, long count, String sortColumn, boolean sortDirection) throws DataAccessException {
    logger.trace("Selecting multiple {}, {} offset, {} count", tableName, offset, count);
    List<DatabaseEntry> rows = new ArrayList<>();
    try(Connection conn = dataSource.getConnection()) {
      // TODO: only select columns that are displayed or part of the primary key
      String sorting = sortDirection ? "asc" : "desc";
      String query = String.format("SELECT * FROM %s ORDER BY %s %s LIMIT %d OFFSET %d", tableName, sortColumn, sorting, count, offset);
      logger.trace("Formatted selectMultiple query: {}", query);
      PreparedStatement statement = conn.prepareStatement(query);
      ResultSet results = statement.executeQuery();
      while(results.next()) {
        Map<String, Object> row = new BasicRowProcessor().toMap(results);
        rows.add(DatabaseEntry.buildFrom(row));
      }
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }
    return rows;
  }
  
  /**
   * @param postEntry unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurs while accessing the database.
   */
  @Override
  public void insert(FormPostEntry postEntry) throws DataAccessException {
    logger.trace("Inserting a new {}", tableName);
    try(Connection conn = dataSource.getConnection()) {
      // construct the SQL query
      String query = createInsertStatement(postEntry);
      
      PreparedStatement statement = conn.prepareStatement(query);
      int index = 1;
      for(String columnName : postEntry.getKeyValues().keySet()) {
        setValue(statement, index++, postEntry.getKeyValues().get(columnName), getColumnDefinitions().get(columnName), columnName);
      }
      for(String columnName : postEntry.getValues().keySet()) {
        setValue(statement, index++, postEntry.getValues().get(columnName), getColumnDefinitions().get(columnName), columnName);
      }
      
      logger.debug("Prepared statement SQL: {}", query);
      int updatedRows = statement.executeUpdate();
      if(updatedRows != 1) {
        throw new SQLException("Updated " + updatedRows + ", expected 1");
      }
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }
  }
  
  /**
   * @param postEntry unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurs while accessing the database.
   */
  @Override
  public void update(FormPostEntry postEntry) throws DataAccessException {
    logger.trace("Updating an existing {}", tableName);
    try(Connection conn = dataSource.getConnection()) {
      // construct the SQL query
      String query = createUpdateQuery(postEntry);
      logger.debug("Prepared statement SQL: {}", query);
      PreparedStatement statement = conn.prepareStatement(query);
      
      int index = 1;
      // first bind the SET field = ? portion
      for(String columnName : postEntry.getValues().keySet()) {
        setValue(statement, index++, postEntry.getValues().get(columnName), getColumnDefinitions().get(columnName), columnName);
      }
      // and next the WHERE field = ? part
      for(String columnName : postEntry.getKeyValues().keySet()) {
        setValue(statement, index++, postEntry.getKeyValues().get(columnName), getColumnDefinitions().get(columnName), columnName);
      }
      logger.debug("Query: {}", statement.toString());
      int updatedRows = statement.executeUpdate();
      if(updatedRows != 1) {
        throw new SQLException("Updated " + updatedRows + ", expected 1");
      }
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }
  }
  
  /**
   * Returns the number of entries in the database of the resource.
   *
   * @return number of entries
   * @throws DataAccessException if an SQL exception occurred
   */
  @Override
  public int count() throws DataAccessException {
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(String.format("SELECT COUNT(*) FROM %s", tableName));
      ResultSet results = statement.executeQuery();
      results.next();
      return results.getInt(1);
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }
  }
  
  
  @Override
  public void delete(Object... keys) throws DataAccessException {
    logger.trace("Updating an existing {}", tableName);
    try(Connection conn = dataSource.getConnection()) {
      // construct the SQL query
      String conditions = resourceSchemaProvider.getKeyColumns().stream()
          .map(x -> String.format("%s = ?", x.getName()))
          .reduce((s, s2) -> s + " AND " + s2)
          .orElseThrow(() -> new DataAccessException("Could not generate SQL condition"));
      
      PreparedStatement statement = conn.prepareStatement(String.format("DELETE FROM %s WHERE %s", tableName, conditions));
      for(int i = 1; i <= resourceSchemaProvider.getKeyColumns().size(); i++) {
        ColumnDefinition columnDefinition = resourceSchemaProvider.getKeyColumns().get(i - 1);
        setValue(statement, i, (String) keys[i - 1], columnDefinition, columnDefinition.getName());
      }
      logger.debug("Executing statement {}", statement.toString());
      boolean results = statement.execute();
      
    } catch(SQLException e) {
      throw new DataAccessException(e);
    }
  }
  
  /**
   * Creates an SQL update query for the provided postEntry.
   *
   * @param postEntry object to construct the update query for
   * @return update query with unbound parameters
   */
  protected String createUpdateQuery(FormPostEntry postEntry) {
    String wherePortion = postEntry.getKeyValues().keySet().stream()
        .map(x -> x + " = ?")
        .reduce((s, s2) -> s + " AND " + s2).orElse("");
    
    String setPortion = postEntry.getValues().keySet().stream()
        .map(x -> x + " = ?")
        .reduce((s, s2) -> s + "," + s2).orElse("");
    return String.format("UPDATE %s SET %s WHERE %s", tableName, setPortion, wherePortion);
  }
  
  /**
   * Creates an SQL insert query for the provided postEntry.
   *
   * @param postEntry object to construct the insert query for
   * @return insert query with unbound parameters
   */
  protected String createInsertStatement(FormPostEntry postEntry) {
    // obtain a list of all resource columns present in the post data
    List<String> columnSet = new ArrayList<>(postEntry.getKeyValues().keySet());
    columnSet.addAll(postEntry.getValues().keySet());
    
    String parameters = Joiner.on(",").join(Collections.nCopies(columnSet.size(), "?"));
    String parameterString = Joiner.on(",").join(columnSet);
    return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, parameterString, parameters);
  }
  
  /**
   * Query updater that attempts to use the most specific setX method based on the provided input.
   *
   * @param statement        statement to fill
   * @param index            index of the parameter to configure
   * @param value            user-provided value
   * @param columnDefinition column definition, used to obtain type information
   * @param columnName       name of the column being set
   * @throws DataAccessException exception that may be thrown by {@link PreparedStatement#setObject(int, Object)} and others
   */
  protected void setValue(PreparedStatement statement, int index, String value, ColumnDefinition columnDefinition, String columnName) throws DataAccessException {
    if(columnDefinition == null) {
      throw new DataAccessException("Found no column definition for column " + columnName + ", value " + value);
    }
    try {
      if(StringUtils.isEmpty(value)) {
        // TODO: use setNull here
        logger.trace("Setting null for column {}", columnDefinition.getName());
        statement.setObject(index, null);
      } else {
        switch(columnDefinition.getType()) {
          case integer:
            statement.setInt(index, Integer.valueOf(value));
            break;
          case bool:
            statement.setBoolean(index, Boolean.valueOf(value));
            break;
          case datetime:
            // TODO: handle input-to-date conversion
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
              Date date = format.parse(value);
              statement.setDate(index, new java.sql.Date(date.getTime()));
            } catch(ParseException e) {
              logger.error("Could not parse the provided datetime string: {}", value, e);
            }
            break;
          case string:
          case text:
            statement.setString(index, value);
            break;
          default:
            logger.error("Unsupported column definition type {} found, setting without type checking", columnDefinition.getType());
            statement.setObject(index, value);
            break;
        }
      }
    } catch(SQLException e) {
      logger.error("Could not set {}.{} (type {}) to {}", tableName, columnDefinition.getName(), columnDefinition.getType(), value);
      throw new DataAccessException(e);
    }
  }
}
