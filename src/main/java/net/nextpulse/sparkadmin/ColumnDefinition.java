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

  /**
   * True if this column is part of the key identifying a row of the datasource containing this column.
   */
  private boolean keyColumn;
  private boolean editable;

  public ColumnDefinition() {
  }

  public ColumnDefinition(String name, ColumnType type) {
    this.name = name;
    this.type = type;
  }

  public ColumnDefinition(String name, ColumnType type, boolean keyColumn, boolean editable) {
    this.name = name;
    this.type = type;
    this.keyColumn = keyColumn;
    this.editable = editable;
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

  public boolean isKeyColumn() {
    return keyColumn;
  }

  public void setKeyColumn(boolean keyColumn) {
    this.keyColumn = keyColumn;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }
}
