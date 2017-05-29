package net.nextpulse.jadmin.dao;

import com.google.common.collect.ImmutableList;
import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.ColumnType;
import net.nextpulse.jadmin.FormPostEntry;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

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
  @Mock
  private ResourceSchemaProvider mockResourceProvider;
  
  private GenericSQLDAO dao;
  private FormPostEntry postEntry;
  
  @Before
  public void setup() throws Exception {
    dao = new GenericSQLDAO(mockDataSource, "tests");
    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    
    List<ColumnDefinition> definitions = ImmutableList.of(new ColumnDefinition("key1_column", ColumnType.string),
        new ColumnDefinition("key2_column", ColumnType.integer),
        new ColumnDefinition("value_column", ColumnType.bool));
    when(mockResourceProvider.getColumnDefinitions()).thenReturn(definitions);
    dao.initialize(mockResourceProvider);
    
    postEntry = new FormPostEntry();
    postEntry.addKeyValue("key1_column", "pk_123");
    postEntry.addKeyValue("key2_column", "42");
    postEntry.addValue("value_column", "true");
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
    verify(mockPreparedStatement).setString(1, "pk_123");
    verify(mockPreparedStatement).setInt(2, 42);
    verify(mockPreparedStatement).setBoolean(3, true);
  }
  
  @Test
  public void update() throws Exception {
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    dao.update(postEntry);
    verify(mockConnection).prepareStatement(Matchers.eq("UPDATE tests SET value_column = ? WHERE key1_column = ? AND key2_column = ?"));
    verify(mockPreparedStatement).setBoolean(1, true);
    verify(mockPreparedStatement).setString(2, "pk_123");
    verify(mockPreparedStatement).setInt(3, 42);
  }
  
}