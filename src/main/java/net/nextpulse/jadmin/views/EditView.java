package net.nextpulse.jadmin.views;

import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.dao.DatabaseEntry;

/**
 * @author yholkamp
 */
public class EditView extends AbstractViewObject {

  private final DatabaseEntry object;
  private final Resource resource;

  public EditView(Resource resource, DatabaseEntry object, TemplateObject templateObject) {
    this.resource = resource;
    this.object = object;
    this.templateObject = templateObject;
  }

  public DatabaseEntry getObject() {
    return object;
  }

  public Resource getResource() {
    return resource;
  }
}
