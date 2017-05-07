package net.nextpulse.jadmin.dsl;

/**
 * Functional interface that specifies a method that will validatePostData (and optionally transform) a user's input value.
 * If the user input is not valid, an exception is thrown, otherwise the (optionally transformed) user input is returned.
 *
 * @author yholkamp
 */
@FunctionalInterface
public interface InputValidator {

  /**
   * Method that will validatePostData (and optionally transform) a user's input value. If the user input is not valid, an
   * exception is thrown, otherwise the (optionally transformed) user input is returned.
   *
   * @param userInput   raw (unsafe!) user data
   * @return  user data, optionally transformed in to a new format
   * @throws InvalidInputException  if the user input is invalid
   */
  String validate(String userInput) throws InvalidInputException;
}
