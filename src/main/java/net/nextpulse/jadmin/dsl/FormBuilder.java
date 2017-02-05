package net.nextpulse.jadmin.dsl;

import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.elements.FormButtons;
import net.nextpulse.jadmin.elements.FormInputGroup;
import net.nextpulse.jadmin.elements.ParagraphElement;

import java.util.function.Consumer;

/**
 * DSL class that offers top level access to the form configuration of a Resource. Use this class to add input groups
 * and more to the form shown when creating or editing instances of this resource.
 *
 * @author yholkamp
 */
public class FormBuilder {

  private final Resource resource;

  public FormBuilder(Resource resource) {
    this.resource = resource;
  }

  public FormBuilder inputGroup(String header, Consumer<InputGroupBuilder> consumer) {
    FormInputGroup inputGroup = new FormInputGroup();
    inputGroup.setHeader(header);
    resource.getFormPage().add(inputGroup);
    InputGroupBuilder builder = new InputGroupBuilder(inputGroup, resource);
    consumer.accept(builder);
    return this;
  }

  public FormBuilder paragraph(String content) {
    resource.getFormPage().add(new ParagraphElement(content));
    return this;
  }

  public FormBuilder actions() {
    resource.getFormPage().add(new FormButtons());
    return this;
  }
}
