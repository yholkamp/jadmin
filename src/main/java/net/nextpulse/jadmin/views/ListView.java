package net.nextpulse.jadmin.views;

import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.dao.DatabaseEntry;

import java.util.List;

/**
 * @author yholkamp
 */
public class ListView extends AbstractViewObject {
  private final Resource resource;
  private final List<DatabaseEntry> rows;
  private final List<String> headers;
  private final TemplateObject templateObject;
  private final int currentPage;
  private final int numberOfPages;

  public ListView(Resource resource, List<DatabaseEntry> rows, List<String> headers, TemplateObject templateObject, int currentPage, int numberOfPages) {
    this.resource = resource;
    this.rows = rows;
    this.headers = headers;
    this.templateObject = templateObject;
    this.currentPage = currentPage;
    this.numberOfPages = numberOfPages;
  }

  public Resource getResource() {
    return resource;
  }

  public List<DatabaseEntry> getRows() {
    return rows;
  }

  public List<String> getHeaders() {
    return headers;
  }

  @Override
  public TemplateObject getTemplateObject() {
    return templateObject;
  }
  
  public int getCurrentPage() {
    return currentPage;
  }
  
  public int getNumberOfPages() {
    return numberOfPages;
  }
}
