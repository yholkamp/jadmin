package integrationtests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import net.nextpulse.jadmin.CrudController;
import net.nextpulse.jadmin.JAdmin;
import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.views.EditPost;
import net.nextpulse.jadmin.views.EditView;
import net.nextpulse.jadmin.views.ListView;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import testhelpers.DatabaseTest;
import testhelpers.IntegrationTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Basic 'live' test for the CrudControllerIntegrationTest, using actual databases to validatePostData the implemented functionality.
 *
 * @author yholkamp
 */
@Category(IntegrationTest.class)
public class CrudControllerIntegrationTest extends DatabaseTest {

  private Resource resource;
  private CrudController controller;
  private Response response = mock(Response.class);
  
  @Before
  public void loadData() throws Exception {
    Operation operation =
        sequenceOf(
            deleteAllFrom("locations"),
            insertInto("locations")
                .withDefaultValue("is_active", true)
                .columns("id", "name", "favorite_number")
                .values(1, "location1", 13)
                .values(2, "location2", 42)
                .build());
    DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
    dbSetup.launch();

    JAdmin jAdmin = new JAdmin();
    jAdmin.resource("locations", dataSource);
    resource = jAdmin.getResources().get("locations");
    controller = new CrudController("/adminprefix", jAdmin.getResources());
  }

  @Test
  public void listMethod() throws Exception {
    ModelAndView result = controller.listRoute.handle(createMockRequest(), response);
    ListView model = (ListView) result.getModel();
    assertEquals("locations", model.getResource().getTableName());
    assertEquals(ImmutableList.of("id", "name", "is_active", "favorite_number"), model.getHeaders());
    assertEquals(2, model.getRows().size());
  }

  @Test
  public void editMethod() throws Exception {
    Request mockRequest = createMockRequest();
    when(mockRequest.params(":ids")).thenReturn("2/location2");

    ModelAndView result = controller.editRoute.handle(mockRequest, response);
    EditView model = (EditView) result.getModel();

    // ensure the name and id match, both in case insensitive fashion
    assertEquals("location2", model.getObject().getProperties().getOrDefault("NAME", model.getObject().getProperties().get("name")));
    assertEquals(2, model.getObject().getProperties().getOrDefault("ID", model.getObject().getProperties().get("id")));
  }

  @Test
  public void editPost() throws Exception {
    Request mockRequest = createMockRequest();
    when(mockRequest.queryMap()).thenReturn(new TestQueryParamsMap(ImmutableMap.of(
        "id", new String[]{"1"},
        "name", new String[]{"newName"},
        "is_active", new String[]{"0"},
        "favorite_number", new String[]{"71"}
    )));

    EditPost result = (EditPost) controller.editPostRoute.handle(mockRequest, response);
    assertTrue(result.isSuccess());
    try(Connection conn = dataSource.getConnection()) {
      ResultSet queryResult = conn.prepareStatement("SELECT * FROM locations WHERE id = 1").executeQuery();
      // pull the result row
      queryResult.next();
      Map<String, Object> location = new BasicRowProcessor().toMap(queryResult);
      assertEquals(1, location.get("id"));
      assertEquals("newName", location.get("name"));
      assertEquals(71, location.get("favorite_number"));
      // PG will return this value as boolean, H2 as byte
      assertTrue(location.get("is_active").equals((byte) 0) || location.get("is_active").equals(false));
    }
  }

  private Request createMockRequest() {
    Request mockRequest = mock(Request.class);
    when(mockRequest.attribute("resourceSchemaProvider")).thenReturn(resource);
    return mockRequest;
  }

}

class TestQueryParamsMap extends QueryParamsMap {

  public TestQueryParamsMap(Map<String, String[]> params) {
    super(params);
  }
}