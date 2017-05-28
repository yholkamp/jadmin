package net.nextpulse.jadmin.dao;

import com.google.common.collect.ComparisonChain;
import net.nextpulse.jadmin.FormPostEntry;

import java.util.*;
import java.util.stream.Collectors;

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
   * @param offset        number of objects to skip
   * @param count         number of objects to retrieve
   * @param sortColumn    column to sort by
   * @param sortDirection direction to sort by, true for ascending, false for descending
   * @return list of results
   * @throws DataAccessException if an error occurred while retrieving the objects
   */
  @Override
  public List<DatabaseEntry> selectMultiple(long offset, long count, String sortColumn, boolean sortDirection) throws DataAccessException {
    if(offset >= objects.size()) {
      return Collections.emptyList();
    } else {
      int direction = sortDirection ? 1 : -1;
      return objects.values().stream().sorted((o1, o2) -> genericCompare(o1, o2) * direction).skip(offset).limit(count).collect(Collectors.toList());
    }
  }
  
  /**
   * Generic compare function for two objects of unknown types. Returns a negative integer, zero, or a positive integer 
   * as the first object is less than, equal to, or greater than the second object. 
   * 
   * @param o1  first object to compare
   * @param o2  second object to compare
   * @return a negative integer, zero, or a positive integer as o1 is less than, equal to, or greater than the o2 object.
   */
  protected static int genericCompare(Object o1, Object o2) {
    if(o1 == o2) {
      return 0;
    } else if(o1 == null) {
      return -1;
    } else if(o2 == null) {
      return 1;
    } else if(o1 instanceof Comparable && o2 instanceof Comparable && o1.getClass().equals(o2.getClass())) {
      return ((Comparable)o1).compareTo(o2);
    } else {
      // default to simply sorting based on the object's class name as a fall back
      return o1.getClass().getName().compareTo(o2.getClass().getName());
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
   * @return number of objects
   * @throws DataAccessException never
   */
  @Override
  public int count() throws DataAccessException {
    return objects.size();
  }
  
  /**
   * Deletes a single DatabaseEntry using the primary key(s) of the resourceSchemaProvider.
   *
   * @param keys primary key(s)
   * @throws DataAccessException if an error occurred while deleting the object
   */
  @Override
  public void delete(Object... keys) throws DataAccessException {
    DatabaseEntryKey key = new DatabaseEntryKey(keys);
    DatabaseEntry entry = objects.get(key);
    if(entry != null) {
      objects.remove(key);
    } else {
      throw new DataAccessException("Could not access object identified by " + key);
    }
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
