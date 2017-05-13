package net.nextpulse.jadmin.helpers;

/**
 * @author yholkamp
 */
public class Path {

  public static class Template {
    public static final String JADMIN_INDEX = "index.ftl";
    public static final String LIST = "list.ftl";
    public static final String EDIT = "edit.ftl";
  }

  public static class Route {
    public static final String ADMIN_INDEX = "";
    public static final String CREATE_ROW = "/:table/new";
    public static final String EDIT_ROW = "/:table/:ids";
    public static final String WILDCARD = "/*";
    public static final String INDEX = "/:table";
  }
}
