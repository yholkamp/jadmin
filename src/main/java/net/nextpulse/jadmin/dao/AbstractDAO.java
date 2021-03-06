package net.nextpulse.jadmin.dao;

import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.FormPostEntry;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract parent class for a DAO class backing a specific Resource. The DAO class handles the retrieval
 * and updating of the resourceSchemaProvider instances.
 *
 * @author yholkamp
 */
public abstract class AbstractDAO {
  
  /**
   * Resource managed by this DAO instance.
   */
  protected ResourceSchemaProvider resourceSchemaProvider;
  private Map<String, ColumnDefinition> columnDefinitionMap;
  
  /**
   * Initializer invoked before any other methods are called by the application.
   *
   * @param resourceSchemaProvider class that provides schema information about this resource.
   */
  public void initialize(ResourceSchemaProvider resourceSchemaProvider) {
    this.resourceSchemaProvider = resourceSchemaProvider;
  }
  
  /**
   * Retrieves a single DatabaseEntry using the primary key(s) of the resourceSchemaProvider.
   *
   * @param keys primary key(s)
   * @return either an empty optional object or the object represented by the provided keys.
   * @throws DataAccessException if an error occurred while retrieving the object or the provided keys are invalid.
   */
  public abstract Optional<DatabaseEntry> selectOne(Object... keys) throws DataAccessException;
  
  /**
   * Retrieves multiple DatabaseEntry objects from the data store, optionally sorting by the provided column and in the provided direction.
   *
   * @param offset        number of objects to skip
   * @param count         number of objects to retrieve
   * @param sortColumn    name of the column to sort by, may not be implemented
   * @param sortDirection false for ascending, true for descending, may not be implemented
   * @return list of results
   * @throws DataAccessException if an error occurred while retrieving the objects
   */
  public abstract List<DatabaseEntry> selectMultiple(long offset, long count, String sortColumn, boolean sortDirection) throws DataAccessException;
  
  /**
   * Inserts a single resourceSchemaProvider instance in to the database, using the unfiltered client submitted data.
   *
   * @param postData unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurred while inserting the object
   */
  public abstract void insert(FormPostEntry postData) throws DataAccessException;
  
  /**
   * Updates a single resourceSchemaProvider instance in the database using the unfiltered client submitted data.
   *
   * @param postData unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurred while inserting the object
   */
  public abstract void update(FormPostEntry postData) throws DataAccessException;
  
  /**
   * Deletes a single DatabaseEntry using the primary key(s) of the resourceSchemaProvider.
   *
   * @param keys primary key(s)
   * @throws DataAccessException if an error occurred while deleting the object or the provided keys are invalid.
   */
  public abstract void delete(Object... keys) throws DataAccessException;
  
  /**
   * Returns (possibly an estimate of) the total number of entries for this particular resource.
   *
   * @return
   * @throws DataAccessException
   */
  public abstract int count() throws DataAccessException;
  
  /**
   * @return mapping of string to column definition
   * @throws DataAccessException if the column definitions could not be retrieved
   */
  public Map<String, ColumnDefinition> getColumnDefinitions() throws DataAccessException {
    if(columnDefinitionMap == null && resourceSchemaProvider == null) {
      return Collections.emptyMap();
    } else if(columnDefinitionMap == null) {
      columnDefinitionMap = resourceSchemaProvider.getColumnDefinitions().stream()
          .collect(Collectors.toMap(ColumnDefinition::getName, Function.identity()));
    }
    
    return columnDefinitionMap;
  }
}
