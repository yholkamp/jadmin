package selenium;

import net.nextpulse.jadmin.JAdmin;
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
  private JAdmin jAdmin;

  public void start() {
    logger.error("Test error in testApplication");
    // connect to a JDBC datasource
    Properties properties = DatabaseTest.loadDatabaseProperties();
    source = DatabaseTest.createH2DataSource(properties, "MYSQL");
    DatabaseTest.databaseSetup(source);
    jAdmin = new JAdmin();

    // default everything
    jAdmin.resource("locations", source);
    jAdmin.resource("compound_table", source);

    // initialize the application
    jAdmin.init();
  }

  public void stop() {
    jAdmin.stop();
  }

  public static void main(String[] args) {
    new TestApplication().start();
  }

}
