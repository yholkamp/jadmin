package net.nextpulse.jadmin.elements;

/**
 * Parent interface for all page elements, providing methods used when rendering pages.
 */
public interface PageElement {

  /**
   * Returns the template name to use for this type of PageElement.
   * <p>
   * Warnings are suppressed as this method is only called dynamically by the template engine.
   *
   * @return filename of the template to use
   */
  @SuppressWarnings("unused")
  String getTemplateName();
}
