package net.nextpulse.jadmin;

import com.google.common.collect.ImmutableMap;
import testhelpers.TestQueryParamsMap;
import org.junit.Test;
import spark.Request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author yholkamp
 */
public class CrudControllerTest {
  
  @Test
  public void extractFormPostEntry_additionalData() throws Exception {
    Request mockRequest = createMockRequest(null);
    when(mockRequest.queryMap()).thenReturn(new TestQueryParamsMap(ImmutableMap.of(
        "string", new String[]{"newName"},
        "boolean", new String[]{"1", "0"},
        "favorite_number", new String[]{"71"}
    )));
    
    Resource testResource = new Resource("tests");
    ColumnDefinition booleanColumn = new ColumnDefinition("boolean", ColumnType.bool, false, true);
    testResource.getColumnDefinitions().add(booleanColumn);
    ColumnDefinition stringColumn = new ColumnDefinition("string", ColumnType.string, false, true);
    testResource.getColumnDefinitions().add(stringColumn);
    
    FormPostEntry postEntry = CrudController.extractFormPostEntry(mockRequest, testResource);
    assertEquals("1", postEntry.getValues().get("boolean"));
    assertEquals("newName", postEntry.getValues().get("string"));
  }
  
  private Request createMockRequest(Resource resource) {
    Request mockRequest = mock(Request.class);
    when(mockRequest.attribute("resourceSchemaProvider")).thenReturn(resource);
    return mockRequest;
  }
  
}