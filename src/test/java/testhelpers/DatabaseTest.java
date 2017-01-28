package testhelpers;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Parent class for tests that should be run against a variatey of (SQL) databases to check for compatibility. Classes
 * extending this class will be invoked with several different DataSource objects, defined in the data() method.
 *
 * @author yholkamp
 */
@RunWith(Parameterized.class)
public abstract class DatabaseTest {

  private static final Logger logger = LogManager.getLogger();
  /**
   * Cache of the DataSources to avoid recreating these every time.
   */
  private static ImmutableList<Object[]> dataCache;
  /**
   * Name of the datasource, present to make the JUnit reporting more readable.
   */
  @Parameterized.Parameter(value = 0)
  public String dataSourceName;

  /**
   * DataSource object to use for our calls
   */
  @Parameterized.Parameter(value = 1)
  public DataSource dataSource;

  /**
   * Load the test tables into each database
   * @param dataSource
   */
  public static void databaseSetup(DataSource dataSource) {
    logger.info("Starting databaseSetup for dataSource " + dataSource.getClass().getSimpleName());
    try(Connection conn = dataSource.getConnection()) {
      // simple table without foreign key
      conn.prepareStatement("CREATE TABLE IF NOT EXISTS locations(id SERIAL PRIMARY KEY, name VARCHAR(255), is_active TINYINT(1), favorite_number INT);").execute();

      // table with a compound primary key
      conn.prepareStatement("CREATE TABLE IF NOT EXISTS compound_table(id INT, second_id INT, name VARCHAR(255), location_id INTEGER REFERENCES locations(id), PRIMARY KEY(id, second_id));").execute();
    } catch(SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Parameterized.Parameters(name = "Database {index}, {0}")
  public static Collection<Object[]> data() {
    // TODO: read from config
    if(dataCache == null) {
      dataCache = ImmutableList.of(
          new Object[]{"H2-MySQL", createH2DataSource("MYSQL")},
          new Object[]{"H2-PostgreSQL", createH2DataSource("PostgreSQL")},
          new Object[]{"PostgreSQL", createPostgresDataSource()
        });
    }
    return dataCache;
  }

  public static DataSource createH2DataSource(String mode) {
    JdbcDataSource dataSource = new JdbcDataSource();
    String h2jdbcConnectionString = "jdbc:h2:mem:test_"+mode+";DATABASE_TO_UPPER=FALSE;MODE="+mode+";DB_CLOSE_DELAY=-1";
    dataSource.setURL(h2jdbcConnectionString);
    databaseSetup(dataSource);
    return dataSource;
  }

  public static DataSource createPostgresDataSource() {
    PGPoolingDataSource source = new PGPoolingDataSource();
    source.setUrl("jdbc:postgresql://localhost/sparkadmin_test");
    source.setMaxConnections(10);
    databaseSetup(source);
    return source;
  }
}
