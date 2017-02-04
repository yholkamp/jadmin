package net.nextpulse.sparkadmin.dao;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.FormPostEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yholkamp
 */
public class InMemoryDAOTest {
  private InMemoryDAO dao;
  private ColumnDefinition keyColumn1;
  private ColumnDefinition keyColumn2;
  private ColumnDefinition valueColumn;
  private FormPostEntry formEntry2;
  private FormPostEntry formEntry;

  @Before
  public void setUp() throws Exception {
    dao = new InMemoryDAO();

    keyColumn1 = new ColumnDefinition("key1_column", ColumnType.string);
    keyColumn2 = new ColumnDefinition("key2_column", ColumnType.integer);
    valueColumn = new ColumnDefinition("value_column", ColumnType.bool);

    formEntry = new FormPostEntry();
    formEntry.addKeyValue(keyColumn1, "key1value");
    formEntry.addKeyValue(keyColumn2, "key2value");
    formEntry.addValue(valueColumn, "value123");
    dao.insert(formEntry);

    formEntry2 = new FormPostEntry();
    formEntry2.addKeyValue(keyColumn1, "key1 another value");
    formEntry2.addKeyValue(keyColumn2, "key2 another value");
    formEntry2.addValue(valueColumn, "another value");
    dao.insert(formEntry2);
  }

  @Test
  public void selectOne() throws Exception {
    Optional<DatabaseEntry> object = dao.selectOne("key1value", "key2value");
    assertTrue("Should return the first entry", object.isPresent());
    // assert content is correct
    assertEquals(formEntry.toPropertiesMap(), object.get().getProperties());
  }

  @Test
  public void selectMultiple() throws Exception {
    List<DatabaseEntry> results = dao.selectMultiple(1, 1);
    assertEquals("Should return 1 row", 1, results.size());
    assertEquals("Should skip the first object and return the second object", formEntry2.toPropertiesMap(), results.get(0).getProperties());
  }

  @Test
  public void insert() throws Exception {
    InMemoryDAO dao = new InMemoryDAO();

    FormPostEntry entry = new FormPostEntry();
    entry.addKeyValue(keyColumn1, "key1value");
    entry.addKeyValue(keyColumn2, "key2value");
    entry.addValue(valueColumn, "value123");
    dao.insert(entry);

    assertEquals("Should have added an object", 1, dao.selectMultiple(0, 10).size());
  }

  @Test
  public void update() throws Exception {
    FormPostEntry entry = new FormPostEntry();
    entry.addKeyValue(keyColumn1, "key1value");
    entry.addKeyValue(keyColumn2, "key2value");
    entry.addValue(valueColumn, "new value");

    dao.update(entry);

    assertEquals("new value", dao.selectOne("key1value", "key2value").map(x -> x.getProperties().get("value_column")).orElse(""));
  }

}