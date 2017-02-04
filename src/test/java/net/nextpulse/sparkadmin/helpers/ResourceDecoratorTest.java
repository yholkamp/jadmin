package net.nextpulse.sparkadmin.helpers;

import com.google.common.collect.ImmutableList;
import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.Resource;
import net.nextpulse.sparkadmin.elements.FormButtons;
import net.nextpulse.sparkadmin.elements.FormInputGroup;
import net.nextpulse.sparkadmin.elements.PageElement;
import net.nextpulse.sparkadmin.schema.ResourceSchemaProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yholkamp
 */
public class ResourceDecoratorTest {

  private Resource resource;
  private ResourceDecorator decorator;
  private ResourceSchemaProvider schemaProvider;

  private ColumnDefinition idColumn;
  private ColumnDefinition secondIdColumn;
  private ColumnDefinition nameColumn;

  private List<ColumnDefinition> columns;

  @Before
  public void setup() throws Exception {
    resource = new Resource("locations");
    decorator = new ResourceDecorator();
    schemaProvider = () -> columns;

    idColumn = new ColumnDefinition("id", ColumnType.integer, true, false);
    secondIdColumn = new ColumnDefinition("second_id", ColumnType.text, false, false);
    nameColumn = new ColumnDefinition("name", ColumnType.text, false, true);

    columns = ImmutableList.of(idColumn, secondIdColumn, nameColumn);
  }

  @Test
  public void decorate_simplePrimaryKey() throws Exception {
    decorator.accept(resource, schemaProvider);
    assertEqualsCaseInsensitive(ImmutableList.of("id"), resource.getPrimaryKeys());
    assertEquals(3, resource.getColumnDefinitions().size());
  }

  @Test
  public void decorate_compoundPrimaryKey() throws Exception {
    secondIdColumn.setKeyColumn(true);

    decorator.accept(resource, schemaProvider);
    assertEqualsCaseInsensitive(ImmutableList.of("id", "second_id"), resource.getPrimaryKeys());
    assertEquals(3, resource.getColumnDefinitions().size());

    secondIdColumn.setKeyColumn(false);
  }

  /**
   * Check whether the formPage has been populated with the right input fields
   */
  @Test
  public void decorate_formPage() {
    decorator.accept(resource, schemaProvider);

    List<PageElement> formPage = resource.getFormPage();
    assertEquals(2, formPage.size());
    FormInputGroup result = (FormInputGroup) formPage.get(0);
    assertEquals(2, result.getInputs().size());
    assertTrue("Should have added an input field for second_id", result.getInputs().stream().anyMatch(x -> x.getName().equals("second_id")));
    assertTrue("Should have added an input field for name", result.getInputs().stream().anyMatch(x -> x.getName().equals("name")));

    assertTrue("Should have added submit/cancel buttons by default", formPage.get(1) instanceof FormButtons);
  }

  protected void assertEqualsCaseInsensitive(List<String> expected, List<String> actual) {
    assertEquals(
        expected.stream().map(String::toLowerCase).collect(Collectors.toList()),
        actual.stream().map(String::toLowerCase).collect(Collectors.toList())
    );
  }

}