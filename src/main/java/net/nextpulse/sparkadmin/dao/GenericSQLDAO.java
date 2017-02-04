package net.nextpulse.sparkadmin.dao;

import com.google.common.base.Joiner;
import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.FormPostEntry;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
   * @return
   * @throws DataAccessException
   */
  @Override
  public Optional<DatabaseEntry> selectOne(Object[] keys) throws DataAccessException {
    Map<String, Object> editedObject = null;
    try(Connection conn = dataSource.getConnection()) {
      String conditions = resourceSchemaProvider.getKeyColumns().stream()
          .map(x -> String.format("%s = ?", x.getName()))
          .reduce((s, s2) -> s + " AND " + s2)
          .orElseThrow(() -> new DataAccessException("Could not generate SQL condition"));

      PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s WHERE %s LIMIT 1", tableName, conditions));
      for(int i = 0; i < resourceSchemaProvider.getKeyColumns().size(); i++) {
        setValue(statement, i+1, (String)keys[i], resourceSchemaProvider.getKeyColumns().get(i));
      }
      logger.debug("Executing statement {}", statement.toString());
      ResultSet results = statement.executeQuery();

      if(results.next()) {
        // TODO: replace the BasicRowProcessor with a custom implementation
        editedObject = new BasicRowProcessor().toMap(results);
      }
    } catch(SQLException e) {
      logger.error("Exception occurred while executing");
      throw new DataAccessException(e);
    }
    return editedObject == null ? Optional.empty() : Optional.of(DatabaseEntry.buildFrom(editedObject));
  }

  /**
   *
   * @param offset  number of objects to skip
   * @param count   number of objects to retrieve
   * @return
   * @throws DataAccessException
   */
  @Override
  public List<DatabaseEntry> selectMultiple(long offset, long count) throws DataAccessException {
    // TODO: add configurable pagination
    List<DatabaseEntry> rows = new ArrayList<>();
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(String.format("SELECT * FROM %s LIMIT %d OFFSET %d", tableName, 20, 0));
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
   *
   * @param postEntry    unfiltered user submitted data, must be used with caution
   * @throws DataAccessException
   */
  @Override
  public void insert(FormPostEntry postEntry) throws DataAccessException {
    try(Connection conn = dataSource.getConnection()) {
      // construct the SQL query
      String query = createInsertStatement(postEntry);

      PreparedStatement statement = conn.prepareStatement(query);
      int index = 1;
      for(ColumnDefinition columnDefinition : postEntry.getKeyValues().keySet()) {
        setValue(statement, index++, postEntry.getKeyValues().get(columnDefinition), columnDefinition);
      }
      for(ColumnDefinition columnDefinition : postEntry.getValues().keySet()) {
        setValue(statement, index++, postEntry.getValues().get(columnDefinition), columnDefinition);
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
   *
   * @param postEntry    unfiltered user submitted data, must be used with caution
   * @throws DataAccessException
   */
  @Override
  public void update(FormPostEntry postEntry) throws DataAccessException {
    try(Connection conn = dataSource.getConnection()) {
      // construct the SQL query
      String query = createUpdateQuery(postEntry);
      logger.debug("Prepared statement SQL: {}", query);
      PreparedStatement statement = conn.prepareStatement(query);

      int index = 1;
      // first bind the SET field = ? portion
      for(ColumnDefinition columnDefinition : postEntry.getValues().keySet()) {
        setValue(statement, index++, postEntry.getValues().get(columnDefinition), columnDefinition);
      }
      // and next the WHERE field = ? part
      for(ColumnDefinition columnDefinition : postEntry.getKeyValues().keySet()) {
        setValue(statement, index++, postEntry.getKeyValues().get(columnDefinition), columnDefinition);
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
   *
   * @param postEntry
   * @return
   */
  protected String createUpdateQuery(FormPostEntry postEntry) {
    String wherePortion = postEntry.getKeyValues().keySet().stream().map(ColumnDefinition::getName)
        .map(x -> x + " = ?")
        .reduce((s, s2) -> s + " AND " + s2).orElse("");

    String setPortion = postEntry.getValues().keySet().stream().map(ColumnDefinition::getName)
        .map(x -> x + " = ?")
        .reduce((s, s2) -> s+ "," + s2).orElse("");
    return String.format("UPDATE %s SET %s WHERE %s", tableName, setPortion, wherePortion);
  }

  /**
   * Creates an unbound parameterized SQL insert statement for the provided resourceSchemaProvider type.
   * @param postEntry
   * @return
   */
  protected String createInsertStatement(FormPostEntry postEntry) {
    // obtain a list of all resource columns present in the post data
    List<String> columnSet = new ArrayList<>(postEntry.getKeyValues().keySet().stream().map(ColumnDefinition::getName).collect(Collectors.toList()));
    columnSet.addAll(postEntry.getValues().keySet().stream().map(ColumnDefinition::getName).collect(Collectors.toList()));

    String parameters = Joiner.on(",").join(Collections.nCopies(columnSet.size(), "?"));
    String parameterString = Joiner.on(",").join(columnSet);
    return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, parameterString, parameters);
  }

  /**
   * Query updater that attempts to use the most specific setX method based on the provided input.
   *
   * @param statement         statement to fill
   * @param index             index of the parameter to configure
   * @param value             user-provided value
   * @param columnDefinition  column definition, used to obtain type information
   * @throws SQLException     exception that may be thrown by {@link PreparedStatement#setObject(int, Object)} and others
   */
  protected void setValue(PreparedStatement statement, int index, String value, ColumnDefinition columnDefinition) throws DataAccessException {
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
            statement.setObject(index, String.valueOf(value));
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
