package net.nextpulse.jadmin.dsl;

/**
 * An interface for all user input transformation methods. May be used to hash a user password, replace common typos or 
 * other adjustments. Will be invoked before the user input is sent to the DAO for saving.
 *
 * @author yholkamp
 */
@FunctionalInterface
public interface InputTransformer {
  
  /**
   * Transforms the provided user input, for example to hash a user provided password.
   *
   * @param input raw user input
   * @return transformed user input, i.e. applying a hashing algorithm
   */
  String apply(String input);
}
