package selenium;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yholkamp
 */
@Ignore("Not yet implemented")
public class SeleniumTest {
  private static HtmlUnitDriver driver;
  private static TestApplication testApplication;
  private static final String HOST = "http://localhost:9876/admin/";

  @BeforeClass
  public static void openBrowser(){
    driver = new HtmlUnitDriver();
    driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);

    testApplication = new TestApplication();
    testApplication.start();
  }

  @Test
  public void createNewLocation(){
    driver.get(HOST + "locations");
    driver.findElementByLinkText("Add").click();

    assertEquals(HOST + "locations/new", driver.getCurrentUrl());
    driver.findElementById("input-name").sendKeys("myNewLocation");
    driver.findElementByTagName("button").submit();

    assertEquals(HOST + "locations", driver.getCurrentUrl());
    assertTrue(driver.getPageSource().contains("myNewLocation"));
  }

  @AfterClass
  public static void closeBrowser(){
    driver.quit();
    testApplication.stop();
  }
}