package net.nextpulse.jadmin.helpers;

import net.nextpulse.jadmin.ColumnDefinition;
import net.nextpulse.jadmin.Resource;
import net.nextpulse.jadmin.dao.DataAccessException;
import net.nextpulse.jadmin.elements.FormButtons;
import net.nextpulse.jadmin.elements.FormInput;
import net.nextpulse.jadmin.elements.FormInputGroup;
import net.nextpulse.jadmin.schema.ResourceSchemaProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Class that decorates a Resource object using type information retrieved from the provided DBMS.
 *
 * @author yholkamp
 */
public class ResourceDecorator implements BiConsumer<Resource, ResourceSchemaProvider> {
  private static final Logger logger = LogManager.getLogger();

  /**
   * @param resource
   * @param schemaProvider
   * @throws SQLException
   */
  private static void updateResourceWithColumns(Resource resource, ResourceSchemaProvider schemaProvider) {
    try {
      List<ColumnDefinition> columnDefinitions = schemaProvider.getColumnDefinitions();
      resource.setColumnDefinitions(columnDefinitions);
    } catch(DataAccessException e) {
      logger.error("Could not retrieve primary keys from DAO", e);
    }
  }

  /**
   * Initializes the provided resourceSchemaProvider with input fields for all non-primary key fields and index columns for all columns.
   *
   * @param resource
   */
  private static void createDefaultResourceFormPages(Resource resource) {
    FormInputGroup inputGroup = new FormInputGroup();

    resource.getColumnDefinitions().forEach(x -> {
      logger.trace("Adding default for {} col {}", resource.getTableName(), x.getName());
      if(!x.isKeyColumn()) {
        inputGroup.addInput(new FormInput(x.getName(), x.getType()));
        resource.addEditableColumn(x.getName());
      }
      resource.getIndexColumns().add(x.getName());
    });
    resource.getFormPage().add(inputGroup);
    resource.getFormPage().add(new FormButtons());
  }

  /**
   * Decorates the provided resourceSchemaProvider with information provided by the DBMS.
   *
   * @param resource       object to decorate
   * @param schemaProvider class that provides schema info for this resource.
   */
  public void accept(Resource resource, ResourceSchemaProvider schemaProvider) {
    logger.trace("Decorating {}", resource.getTableName());
//    updateResourceWithPrimaryKeys(resource, schemaProvider);
    updateResourceWithColumns(resource, schemaProvider);
    createDefaultResourceFormPages(resource);
  }
}
