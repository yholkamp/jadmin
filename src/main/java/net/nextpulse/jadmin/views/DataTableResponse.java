package net.nextpulse.jadmin.views;

import java.util.List;

/**
 * JSON model to return to the DataTable script running in the client
 *
 * @see <a href="https://datatables.net/manual/server-side">DataTable documentation</a>
 */
public class DataTableResponse {
  /**
   * The draw counter that this object is a response to - from the draw parameter sent as part of the data request. Note that it is strongly recommended for security reasons that you cast this parameter to an integer, rather than simply echoing back to the client what it sent in the draw parameter, in order to prevent Cross Site Scripting (XSS) attacks.
   */
  public int draw;
  /**
   * Total records, after filtering (i.e. the total number of records after filtering has been applied - not just the number of records being returned for this page of data).
   */
  public int recordsFiltered;
  /**
   * The data to be displayed in the table. This is an array of data source objects, one for each row, which will be used by DataTables. Note that this parameter's name can be changed using the ajax option's dataSrc property.
   */
  public List data;
  /**
   * Optional: If an error occurs during the running of the server-side processing script, you can inform the user of this error by passing back the error message to be displayed using this parameter. Do not include if there is no error.
   */
  public String error;
  
  /**
   * Success response constructor.
   *
   * @param draw            contains security token sent in the request
   * @param recordsFiltered number of records after filtering
   * @param data            results array
   */
  public DataTableResponse(int draw, List data, int recordsFiltered) {
    this.draw = draw;
    this.recordsFiltered = recordsFiltered;
    this.data = data;
  }
  
  /**
   * Error response constructor
   *
   * @param error
   */
  public DataTableResponse(String error) {
    this.error = error;
  }
}