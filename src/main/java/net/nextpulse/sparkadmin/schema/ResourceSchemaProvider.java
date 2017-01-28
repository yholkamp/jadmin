package net.nextpulse.sparkadmin.schema;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.dao.DataAccessException;

import java.util.List;

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
  List<ColumnDefinition> getKeyColumns() throws DataAccessException;

  /**
   * Returns the full list of columns that make up the resource.
   *
   * @return
   * @throws DataAccessException
   */
  List<ColumnDefinition> getColumnDefinitions() throws DataAccessException;
}
