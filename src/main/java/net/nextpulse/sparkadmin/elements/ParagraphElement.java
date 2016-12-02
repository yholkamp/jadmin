package net.nextpulse.sparkadmin.elements;

import lombok.Data;

@Data
public class ParagraphElement implements PageElement {

  private final String text;

  public ParagraphElement(String paragraph) {
    this.text = paragraph;
  }

  @Override
  public String getTemplateName() {
    return "paragraph.ftl";
  }
}
