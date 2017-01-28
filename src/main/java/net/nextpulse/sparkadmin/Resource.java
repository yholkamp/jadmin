package net.nextpulse.sparkadmin;

import net.nextpulse.sparkadmin.dao.AbstractDAO;
import net.nextpulse.sparkadmin.elements.PageElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
   * Column names to include on the edit page.
   */
  private Set<String> editableColumns = new HashSet<>();
  /**
   * Column names of the primary keys
   */
  private List<String> primaryKeys = new ArrayList<>();
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
    return editableColumns;
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
    return primaryKeys;
  }

  public void setPrimaryKeys(List<String> primaryKeys) {
    this.primaryKeys = primaryKeys;
  }

  public List<ColumnDefinition> getColumnDefinitions() {
    return columnDefinitions;
  }

  public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
    this.columnDefinitions = columnDefinitions;
  }

  public void setEditableColumns(Set<String> editableColumns) {
    this.editableColumns = editableColumns;
  }

  public AbstractDAO getDao() {
    return dao;
  }

  public void setDao(AbstractDAO dao) {
    this.dao = dao;
  }
}
