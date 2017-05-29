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
  private LinkedHashMap<ColumnDefinition, String> keyValues = new LinkedHashMap<>();
  /**
   * Editable values of a resource, i.e. an editable name field.
   */
  private LinkedHashMap<ColumnDefinition, String> values = new LinkedHashMap<>();
  
  public FormPostEntry() {
  }
  
  /**
   * @return map of unescaped post values
   */
  public LinkedHashMap<ColumnDefinition, String> getValues() {
    return values;
  }
  
  public void addValue(ColumnDefinition def, String value) {
    values.put(def, value);
  }
  
  /**
   * @return map of unescaped values that make up the identifier of this row
   */
  public LinkedHashMap<ColumnDefinition, String> getKeyValues() {
    return keyValues;
  }
  
  /**
   * Adds a new user supplied value for the specified column.
   *
   * @param def   column definition
   * @param value user provided value
   */
  public void addKeyValue(ColumnDefinition def, String value) {
    keyValues.put(def, value);
  }
  
  /**
   * Returns a Map with column keys linked to the submitted values.
   *
   * @return this entry as map of column name to value
   */
  public Map<String, String> toPropertiesMap() {
    LinkedHashMap<String, String> output = new LinkedHashMap<>();
    keyValues.forEach((def, value) -> output.put(def.getName(), value));
    values.forEach((def, value) -> output.put(def.getName(), value));
    return output;
  }
}
