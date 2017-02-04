package net.nextpulse.sparkadmin.elements;

import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.helpers.Tuple2;

import java.util.List;
import java.util.function.Supplier;

/**
 * Element that will be rendered as a @code{<select>} tag in the template. Contains a supplier that will be called to
 * retrieve the list of values to show as @code{<option></option>} tags.
 *
 * @author yholkamp
 */
public class FormSelect extends FormInput {
  private final Supplier<List<Tuple2<String, String>>> optionProducer;

  /**
   *
   * @param name            internal name of this column
   * @param columnType      type of this column
   * @param optionSupplier  method that provides a list of tuples of (value, name) that will be used as selectable options.
   */
  public FormSelect(String name, ColumnType columnType, Supplier<List<Tuple2<String, String>>> optionSupplier) {
    super(name, columnType);
    this.optionProducer = optionSupplier;
  }

  @Override
  public String getTemplateName() {
    return "select.ftl";
  }

  @SuppressWarnings("unused")
  public List<Tuple2<String, String>> getOptions() {
    return optionProducer.get();
  }
}
