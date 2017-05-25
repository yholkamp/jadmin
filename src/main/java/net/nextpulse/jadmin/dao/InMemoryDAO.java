package net.nextpulse.jadmin.dao;

import net.nextpulse.jadmin.FormPostEntry;

import java.util.*;

/**
 * Example DAO that provides access to an in-memory representation of objects, which are kept in insertion order.
 * May be used for testing purposes or as an example implementation.
 *
 * @author yholkamp
 */
public class InMemoryDAO extends AbstractDAO {

  private LinkedHashMap<DatabaseEntryKey, DatabaseEntry> objects = new LinkedHashMap<>();

  /**
   * Retrieves a single DatabaseEntry using the primary key(s) of the resourceSchemaProvider.
   *
   * @param keys primary key(s)
   * @return either an empty optional object or the object represented by the provided keys.
   * @throws DataAccessException if an error occurred while retrieving the object or the provided keys are invalid.
   */
  @Override
  public Optional<DatabaseEntry> selectOne(Object... keys) throws DataAccessException {
    DatabaseEntryKey key = new DatabaseEntryKey(keys);
    return Optional.ofNullable(objects.get(key));
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
    if(offset >= objects.size()) {
      return Collections.emptyList();
    } else {
      int endIndex = Math.min((int) (offset + count), objects.size());
      return new ArrayList<>(objects.values()).subList((int) offset, endIndex);
    }
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
    DatabaseEntryKey key = new DatabaseEntryKey(postData.getKeyValues().values());
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
    DatabaseEntryKey key = new DatabaseEntryKey(postData.getKeyValues().values());
    DatabaseEntry entry = objects.get(key);
    if(entry != null) {
      entry.getProperties().clear();
      postData.getKeyValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
      postData.getValues().forEach((def, value) -> entry.getProperties().put(def.getName(), value));
    } else {
      throw new DataAccessException("Could not access object identified by " + key);
    }
  }
  
  /**
   * Returns the number of stored objects.
   * 
   * @return  number of objects
   * @throws DataAccessException never
   */
  @Override
  public int count() throws DataAccessException {
    return objects.size();
  }
  
  /**
   * Internal representation of the object key.
   */
  private class DatabaseEntryKey {
    List<Object> keyValues;


    public DatabaseEntryKey(Collection<String> keys) {
      keyValues = new ArrayList<>(keys);
    }

    public DatabaseEntryKey(Object[] keys) {
      keyValues = Arrays.asList(keys);
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      DatabaseEntryKey that = (DatabaseEntryKey) o;
      return Objects.equals(keyValues, that.keyValues);
    }

    @Override
    public int hashCode() {
      return Objects.hash(keyValues);
    }
  }

}
