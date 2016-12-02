package net.nextpulse.sparkadmin.elements;

import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.helpers.Tuple2;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author yholkamp
 */
public class FormSelect extends FormInput {
  private final Supplier<List<Tuple2<Object, String>>> optionProducer;

  public FormSelect(String name, ColumnType columnType, Supplier<List<Tuple2<Object, String>>> selectProducer) {
    super(name, columnType);
    this.optionProducer = selectProducer;
  }

  @Override
  public String getTemplateName() {
    return "select.ftl";
  }

  public List<Tuple2<Object, String>> getOptions() {
    return optionProducer.get();
  }
}
