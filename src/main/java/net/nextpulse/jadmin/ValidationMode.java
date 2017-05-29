package net.nextpulse.jadmin;

/**
 * Enum used to indicate whether validation is performed for a new entry or an update of an existing entry.
 */
public enum ValidationMode {
  /**
   * Enum value to identify the creation of a new entry
   */
  CREATE,
  /**
   * Enum value to identify updating an existing entry
   */
  EDIT
}
