package net.nextpulse.sparkadmin;

import lombok.Data;

/**
 * Object describing a single column of a table.
 *
 * @author yholkamp
 */
@Data
public class ColumnDefinition {

  /**
   * Column name, case sensitive
   */
  private String name;
  /**
   * SparkAdmin-type of this column.
   */
  private ColumnType type;

  public void setStringType(String type) {
    this.type = ColumnType.get(type);
  }

}
