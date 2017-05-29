package net.nextpulse.jadmin;

import net.nextpulse.jadmin.dsl.InputValidationRule;
import net.nextpulse.jadmin.dsl.InvalidInputException;
import spark.utils.StringUtils;

/**
 * Input validation class that will check the user provided data against the configured input restrictions.
 *
 * @author yorick
 */
public class InputValidator {
  
  private InputValidator() {
  }
  
  /**
   * Ensures all entries of the FormPostEntry pass validation.
   *
   * @param postEntry      entry to validatePostData
   * @param resource       internal resource object
   * @param validationMode validation mode, either edit or create
   * @throws InvalidInputException if the user input does not pass validation
   */
  public static void validate(FormPostEntry postEntry, Resource resource, ValidationMode validationMode) throws InvalidInputException {
    if(resource.getBeforeValidation() != null) {
      resource.getBeforeValidation().apply(validationMode, postEntry);
    }
    for(ColumnDefinition columnDefinition : resource.getColumnDefinitions()) {
      validateColumn(columnDefinition, postEntry, validationMode);
    }
    if(resource.getAfterValidation() != null) {
      resource.getAfterValidation().apply(validationMode, postEntry);
    }
  }
  
  /**
   * Compares the POST data against the column definition and validation.
   *
   * @param columnDefinition definition of the column
   * @param postEntry        user provided data
   * @param validationMode   validation mode
   * @throws InvalidInputException
   */
  private static void validateColumn(ColumnDefinition columnDefinition, FormPostEntry postEntry, ValidationMode validationMode) throws InvalidInputException {
    if(columnDefinition.isKeyColumn()) {
      // skip the key presence requirement for new entries
      if(validationMode == ValidationMode.EDIT) {
        String input = postEntry.getKeyValues().get(columnDefinition);
        if(StringUtils.isBlank(input)) {
          throw new InvalidInputException("Key column " + columnDefinition.getName() + " is missing");
        }
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
   *
   * @throws InvalidInputException if the user data does not pass input validation
   */
  private static void validateColumn(ColumnDefinition columnDefinition, String userData) throws InvalidInputException {
    for(InputValidationRule rule : columnDefinition.getValidationRules()) {
      rule.validate(columnDefinition, userData);
    }
  }
}
