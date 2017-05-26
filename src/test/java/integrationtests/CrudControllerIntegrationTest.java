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
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import testhelpers.DatabaseTest;
import testhelpers.TestQueryParamsMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
  public void editPost_uncheckedCheckbox() throws Exception {
    Request mockRequest = createMockRequest();
    when(mockRequest.queryMap()).thenReturn(new TestQueryParamsMap(ImmutableMap.of(
        "id", new String[]{"1"},
        "name", new String[]{"newName"},
        "is_active", new String[]{"false"},
        "favorite_number", new String[]{"71"}
    )));
    
    EditPost result = (EditPost) controller.editPostRoute.handle(mockRequest, response);
    assertTrue(result.isSuccess());
    Map<String, Object> location = retrieveLocation(1);
    assertEquals("newName", location.get("name"));
    assertEquals(71, location.get("favorite_number"));
    // PG will return this value as boolean, H2 as byte
    assertTrue(location.get("is_active").equals((byte) 0) || location.get("is_active").equals(false));
  }
  
  @Test
  public void editPost_checkedCheckbox() throws Exception {
    Request mockRequest = createMockRequest();
    when(mockRequest.queryMap()).thenReturn(new TestQueryParamsMap(ImmutableMap.of(
        "id", new String[]{"1"},
        "name", new String[]{"newName2"},
        "is_active", new String[]{"true", "false"},
        "favorite_number", new String[]{"72"}
    )));
    
    EditPost result = (EditPost) controller.editPostRoute.handle(mockRequest, response);
    assertTrue(result.isSuccess());
    Map<String, Object> location = retrieveLocation(1);
    assertEquals("newName2", location.get("name"));
    assertEquals(72, location.get("favorite_number"));
    // PG will return this value as boolean, H2 as byte
    assertTrue(location.get("is_active").equals((byte) 1) || location.get("is_active").equals(true));
  }

  @Test
  public void deleteTest() throws Exception {
    Request mockRequest = createMockRequest();
    when(mockRequest.params(":ids")).thenReturn("1/location1");

    EditPost result = (EditPost) controller.deleteRoute.handle(mockRequest, response);
    assertTrue(result.isSuccess());
  }
  
  private Request createMockRequest() {
    Request mockRequest = mock(Request.class);
    when(mockRequest.attribute("resourceSchemaProvider")).thenReturn(resource);
    return mockRequest;
  }
  
  private Map<String, Object> retrieveLocation(int locationId) throws SQLException {
    Map<String, Object> location;
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM locations WHERE id = ?");
      preparedStatement.setInt(1, locationId);
      ResultSet queryResult = preparedStatement.executeQuery();
      queryResult.next();
      location = new BasicRowProcessor().toMap(queryResult);
    }
    return location;
  }
}

