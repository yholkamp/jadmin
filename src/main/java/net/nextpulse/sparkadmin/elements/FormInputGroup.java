package net.nextpulse.sparkadmin.elements;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of input fields
 */
@Data
public class FormInputGroup implements PageElement  {

  private List<FormInput> inputs = new ArrayList<>();
  private String header;

  @Override
  public String getTemplateName() {
    return "group.ftl";
  }

}
