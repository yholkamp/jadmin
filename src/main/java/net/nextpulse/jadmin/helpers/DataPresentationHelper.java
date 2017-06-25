package net.nextpulse.jadmin.helpers;

import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.dao.DatabaseEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Helper class containing methods used to (re)format the data before presenting it to the end user.
 *
 * @author yholkamp
 */
public class DataPresentationHelper {
  
  private static final Logger logger = LogManager.getLogger();
  
  private DataPresentationHelper() {
  }
  
  
  /**
   * Method that takes a resource definition and list of database rows and copies all fields that should be available
   * on the list page and adds a key field identifying the row.
   *
   * @param resource resource definition
   * @param rows     row objects to transform
   * @return a list of maps that contain the fields available on the list page as well as a row identifier
   */
  public static List<Map<String, Object>> transformDatabaseResults(Resource resource, List<DatabaseEntry> rows) {
    return rows.stream().map(x -> {
      // copy only the columns available on the list page
      HashMap<String, Object> filteredCopy = new HashMap<>();
      resource.getIndexColumns().forEach(z -> {
        Optional<ColumnDefinition> columnDefinition = resource.findColumnDefinitionByName(z);
        columnDefinition.map(def -> {
          if(def.getColumnValueTransformer() != null) {
            return filteredCopy.put(z, def.getColumnValueTransformer().apply(x.getProperties().get(z)));
          } else {
            return filteredCopy.put(z, x.getProperties().get(z));
          }
        });
      });

          // add a row identifier
        filteredCopy.put("DT_RowId", extractUrlEncodedPK(resource.getPrimaryKeys(), x.getProperties()));
        return filteredCopy;
      }
    ).collect(Collectors.toList());
  }
  
  /**
   * Extracts a URL encoded primary key string from a map of object properties, which may be used to generate edit/delete URLs.
   *
   * @param primaryKeyColumns column names of the primary keys
   * @param properties        map of all available properties
   * @return a URL encoded sequence of primary keys, separated by a forward slash
   */
  protected static String extractUrlEncodedPK(List<String> primaryKeyColumns, Map<String, Object> properties) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < primaryKeyColumns.size(); i++) {
      if(i > 0) {
        sb.append("/");
      }
      String columnName = primaryKeyColumns.get(i);
      String objectValue = String.valueOf(properties.get(columnName));
      try {
        sb.append(URLEncoder.encode(objectValue, "UTF-8"));
      } catch(UnsupportedEncodingException e) {
        logger.error("Unsupported encoding exception", e);
      }
    }
    return sb.toString();
  }
}
