package net.nextpulse.sparkadmin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author yholkamp
 */
public enum ColumnType {
  varchar,
  text,
  integer("serial", "int", "int4"),
  bool("tinyint"),
  datetime("timestamp");

  // TODO: consider creating specialized converter for different DBMS instead of using aliases
  private final List<String> aliases;

  ColumnType(String... aliases) {
    this.aliases = Arrays.asList(aliases);
  }

  protected static Map<String, ColumnType> typeLookup = new HashMap<>();
  static {
    for(ColumnType columnType : values()) {
      typeLookup.put(columnType.name(), columnType);
      for(String alias : columnType.aliases) {
        typeLookup.put(alias, columnType);
      }
    }
  }

  public static ColumnType get(String alias) {
    if(!typeLookup.containsKey(alias.toLowerCase())) {
      throw new IllegalArgumentException("Unsupported column type " + alias);
    }
    return typeLookup.get(alias.toLowerCase());
  }
}
