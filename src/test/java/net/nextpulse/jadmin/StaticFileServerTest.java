package net.nextpulse.jadmin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author yholkamp
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticFileServerTest {
  private StaticFileServer fileServer;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  
  @Before
  public void setUp() throws Exception {
    fileServer = new StaticFileServer("/barbar");
  }
  
  @Test
  public void testStaticPathMatching() throws Exception {
    assertTrue(fileServer.matchesStaticPath("/barbar/css/style.css"));
    assertFalse(fileServer.matchesStaticPath("/barbar/css2/style.css"));
    assertFalse(fileServer.matchesStaticPath("/barbar/../../css/style.css"));
  }
  
}
