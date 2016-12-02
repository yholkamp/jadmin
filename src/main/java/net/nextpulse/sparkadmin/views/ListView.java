package net.nextpulse.sparkadmin.views;

import lombok.Data;
import net.nextpulse.sparkadmin.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author yholkamp
 */
@Data
public class ListView extends AbstractViewObject {
  private final Resource resource;
  private final List<Map<String, Object>> rows;
  private final List<String> headers;
  private final TemplateObject templateObject;

  public ListView(Resource resource, List<Map<String, Object>> rows, List<String> headers, TemplateObject templateObject) {
    this.resource = resource;
    this.rows = rows;
    this.headers = headers;
    this.templateObject = templateObject;
  }

}
