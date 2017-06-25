package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dsl.ColumnValueTransformer;
import net.nextpulse.jadmin.dsl.InputTransformer;
import net.nextpulse.jadmin.dsl.InputValidationRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  private List<InputValidationRule> validationRules = new ArrayList<>();
  private InputTransformer inputTransformer;
  private ColumnValueTransformer columnValueTransformer;

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

  /**
   * Adds the provided input validation rule to the list of rules.
   *
   * @param inputValidationRules  rule(s) that should be active for this column
   */
  public ColumnDefinition addValidationRules(InputValidationRule... inputValidationRules) {
    validationRules.addAll(Arrays.asList(inputValidationRules));
    return this;
  }
  
  /**
   * @return the configured validation rules
   */
  public List<InputValidationRule> getValidationRules() {
    return validationRules;
  }
  
  public InputTransformer getInputTransformer() {
    return inputTransformer;
  }
  
  public ColumnDefinition setInputTransformer(InputTransformer inputTransformer) {
    this.inputTransformer = inputTransformer;
    return this;
  }

  public ColumnValueTransformer getColumnValueTransformer() {
    return columnValueTransformer;
  }

  public ColumnDefinition setColumnValueTransformer(ColumnValueTransformer columnValueTransformer) {
    this.columnValueTransformer = columnValueTransformer;
    return this;
  }
}
