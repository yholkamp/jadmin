package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dsl.InputValidationRule;
import net.nextpulse.jadmin.dsl.InvalidInputException;
import spark.utils.StringUtils;

/**
 * @author yorick
 */
public class InputValidator {
  
  private InputValidator() {
  }
  
  /**
   * Ensures all entries of the FormPostEntry pass validation.
   *
   * @param postEntry entry to validatePostData
   * @param resource
   * @throws InvalidInputException if the user input does not pass validation
   */
  public static void validate(FormPostEntry postEntry, Resource resource) throws InvalidInputException {
    for(ColumnDefinition columnDefinition : resource.getColumnDefinitions()) {
      validateColumn(columnDefinition, postEntry);
    }
  }
  
  /**
   * Compares the POST data against the column definition and validation.
   *
   * @param columnDefinition definition of the column
   * @param postEntry        user provided data
   * @throws InvalidInputException
   */
  private static void validateColumn(ColumnDefinition columnDefinition, FormPostEntry postEntry) throws InvalidInputException {
    if(columnDefinition.isKeyColumn()) {
      String input = postEntry.getKeyValues().get(columnDefinition);
      if(StringUtils.isBlank(input)) {
        throw new InvalidInputException("Key column " + columnDefinition.getName() + " is missing");
      }
    } else {
      String input = postEntry.getValues().get(columnDefinition);
      if(!columnDefinition.isEditable()) {
        throw new InvalidInputException("Column " + columnDefinition.getName() + " is not editable");
      }
      
      // validate the actual input
      validateColumn(columnDefinition, input);
    }
  }
  
  /**
   * Validates the user input against the rules configured for the column
   * @throws InvalidInputException if the user data does not pass input validation
   */
  private static void validateColumn(ColumnDefinition columnDefinition, String userData) throws InvalidInputException {
    for(InputValidationRule rule : columnDefinition.getValidationRules()) {
      rule.validate(columnDefinition, userData);
    }
  }
}
