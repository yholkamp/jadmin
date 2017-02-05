package net.nextpulse.jadmin.dsl;

import net.nextpulse.jadmin.Resource;

import java.util.function.Consumer;

/**
 * DSL class that provides a fluent API to configure a Resource object.
 *
 * @author yholkamp
 */
public class ResourceBuilder {

  private final Resource resource;

  public ResourceBuilder(Resource resource) {
    this.resource = resource;
  }

  /**
   * Configures the form/edit page generated for the last created resourceSchemaProvider.
   *
   * @param builderConsumer function that will be called for further configuration
   * @return this instance
   */
  public ResourceBuilder formConfig(Consumer<FormBuilder> builderConsumer) {
    FormBuilder builder = new FormBuilder(resource);
    resource.getFormPage().clear();
    resource.getEditableColumns().clear();
    builderConsumer.accept(builder);
    return this;
  }

  /**
   * Configures the index page generated for the last created resourceSchemaProvider.
   *
   * @param builderConsumer function that will be called for further configuration
   * @return this instance
   */
  public ResourceBuilder indexConfig(Consumer<IndexBuilder> builderConsumer) {
    IndexBuilder builder = new IndexBuilder(resource);
    resource.getIndexColumns().clear();
    builderConsumer.accept(builder);
    return this;
  }

}
