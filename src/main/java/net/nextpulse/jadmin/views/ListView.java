package net.nextpulse.jadmin.views;

import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.dao.DatabaseEntry;

import java.util.List;

/**
 * @author yholkamp
 */
public class ListView extends AbstractViewObject {
  private final Resource resource;
  private final List<String> headers;
  private final TemplateObject templateObject;

  public ListView(Resource resource, List<String> headers, TemplateObject templateObject) {
    this.resource = resource;
    this.headers = headers;
    this.templateObject = templateObject;
  }

  public Resource getResource() {
    return resource;
  }

  public List<String> getHeaders() {
    return headers;
  }

  @Override
  public TemplateObject getTemplateObject() {
    return templateObject;
  }
}
