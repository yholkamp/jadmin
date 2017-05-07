package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dao.AbstractDAO;
import net.nextpulse.jadmin.dsl.InputValidationRule;
import net.nextpulse.jadmin.elements.PageElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Top level configuration object for a resourceSchemaProvider that can be managed through JAdmin.
 *
 * @author yholkamp
 */
public class Resource {
  /**
   * Internal 'table' name of this resource
   */
  private final String tableName;
  /**
   * Column names to display on the index/list page.
   */
  private final List<String> indexColumns = new ArrayList<>();
  /**
   * Page elements to  display on the form (create & edit) pages.
   */
  private final List<PageElement> formPage = new ArrayList<>();
  /**
   * List of the column definitions for this resourceSchemaProvider, defining the column type and other properties.
   */
  private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
  /**
   * Data access object handling object retrieval and persistence for this Resource.
   */
  private AbstractDAO dao;

  public Resource(String tableName) {
    if(tableName == null) {
      throw new NullPointerException("tableName was null");
    }
    this.tableName = tableName;
  }

  public Set<String> getEditableColumns() {
    return columnDefinitions.stream().filter(ColumnDefinition::isEditable).map(ColumnDefinition::getName).collect(Collectors.toSet());
  }

  public String getTableName() {
    return tableName;
  }

  public List<String> getIndexColumns() {
    return indexColumns;
  }

  public List<PageElement> getFormPage() {
    return formPage;
  }

  public List<String> getPrimaryKeys() {
    return columnDefinitions.stream().filter(ColumnDefinition::isKeyColumn).map(ColumnDefinition::getName).collect(Collectors.toList());
  }

  public List<ColumnDefinition> getColumnDefinitions() {
    return columnDefinitions;
  }

  public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
    this.columnDefinitions = columnDefinitions;
  }

  public AbstractDAO getDao() {
    return dao;
  }

  public void setDao(AbstractDAO dao) {
    this.dao = dao;
  }

  /**
   * Marks the provided column as editable.
   *
   * @param name internal name of the column to mark as editable
   */
  public void addEditableColumn(String name) {
    findColumnDefinitionByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Column " + name + " could not be found on resource " + tableName))
        .setEditable(true);
  }

  /**
   * Marks the provided column as editable, configuring a validation function to run on the user input.
   *
   * @param name    name of this column
   * @param inputValidationRules  validates and/or transforms the user input for this column
   */
  public void addEditableColumn(String name, InputValidationRule... inputValidationRules) {
    findColumnDefinitionByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Column " + name + " could not be found on resource " + tableName))
        .setEditable(true)
        .addValidationRules(inputValidationRules);
  }

  /**
   * Locates the column definition identified by 'name'
   *
   * @param name internal name of the column to retrieve the column definition for
   * @return either the ColumnDefinition or an empty Optional
   */
  private Optional<ColumnDefinition> findColumnDefinitionByName(String name) {
    return columnDefinitions.stream().filter(x -> x.getName().equals(name)).findFirst();
  }
}
