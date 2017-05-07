package net.nextpulse.jadmin;

import com.google.common.collect.ImmutableList;
import net.nextpulse.jadmin.dao.InMemoryDAO;
import net.nextpulse.jadmin.dsl.InvalidInputException;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;
import org.junit.Before;
import org.junit.Test;
import spark.Request;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author yholkamp
 */
public class CrudControllerTest {
  private static Map<String, Resource> resourcesMap;
  private JAdmin jAdmin;
  private ResourceSchemaProvider schemaProvider;
  private List<ColumnDefinition> columns;
  private Resource resource;
  ColumnDefinition idColumn;
  ColumnDefinition loginColumn;
  ColumnDefinition passwordColumn;

  @Before
  public void setUp() throws Exception {
    jAdmin = new JAdmin();
    //CrudController controller = new CrudController("/bla", resourcesMap);
    schemaProvider = () -> columns;

    idColumn = new ColumnDefinition("id", ColumnType.integer, true, false);
    loginColumn = new ColumnDefinition("login", ColumnType.text, false, false);
    passwordColumn = new ColumnDefinition("password", ColumnType.text, false, true);

    columns = ImmutableList.of(idColumn, loginColumn, passwordColumn);
    jAdmin.resource("test_obj", new InMemoryDAO(), schemaProvider).formConfig(x ->
        x.inputGroup("User fields", inputGroupBuilder -> {
          inputGroupBuilder.input("password", CrudControllerTest::validateAndHashPassword);
        })
    );

    resourcesMap = jAdmin.getResources();
    resource = resourcesMap.get("test_obj");
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectMissingPK() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addValue(loginColumn, "1");
    postEntry.addValue(passwordColumn, "foobarbaz");
    CrudController.validatePostData(postEntry, resource);
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectPKAsValue() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addValue(loginColumn, "1");
    postEntry.addValue(idColumn, "1");
    postEntry.addValue(passwordColumn, "foobarbaz");
    CrudController.validatePostData(postEntry, resource);
  }

  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectShortPassword() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue(idColumn, "1");
    postEntry.addValue(passwordColumn, "short");
    postEntry.addValue(loginColumn, "foobarbaz");
    CrudController.validatePostData(postEntry, resource);
  }

  @Test
  public void testValidatePostData_shouldTransformValidPassword() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue(idColumn, "1");
    postEntry.addValue(passwordColumn, "ybatybatybat");
    postEntry.addValue(loginColumn, "foo");
    CrudController.validatePostData(postEntry, resource);
    String updatedValue = postEntry.getValues().get(passwordColumn);
    assertEquals("Should have ROT13-ed our input data", "longlonglong", updatedValue);
  }

  /**
   * Example password "encrypting" method. NOT SAFE FOR ANYTHING OTHER THAN DEMOS. THis will just replace every
   * character with a character 13 places further in the alphabet.
   * <p>
   * Recommended options: SHA1024+, BCrypt, PBKDF2 or SCRYPT.
   *
   * @param input user provided (unsafe) input data
   * @return a tuple consisting of valid and the transformed user input.
   */
  private static String validateAndHashPassword(String input) throws InvalidInputException {
    if(input.length() < 6) {
      throw new InvalidInputException("Password is too short");
    } else {
      // Thanks to http://stackoverflow.com/a/17396786 for this excellent ROT13 implementation
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if(c >= 'a' && c <= 'm') c += 13;
        else if(c >= 'A' && c <= 'M') c += 13;
        else if(c >= 'n' && c <= 'z') c -= 13;
        else if(c >= 'N' && c <= 'Z') c -= 13;
        sb.append(c);
      }
      return sb.toString();
    }
  }

  private Request createMockRequest(Resource resource) {
    Request mockRequest = mock(Request.class);
    when(mockRequest.attribute("resourceSchemaProvider")).thenReturn(resource);
    return mockRequest;
  }

}