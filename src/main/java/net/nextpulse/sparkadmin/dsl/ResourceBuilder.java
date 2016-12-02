package net.nextpulse.sparkadmin.dsl;

import net.nextpulse.sparkadmin.Resource;

import java.util.function.Consumer;

/**
 *
 *
 * @author yholkamp
 */
public class ResourceBuilder {

  private final Resource resource;

  public ResourceBuilder(Resource resource) {
    this.resource = resource;
  }

  /**
   * Configures the form/edit page generated for the last created resource.
   * @param builderConsumer
   * @return
   */
  public ResourceBuilder formConfig(Consumer<FormBuilder> builderConsumer) {
    FormBuilder builder = new FormBuilder(resource);
    resource.getFormPage().clear();
    resource.getEditableColumns().clear();
    builderConsumer.accept(builder);
    return this;
  }

  /**
   * Configures the index page generated for the last created resource.
   * @param builderConsumer
   * @return
   */
  public ResourceBuilder indexConfig(Consumer<IndexBuilder> builderConsumer) {
    IndexBuilder builder = new IndexBuilder(resource);
    resource.getIndexColumns().clear();
    builderConsumer.accept(builder);
    return this;
  }

}
