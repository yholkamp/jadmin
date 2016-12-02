package net.nextpulse.sparkadmin.views;

import lombok.Data;

import java.util.List;

/**
 * @author yholkamp
 */
@Data
public class TemplateObject {

  String prefix;
  List<String> tables;
  String currentTable;

  public TemplateObject(String prefix, List<String> tables, String currentTable) {
    this.prefix = prefix;
    this.tables = tables;
    this.currentTable = currentTable;
  }

}
