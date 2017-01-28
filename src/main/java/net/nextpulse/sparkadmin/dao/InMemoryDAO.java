package net.nextpulse.sparkadmin.dao;

import net.nextpulse.sparkadmin.FormPostEntry;

import java.util.*;

/**
 * Example DAO that provides access to an in-memory representation of objects. May be used for testing purposes or as
 * an example implementation.
 *
 * @author yholkamp
 */
public class InMemoryDAO extends AbstractDAO {

  private Map<Object[], DatabaseEntry> objects = new HashMap<>();

  /**
   * Retrieves a single DatabaseEntry using the primary key(s) of the resourceSchemaProvider.
   *
   * @param keys primary key(s)
   * @return either an empty optional object or the object represented by the provided keys.
   * @throws DataAccessException if an error occurred while retrieving the object or the provided keys are invalid.
   */
  @Override
  public Optional<DatabaseEntry> selectOne(Object... keys) throws DataAccessException {
    return Optional.ofNullable(objects.get(keys));
  }

  /**
   * Retrieves multiple DatabaseEntry objects from the data store.
   *
   * @param offset number of objects to skip
   * @param count  number of objects to retrieve
   * @return list of results
   * @throws DataAccessException if an error occurred while retrieving the objects
   */
  @Override
  public List<DatabaseEntry> selectMultiple(long offset, long count) throws DataAccessException {
    return new ArrayList<>(objects.values());
  }

  /**
   * Inserts a single resourceSchemaProvider instance in to the database, using the unfiltered client submitted data.
   *
   * @param postData unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurred while inserting the object
   */
  @Override
  public void insert(FormPostEntry postData) throws DataAccessException {
    DatabaseEntry entry = new DatabaseEntry();
    postData.getKeyValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
    postData.getValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
    Object[] key = postData.getKeyValues().values().toArray();
    objects.put(key, entry);
  }

  /**
   * Updates a single resourceSchemaProvider instance in the database using the unfiltered client submitted data.
   *
   * @param postData unfiltered user submitted data, must be used with caution
   * @throws DataAccessException if an error occurred while inserting the object
   */
  @Override
  public void update(FormPostEntry postData) throws DataAccessException {
    Object[] key = postData.getKeyValues().values().toArray();
    DatabaseEntry entry = objects.get(key);
    if(entry != null) {
      entry.getProperties().clear();
      postData.getKeyValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
      postData.getValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
    } else {
      throw new DataAccessException("Could not access object identified by " + key);
    }
  }

}
