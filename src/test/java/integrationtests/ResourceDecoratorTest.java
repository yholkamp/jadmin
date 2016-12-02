package integrationtests;

import com.google.common.collect.ImmutableList;
import net.nextpulse.sparkadmin.Resource;
import net.nextpulse.sparkadmin.ResourceDecorator;
import net.nextpulse.sparkadmin.elements.FormInputGroup;
import net.nextpulse.sparkadmin.elements.PageElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testhelpers.DatabaseTest;
import testhelpers.IntegrationTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yholkamp
 */
@Category(IntegrationTest.class)
public class ResourceDecoratorTest extends DatabaseTest {

  private Resource simpleResource;
  private ResourceDecorator decorator;
  private Resource compoundResource;

  @Before
  public void setup() throws Exception {
    simpleResource = new Resource("locations");
    compoundResource = new Resource("compound_table");
    decorator = new ResourceDecorator(dataSource);
  }

  @Test
  public void decorate_simplePrimaryKey() throws Exception {
    decorator.decorate(simpleResource);
    assertEqualsCaseInsensitive(ImmutableList.of("id"), simpleResource.getPrimaryKeys());
    // TODO: assert the content is correct
    assertEquals(4, simpleResource.getColumnDefinitions().size());
  }

  @Test
  public void decorate_compoundPrimaryKey() throws Exception {
    decorator.decorate(compoundResource);
    assertEqualsCaseInsensitive(ImmutableList.of("id", "second_id"), compoundResource.getPrimaryKeys());
    // TODO: assert the content is correct
    assertEquals(4, compoundResource.getColumnDefinitions().size());
  }

  @Test
  public void decorate_formFields() {
    decorator.decorate(compoundResource);
    // TODO: assert the content is correct
    List<PageElement> formPage = compoundResource.getFormPage();
    assertEquals(1, formPage.size());
    FormInputGroup result = (FormInputGroup) formPage.get(0);
    assertEquals(2, result.getInputs().size());
    assertTrue(result.getInputs().stream().anyMatch(x -> x.getName().equals("location_id")));
    assertTrue(result.getInputs().stream().anyMatch(x -> x.getName().equals("name")));
  }

  protected void assertEqualsCaseInsensitive(List<String> expected, List<String> actual) {
    assertEquals(
        expected.stream().map(String::toLowerCase).collect(Collectors.toList()),
        actual.stream().map(String::toLowerCase).collect(Collectors.toList())
    );
  }

}