package net.nextpulse.jadmin.dsl;

import net.nextpulse.jadmin.ColumnDefinition;
import spark.utils.StringUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Functional interface that specifies a method that will validatePostData (and optionally transform) a user's input value.
 * If the user input is not valid, an exception is thrown, otherwise the (optionally transformed) user input is returned.
 *
 * @author yholkamp
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface InputValidationRule {
  
  /**
   * Validation rule that indicates a required field.
   */
  Supplier<InputValidationRule> REQUIRED = () -> (columnDefinition, userInput) -> {
    if (StringUtils.isBlank(userInput)) {
      throw new InvalidInputException(String.format("%s is empty but is required", columnDefinition.getName()));
    }
  };
  
  /**
   * Validation rule that indicates a minimum length
   */
  Function<Integer, InputValidationRule> MINIMUM_LENGTH = (minLength) -> (columnDefinition, userInput) -> {
    if (StringUtils.isBlank(userInput) || userInput.length() < minLength) {
      throw new InvalidInputException(String.format("%s is less than %d characters", columnDefinition.getName(), minLength));
    }
  };
  
  /**
   * Validation rule that indicates a minimum length
   */
  Function<Integer, InputValidationRule> MAXIMUM_LENGTH = (maxLength) -> (columnDefinition, userInput) -> {
    if (userInput.length() > maxLength) {
      throw new InvalidInputException(String.format("%s is more than %d characters long", columnDefinition.getName(), maxLength));
    }
  };
  
  /**
   * Method that will validatePostData (and optionally transform) a user's input value. If the user input is not valid, an
   * exception is thrown, otherwise the (optionally transformed) user input is returned.
   *
   * @param columnDefinition
   * @param userInput   raw (unsafe!) user data
   * @throws InvalidInputException  if the user input is invalid
   */
  void validate(ColumnDefinition columnDefinition, String userInput) throws InvalidInputException;
}
