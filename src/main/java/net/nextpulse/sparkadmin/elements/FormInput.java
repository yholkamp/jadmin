package net.nextpulse.sparkadmin.elements;

import net.nextpulse.sparkadmin.ColumnType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormInput implements PageElement {
  private static final Logger logger = LogManager.getLogger();

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

      case datetime:
        return "datetime.ftl";

      case integer:
      case string:
        return standard;


      default:
        logger.warn("Received unsupported column type {}, defaulting to text input", columnType);
        return standard;
    }
  }

  public static Logger getLogger() {
    return logger;
  }

  public ColumnType getColumnType() {
    return columnType;
  }

  public void setColumnType(ColumnType columnType) {
    this.columnType = columnType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
