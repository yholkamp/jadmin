package net.nextpulse.jadmin.schema;

import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.dao.DataAccessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yholkamp
 */
public interface ResourceSchemaProvider {

  /**
   * Returns the names of columns that make up the key identifying any resource instance.
   *
   * @return list of columns that make up the primary key, in the order in which they occur
   * @throws DataAccessException if an error occurs while accessing the underlying data source
   */
  default List<ColumnDefinition> getKeyColumns() throws DataAccessException {
    return getColumnDefinitions().stream().filter(ColumnDefinition::isKeyColumn).collect(Collectors.toList());
  }

  /**
   * Returns the full list of columns that make up the resource.
   *
   * @return list of column definitions
   * @throws DataAccessException if an error occurs while accessing the underlying data source
   */
  List<ColumnDefinition> getColumnDefinitions() throws DataAccessException;
}
