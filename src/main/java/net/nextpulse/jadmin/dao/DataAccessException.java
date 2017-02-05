package net.nextpulse.jadmin.dao;

/**
 * Generic exception for any exceptions caused by the data sources accessed by JAdmin.
 *
 * @author yholkamp
 */
public class DataAccessException extends Exception {
  public DataAccessException(Exception cause) {
    super(cause);
  }

  public DataAccessException(String message) {
    super(message);
  }

  public DataAccessException(String message, Exception cause) {
    super(message, cause);
  }
}
