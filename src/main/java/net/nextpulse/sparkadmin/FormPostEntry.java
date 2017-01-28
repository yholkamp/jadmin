package net.nextpulse.sparkadmin;

import java.util.LinkedHashMap;

/**
 * @author yholkamp
 */
public class FormPostEntry {

  /**
   * Key value(s) for this post entry, i.e. the object id.
   */
  private LinkedHashMap<ColumnDefinition, String> keyValues = new LinkedHashMap<>();
  /**
   * Editable values of a resource, i.e. an editable name field.
   */
  private LinkedHashMap<ColumnDefinition, String> values = new LinkedHashMap<>();

  /**
   * @return map of unescaped post values
   */
  public LinkedHashMap<ColumnDefinition, String> getValues() {
    return values;
  }

  public void addValue(ColumnDefinition def, String value) {
    values.put(def, value);
  }

  public LinkedHashMap<ColumnDefinition, String> getKeyValues() {
    return keyValues;
  }

  public void addKeyValue(ColumnDefinition def, String value) {
    keyValues.put(def, value);
  }

  public int entryCount() {
    return keyValues.size() + values.size();
  }
}
