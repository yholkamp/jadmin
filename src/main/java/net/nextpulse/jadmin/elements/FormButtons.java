package net.nextpulse.jadmin.elements;

/**
 * Form submit & cancel links to include in a form.
 */
public class FormButtons implements PageElement {

  @Override
  public String getTemplateName() {
    return "buttons.ftl";
  }
}
