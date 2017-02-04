package net.nextpulse.sparkadmin.elements;

/**
 * Element containing a block of text.
 */
public class ParagraphElement implements PageElement {

  private final String text;

  public ParagraphElement(String paragraph) {
    this.text = paragraph;
  }

  @Override
  public String getTemplateName() {
    return "paragraph.ftl";
  }

  public String getText() {
    return text;
  }
}
