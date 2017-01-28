package net.nextpulse.sparkadmin.views;

import java.util.List;

/**
 * @author yholkamp
 */
public class TemplateObject {

  String prefix;
  List<String> tables;
  String currentTable;

  public TemplateObject(String prefix, List<String> tables, String currentTable) {
    this.prefix = prefix;
    this.tables = tables;
    this.currentTable = currentTable;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public List<String> getTables() {
    return tables;
  }

  public void setTables(List<String> tables) {
    this.tables = tables;
  }

  public String getCurrentTable() {
    return currentTable;
  }

  public void setCurrentTable(String currentTable) {
    this.currentTable = currentTable;
  }
}
