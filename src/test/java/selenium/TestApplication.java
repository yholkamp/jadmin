package selenium;

import net.nextpulse.sparkadmin.SparkAdmin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import testhelpers.DatabaseTest;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author yholkamp
 */
public class TestApplication {

  private static final Logger logger = LogManager.getLogger();
  private static DataSource source;
  private SparkAdmin sparkAdmin;

  public void start() {
    logger.error("Test error in testApplication");
    // connect to a JDBC datasource
    Properties properties = DatabaseTest.loadDatabaseProperties();
    source = DatabaseTest.createH2DataSource(properties, "MYSQL");
    DatabaseTest.databaseSetup(source);
    sparkAdmin = new SparkAdmin();

    // default everything
    sparkAdmin.resource("locations", source);
    sparkAdmin.resource("compound_table", source);

    // initialize the application
    sparkAdmin.init();
  }

  public void stop() {
    sparkAdmin.stop();
  }

}
