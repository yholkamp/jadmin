package net.nextpulse.jadmin.dsl;

import net.nextpulse.jadmin.FormPostEntry;
import net.nextpulse.jadmin.ValidationMode;

/**
 * A functional interface that is used to provide user input validation methods.
 */
@FunctionalInterface
public interface ValidationFunction {
  
  /**
   * Validation function that will be called by the JAdmin internals. The function may throw an InvalidInputException if
   * the provided FormPostEntry does not conform to the validation requirements. Additionally, the function may add or
   * change the data.
   * 
   * @param validationMode validation mode, can be EDIT or NEW
   * @param formPostEntry  user submitted post data
   * @throws InvalidInputException if the object could not be validated
   */
  void apply(ValidationMode validationMode, FormPostEntry formPostEntry) throws InvalidInputException;
}
