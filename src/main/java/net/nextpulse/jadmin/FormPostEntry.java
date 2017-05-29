package net.nextpulse.jadmin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Object that holds the user submitted form values.
 *
 * @author yholkamp
 */
public class FormPostEntry {
  
  /**
   * Key value(s) for this post entry, i.e. the object id, non-editable values.
   */
  private LinkedHashMap<String, String> keyValues = new LinkedHashMap<>();
  /**
   * Editable values of a resource, i.e. an editable name field.
   */
  private LinkedHashMap<String, String> values = new LinkedHashMap<>();
  
  public FormPostEntry() {
  }
  
  /**
   * @return map of unescaped post values
   */
  public LinkedHashMap<String, String> getValues() {
    return values;
  }
  
  public void addValue(String columnName, String value) {
    values.put(columnName, value);
  }
  
  /**
   * @return map of unescaped values that make up the identifier of this row
   */
  public LinkedHashMap<String, String> getKeyValues() {
    return keyValues;
  }
  
  /**
   * Adds a new user supplied value for the specified column.
   *
   * @param columnName column name
   * @param value      user provided value
   */
  public void addKeyValue(String columnName, String value) {
    keyValues.put(columnName, value);
  }
  
  /**
   * Returns a Map with column keys linked to the submitted values.
   *
   * @return this entry as map of column name to value
   */
  public Map<String, String> toPropertiesMap() {
    LinkedHashMap<String, String> output = new LinkedHashMap<>();
    output.putAll(keyValues);
    output.putAll(values);
    return output;
  }
}
