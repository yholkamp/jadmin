package net.nextpulse.sparkadmin.dsl;

import net.nextpulse.sparkadmin.Resource;

/**
 * @author yholkamp
 */
public class IndexBuilder {
  private final Resource resource;

  public IndexBuilder(Resource resource) {
    this.resource = resource;
  }

  /**
   * Adds a column identified by id to the index page
   * @param id
   * @return
   */
  public IndexBuilder column(String id) {
    resource.getIndexColumns().add(id);
    return this;
  }

//  /**
//   * Adds edit/show/delete buttons to the index, defaults to the last column.
//   */
//  public IndexBuilder actions() {
//    return this;
//  }
}
