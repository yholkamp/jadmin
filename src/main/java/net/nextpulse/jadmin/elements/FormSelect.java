package net.nextpulse.jadmin.elements;

import net.nextpulse.jadmin.ColumnType;
import net.nextpulse.jadmin.helpers.Tuple2;

import java.util.List;
import java.util.function.Supplier;

/**
 * Element that will be rendered as a select tag in the template. Contains a supplier that will be called to
 * retrieve the list of values to show as option tags.
 *
 * @author yholkamp
 */
public class FormSelect extends FormInput {
  private final Supplier<List<Tuple2<String, String>>> optionProducer;

  /**
   * @param name           internal name of this column
   * @param columnType     type of this column
   * @param optionSupplier method that provides a list of tuples of (value, name) that will be used as selectable options.
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
