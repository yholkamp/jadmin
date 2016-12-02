package net.nextpulse.sparkadmin.elements;

import lombok.Data;
import net.nextpulse.sparkadmin.ColumnType;

@Data
public class FormInput implements PageElement {

  private ColumnType columnType;
  private String name;

  public FormInput(String name, ColumnType columnType) {
    this.name = name;
    this.columnType = columnType;
  }

  @Override
  public String getTemplateName() {
    String standard = "input.ftl";
    switch(columnType) {
      case bool:
        return "checkbox.ftl";

      case text:
        return "textfield.ftl";

      case integer:
      case varchar:
        return standard;

      default:
        System.out.println("Received " + columnType);
        return standard;
    }
  }

}
