package net.nextpulse.jadmin.dsl;

/**
 * Exception thrown when the provided user input is not valid for the column.
 *
 * @author yholkamp
 */
public class InvalidInputException extends Exception {

  public InvalidInputException(String message) {
    super(message);
  }
}
