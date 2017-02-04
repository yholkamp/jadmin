package net.nextpulse.sparkadmin;

/**
 * Object describing a single column of a table.
 *
 * @author yholkamp
 */
public class ColumnDefinition {

  /**
   * Column name, case sensitive
   */
  private String name;
  /**
   * SparkAdmin-type of this column.
   */
  private ColumnType type;

  public ColumnDefinition() {
  }

  public ColumnDefinition(String name, ColumnType type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ColumnType getType() {
    return type;
  }

  public void setType(ColumnType type) {
    this.type = type;
  }

}
