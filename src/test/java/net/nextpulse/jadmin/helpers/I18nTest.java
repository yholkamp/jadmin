package net.nextpulse.jadmin.helpers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yholkamp
 */
public class I18nTest {
  
  @Before
  public void setUp() throws Exception {
    I18n.setLanguage("custom");
  }
  
  @Test
  public void get_simpleTranslation() throws Exception {
    assertEquals("Hi this is a translation", I18n.get("test.key"));
  }
  
  @Test
  public void get_withoutTranslation() throws Exception {
    assertEquals("missing_key", I18n.get("missing_key"));
  }
  
  @Test
  public void get_formatTranslation() throws Exception {
    assertEquals("There are 99 results", I18n.get("format.key", 99));
  }
  
}