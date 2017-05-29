package net.nextpulse.jadmin.dsl;

/**
 * Exception thrown when the provided user input is not valid for the column.
 *
 * @author yholkamp
 */
public class InvalidInputException extends Exception {
  
  /**
   * Creates a new input validation exception with the provided exception message being reported to the user as feedback.
   *
   * @param message message to show to the end user
   */
  public InvalidInputException(String message) {
    super(message);
  }
}
