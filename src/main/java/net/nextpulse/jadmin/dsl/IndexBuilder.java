package net.nextpulse.jadmin.dsl;

import net.nextpulse.jadmin.Resource;

/**
 * DSL class that offers a way to configure which columns should be shown on the index page of a column.
 *
 * @author yholkamp
 */
public class IndexBuilder {
  private final Resource resource;
  
  public IndexBuilder(Resource resource) {
    this.resource = resource;
  }
  
  /**
   * Adds a column identified by id to the index page
   *
   * @param id internal column name to add
   * @return this instance
   */
  public IndexBuilder column(String id) {
    resource.addColumn(id);
    return this;
  }

  /**
   * Adds a column identified by id to the index page, with an optional inputValidationRule function. This function will be
   * called when the user input is submitted, allowing the method to validatePostData or transform (i.e. hash) the user input.
   *
   * @param id                         internal column name to add
   * @param columnValueTransformer     column value transformation method to apply to the value in the table on the index page for this column.
   * @return this instance
   */
  public IndexBuilder column(String id, ColumnValueTransformer columnValueTransformer) {
    // ensure the column exists
    resource.addColumn(id, columnValueTransformer);
    return this;
  }
  
  /**
   * Set the number of entries to show per page.
   * 
   * @param count number of entries
   * @return this instance
   */
  public IndexBuilder perPage(int count) {
    resource.setPerPageCount(count);
    return this;
  }

//  /**
//   * Adds edit/show/delete buttons to the index, defaults to the last column.
//   */
//  public IndexBuilder actions() {
//    return this;
//  }
}
