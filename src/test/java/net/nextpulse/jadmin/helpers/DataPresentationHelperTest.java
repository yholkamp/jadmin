package net.nextpulse.jadmin.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yholkamp
 */
public class DataPresentationHelperTest {
  
  @Test
  public void extractUrlEncodedPK() throws Exception {
    String output = DataPresentationHelper.extractUrlEncodedPK(ImmutableList.of("id", "secondId"), ImmutableMap.of("id", 1, "secondId", "\\baz"));
    assertEquals("1/%5Cbaz", output);
  
    String output2 = DataPresentationHelper.extractUrlEncodedPK(ImmutableList.of("id", "secondId"), ImmutableMap.of("id", 1, "secondId", "/barbar/"));
    assertEquals("1/%2Fbarbar%2F", output2);
  }
  
}