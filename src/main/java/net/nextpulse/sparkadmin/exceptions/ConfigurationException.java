package net.nextpulse.sparkadmin.exceptions;

/**
 * Exception thrown when an invalid configuration is provided during initialization.
 *
 * @author yholkamp
 */
public class ConfigurationException extends RuntimeException {
  public ConfigurationException(String message) {
    super(message);
  }
}
