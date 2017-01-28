package net.nextpulse.sparkadmin.dao;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.ColumnType;
import net.nextpulse.sparkadmin.FormPostEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author yholkamp
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericSQLDAOTest {
  @Mock
  private DataSource mockDataSource;
  @Mock
  private Connection mockConnection;
  @Mock
  private PreparedStatement mockPreparedStatement;

  private GenericSQLDAO dao;
  private FormPostEntry postEntry;

  @Before
  public void setup() throws Exception {
    dao = new GenericSQLDAO(mockDataSource, "tests");
    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    postEntry = new FormPostEntry();
    postEntry.addKeyValue(new ColumnDefinition("key1_column", ColumnType.string), "pk_123");
    postEntry.addKeyValue(new ColumnDefinition("key2_column", ColumnType.integer), "42");
    postEntry.addValue(new ColumnDefinition("value_column", ColumnType.bool), "true");
  }

  @Test
  public void selectOne() throws Exception {

  }

  @Test
  public void selectMultiple() throws Exception {

  }

  @Test
  public void insert() throws Exception {
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    dao.insert(postEntry);
    verify(mockConnection).prepareStatement(Matchers.eq("INSERT INTO tests (key1_column,key2_column,value_column) VALUES (?,?,?)"));
    verify(mockPreparedStatement).setString(0, "pk_123");
    verify(mockPreparedStatement).setInt(1, 42);
    verify(mockPreparedStatement).setBoolean(2, true);
  }

  @Test
  public void update() throws Exception {
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    dao.update(postEntry);
    verify(mockConnection).prepareStatement(Matchers.eq("UPDATE tests SET value_column = ? WHERE key1_column = ? AND key2_column = ?"));
    verify(mockPreparedStatement).setBoolean(0, true);
    verify(mockPreparedStatement).setString(1, "pk_123");
    verify(mockPreparedStatement).setInt(2, 42);
  }

}