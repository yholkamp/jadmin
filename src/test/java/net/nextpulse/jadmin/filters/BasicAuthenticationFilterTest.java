package net.nextpulse.jadmin.filters;

import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * @author yorick
 */
public class BasicAuthenticationFilterTest {
  
  private BasicAuthenticationFilter filter;
  
  @Before
  public void setUp() throws Exception {
    filter = new BasicAuthenticationFilter("user", "pass");
  }
  
  @Test
  public void handle() throws Exception {
    
  }
  
  @Test
  public void authenticateWithHeader_shouldAcceptValidAuth() throws Exception {
    assertTrue(filter.authenticateWithHeader("Basic " + base64Encode("user:pass")));
  }
  
  @Test
  public void authenticateWithHeader_shouldRejectInvalidUser() throws Exception {
    assertFalse(filter.authenticateWithHeader("Basic " + base64Encode("user2:pass")));
  }
  
  @Test
  public void authenticateWithHeader_shouldRejectInvalidPassword() throws Exception {
    assertFalse(filter.authenticateWithHeader("Basic " + base64Encode("user:pass2")));
  }
  
  @Test
  public void authenticateWithHeader_shouldRejectMissingUser() throws Exception {
    assertFalse(filter.authenticateWithHeader("Basic " + base64Encode(":pass2")));
  }
  
  @Test
  public void authenticateWithHeader_shouldRejectNullValue() throws Exception {
    assertFalse(filter.authenticateWithHeader(null));
  }
  
  @Test
  public void authenticateWithHeader_shouldRejectEmptryString() throws Exception {
    assertFalse(filter.authenticateWithHeader(""));
    assertFalse(filter.authenticateWithHeader("Basic"));
    assertFalse(filter.authenticateWithHeader("Basic "));
  }
  
  private String base64Encode(String value) {
    return Base64.getEncoder().encodeToString(value.getBytes());
  }
  
}