package integrationtests;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.FormPostEntry;
import net.nextpulse.sparkadmin.dao.DatabaseEntry;
import net.nextpulse.sparkadmin.dao.GenericSQLDAO;
import net.nextpulse.sparkadmin.schema.GenericSQLSchemaProvider;
import org.junit.Before;
import org.junit.Test;
import testhelpers.DatabaseTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yholkamp
 */
public class GenericSQLDAOTest extends DatabaseTest {

  private GenericSQLDAO dao;
  private ColumnDefinition idColumn = new ColumnDefinition("id", ColumnType.integer);
  private ColumnDefinition nameColumn = new ColumnDefinition("name", ColumnType.string);
  private ColumnDefinition activeColumn = new ColumnDefinition("is_active", ColumnType.bool);
  private ColumnDefinition favoriteNumber = new ColumnDefinition("favorite_number", ColumnType.integer);

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

    dao = new GenericSQLDAO(dataSource, "locations");
    dao.initialize(new GenericSQLSchemaProvider(dataSource, "locations"));
  }

  @Test
  public void insert() throws Exception {
    FormPostEntry postEntry = new FormPostEntry();
    postEntry.addKeyValue(idColumn, "3");
    postEntry.addValue(nameColumn, "New Name");
    postEntry.addValue(activeColumn, "true");
    postEntry.addValue(favoriteNumber, "42");

    dao.insert(postEntry);

    Optional<DatabaseEntry> newEntry = dao.selectOne(new Object[]{"3"});
    assertTrue(newEntry.isPresent());

    Map<String, Object> newEntryProperties = newEntry.get().getProperties();
    assertEquals(3, newEntryProperties.get("id"));
    assertEquals("New Name", newEntryProperties.get("name"));
    assertSQLBooleanEqual(true, newEntryProperties.get("is_active"));
    assertEquals(42, newEntryProperties.get("favorite_number"));
  }

  @Test
  public void selectOne() throws Exception {
    Optional<DatabaseEntry> newEntry = dao.selectOne(new Object[]{"1"});
    assertTrue(newEntry.isPresent());
    Map<String, Object> newEntryProperties = newEntry.get().getProperties();
    assertEquals(1, newEntryProperties.get("id"));
    assertEquals("location1", newEntryProperties.get("name"));
    assertSQLBooleanEqual(true, newEntryProperties.get("is_active"));
    assertEquals(13, newEntryProperties.get("favorite_number"));
  }

  @Test
  public void selectMultiple() throws Exception {
    List<DatabaseEntry> values = dao.selectMultiple(0, 2);
    assertEquals("Should return two values", 2, values.size());
  }

  /**
   * Compares boolean values, treating a byte value as boolean for compatibility across different databases/JDBC connectors.
   * @param expected    expected value
   * @param actual      actual value returned by the JDBC connector
   */
  private void assertSQLBooleanEqual(boolean expected, Object actual) {
    if(expected) {
      assertTrue(actual.equals((byte)1) || actual.equals(true));
    } else {
      assertFalse(actual.equals((byte)1) || actual.equals(true));
    }
  }
}
