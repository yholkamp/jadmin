package net.nextpulse.jadmin.dsl;

/**
 * An interface for all column value transformation methods. May be used to display friendly names for enum values, or additional value adjustments.
 * Will be invoked before the user input is sent to the DAO for saving.
 *
 * @author vvandertas
 */
@FunctionalInterface
public interface ColumnValueTransformer {

  /**
   * Transforms the provided column value, for example show a friendly name instead of an enum value.
   *
   * @param value the column value
   * @return transformed column value, i.e. the friendly name of an enum value
   */
  String apply(Object value);
}
