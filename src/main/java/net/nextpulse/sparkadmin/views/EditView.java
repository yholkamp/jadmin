package net.nextpulse.sparkadmin.views;

import lombok.Data;
import net.nextpulse.sparkadmin.Resource;

import java.util.Map;

/**
 * @author yholkamp
 */
@Data
public class EditView extends AbstractViewObject {

  private final Map<String, Object> object;
  private final Resource resource;

  public EditView(Resource resource, Map<String, Object> object, TemplateObject templateObject) {
    this.resource = resource;
    this.object = object;
    this.templateObject = templateObject;
  }

}
