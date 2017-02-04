package net.nextpulse.sparkadmin;

import net.nextpulse.sparkadmin.dao.AbstractDAO;
import net.nextpulse.sparkadmin.elements.PageElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Top level configuration object for a resourceSchemaProvider that can be managed through SparkAdmin.
 *
 * @author yholkamp
 */
public class Resource {
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
   * @param name
   */
  public void addEditableColumn(String name) {
    findColumnDefinitionByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Column " + name + " could not be found on resource " + tableName))
        .setEditable(true);
  }

  /**
   * Locates the column definition identified by 'name'
   *
   * @param name
   * @return
   */
  private Optional<ColumnDefinition> findColumnDefinitionByName(String name) {
    return columnDefinitions.stream().filter(x -> x.getName().equals(name)).findFirst();
  }
}
