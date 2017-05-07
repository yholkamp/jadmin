package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dsl.InputValidator;

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
   * JAdmin-type of this column.
   */
  private ColumnType type;

  /**
   * True if this column is part of the key identifying a row of the datasource containing this column.
   */
  private boolean keyColumn;
  private boolean editable;
  private InputValidator validator;

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

  public ColumnDefinition setName(String name) {
    this.name = name;
    return this;
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

  public ColumnDefinition setKeyColumn(boolean keyColumn) {
    this.keyColumn = keyColumn;
    return this;
  }

  public boolean isEditable() {
    return editable;
  }

  public ColumnDefinition setEditable(boolean editable) {
    this.editable = editable;
    return this;
  }

  public InputValidator getValidator() {
    return validator;
  }

  public ColumnDefinition setValidator(InputValidator validator) {
    this.validator = validator;
    return this;
  }
}
