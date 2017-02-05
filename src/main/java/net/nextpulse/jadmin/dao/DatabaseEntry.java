package net.nextpulse.jadmin.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a single database entry.
 *
 * @author yholkamp
 */
public class DatabaseEntry {

  private Map<String, Object> properties = new HashMap<>();

  public Map<String, Object> getProperties() {
    return properties;
  }

  /**
   * Constructs a new entry using the provided map.
   * @param editedObject  map of column names and values
   * @return  new DatabaseEntry object
   */
  public static DatabaseEntry buildFrom(Map<String, Object> editedObject) {
    DatabaseEntry entry = new DatabaseEntry();
    entry.properties = editedObject;
    return entry;
  }

  /**
   * Constructs an empty object.
   * @return
   */
  public static DatabaseEntry buildEmpty() {
    return new DatabaseEntry();
  }
}
