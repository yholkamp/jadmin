package testhelpers;

import spark.QueryParamsMap;

import java.util.Map;

public class TestQueryParamsMap extends QueryParamsMap {

  public TestQueryParamsMap(Map<String, String[]> params) {
    super(params);
  }
}
