package net.nextpulse.sparkadmin;

import lombok.Data;
import net.nextpulse.sparkadmin.elements.PageElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yholkamp
 */
@Data
public class Resource {
  private final String tableName;
  private final List<String> indexColumns = new ArrayList<>();
  private final List<PageElement> formPage = new ArrayList<>();
  private List<String> primaryKeys = new ArrayList<>();
  private List<ColumnDefinition> columnDefinitions = new ArrayList<>();
  private Set<String> editableColumns = new HashSet<>();

  public Resource(String tableName) {
    if(tableName == null) {
      throw new NullPointerException("tableName was null");
    }
    this.tableName = tableName;
  }


  public Set<String> getEditableColumns() {
    return editableColumns;
  }
}
