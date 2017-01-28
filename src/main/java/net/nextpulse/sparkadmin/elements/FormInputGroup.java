package net.nextpulse.sparkadmin.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of input fields
 */
public class FormInputGroup implements PageElement  {

  private List<FormInput> inputs = new ArrayList<>();
  private String header;

  @Override
  public String getTemplateName() {
    return "group.ftl";
  }

  public List<FormInput> getInputs() {
    return inputs;
  }

  public void setInputs(List<FormInput> inputs) {
    this.inputs = inputs;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
