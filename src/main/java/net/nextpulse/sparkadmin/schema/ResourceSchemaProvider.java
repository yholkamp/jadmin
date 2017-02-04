package net.nextpulse.sparkadmin.schema;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.dao.DataAccessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yholkamp
 */
public interface ResourceSchemaProvider {

  /**
   * Returns the names of columns that make up the key identifying any resource instance.
   *
   * @return
   * @throws DataAccessException
   */
  default List<ColumnDefinition> getKeyColumns() throws DataAccessException {
    return getColumnDefinitions().stream().filter(ColumnDefinition::isKeyColumn).collect(Collectors.toList());
  }

  /**
   * Returns the full list of columns that make up the resource.
   *
   * @return
   * @throws DataAccessException
   */
  List<ColumnDefinition> getColumnDefinitions() throws DataAccessException;
}
