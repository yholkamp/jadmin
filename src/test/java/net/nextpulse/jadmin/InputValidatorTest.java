package net.nextpulse.jadmin;

import com.google.common.collect.ImmutableList;
import net.nextpulse.jadmin.dao.InMemoryDAO;
import net.nextpulse.jadmin.dsl.InputValidationRule;
import net.nextpulse.jadmin.dsl.InvalidInputException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author yorick
 */
public class InputValidatorTest {
  ColumnDefinition idColumn;
  ColumnDefinition loginColumn;
  ColumnDefinition passwordColumn;
  private JAdmin jAdmin = new JAdmin();
  private Resource resource;
  
  @Before
  public void setUp() throws Exception {
    idColumn = new ColumnDefinition("id", ColumnType.integer, true, false);
    loginColumn = new ColumnDefinition("login", ColumnType.text, false, true);
    passwordColumn = new ColumnDefinition("password", ColumnType.text, false, true);
    
    jAdmin.resource("test_obj", new InMemoryDAO(), () -> ImmutableList.of(idColumn, loginColumn, passwordColumn)).formConfig(x ->
        x.inputGroup("User fields", inputGroupBuilder -> {
          inputGroupBuilder.input("password", InputValidationRule.MINIMUM_LENGTH.apply(6));
        })
    );
    resource = jAdmin.getResources().get("test_obj");
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectMissingPK_onEdit() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addValue("login", "1");
    postEntry.addValue("password", "foobarbaz");
    InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
  }
  
  @Test
  public void testValidatePostData_shouldAcceptMissingPK_onCreate() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addValue("login", "1");
    postEntry.addValue("password", "foobarbaz");
    InputValidator.validate(postEntry, resource, ValidationMode.CREATE);
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectPKAsValue() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addValue("login", "1");
    postEntry.addValue("id", "1");
    postEntry.addValue("password", "foobarbaz");
    InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectShortPassword() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue("id", "1");
    postEntry.addValue("password", "short");
    postEntry.addValue("login", "foobarbaz");
    InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
  }
  
  @Test(expected = InvalidInputException.class)
  public void testValidatePostData_shouldRejectUneditableColumnEdits() throws Exception {
    passwordColumn.setEditable(false);
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue("id", "1");
    postEntry.addValue("password", "ybatybatybat");
    postEntry.addValue("login", "foo");
    InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
  }
  
  @Test
  public void testValidatePostData_shouldAcceptAValidEntry() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue("id", "1");
    postEntry.addValue("password", "ybatybatybat");
    postEntry.addValue("login", "foo");
    InputValidator.validate(postEntry, resource, ValidationMode.EDIT);
  }
  
}