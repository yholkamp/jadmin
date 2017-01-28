package net.nextpulse.sparkadmin.helpers;

import net.nextpulse.sparkadmin.ColumnDefinition;
import net.nextpulse.sparkadmin.Resource;
import net.nextpulse.sparkadmin.dao.DataAccessException;
import net.nextpulse.sparkadmin.elements.FormButtons;
import net.nextpulse.sparkadmin.elements.FormInput;
import net.nextpulse.sparkadmin.elements.FormInputGroup;
import net.nextpulse.sparkadmin.schema.ResourceSchemaProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Class that decorates a Resource object using type information retrieved from the provided DBMS.
 *
 * @author yholkamp
 */
public class ResourceDecorator implements BiConsumer<Resource, ResourceSchemaProvider> {
  private static final Logger logger = LogManager.getLogger();

  /**
   * Decorates the provided resourceSchemaProvider with information provided by the DBMS.
   *
   * @param resource        object to decorate
   * @param schemaProvider  class that provides schema info for this resource.
   */
  public void accept(Resource resource, ResourceSchemaProvider schemaProvider) {
    logger.trace("Decorating {}", resource.getTableName());
    updateResourceWithPrimaryKeys(resource, schemaProvider);
    updateResourceWithColumns(resource, schemaProvider);
    createDefaultResourceFormPages(resource);
  }

  /**
   * Updates the resourceSchemaProvider with the primary keys of the table being edited.
   *
   * @param resource
   * @param schemaProvider
   * @throws SQLException
   */
  private static void updateResourceWithPrimaryKeys(Resource resource, ResourceSchemaProvider schemaProvider) {
    try {
      schemaProvider.getKeyColumns().stream().map(ColumnDefinition::getName).forEach(x -> resource.getPrimaryKeys().add(x));
    } catch(DataAccessException e) {
      logger.error("Could not retrieve primary keys from DAO", e);
    }
  }

  /**
   *
   * @param resource
   * @param schemaProvider
   * @throws SQLException
   */
  private static void updateResourceWithColumns(Resource resource, ResourceSchemaProvider schemaProvider) {
    try {
      Collection<ColumnDefinition> columnDefinitions = schemaProvider.getColumnDefinitions();
      resource.getColumnDefinitions().addAll(columnDefinitions);
    } catch(DataAccessException e) {
      logger.error("Could not retrieve primary keys from DAO", e);
    }
  }

  /**
   * Initializes the provided resourceSchemaProvider with input fields for all non-primary key fields and index columns for all columns.
   * @param resource
   */
  private static void createDefaultResourceFormPages(Resource resource) {
    FormInputGroup inputGroup = new FormInputGroup();

    resource.getColumnDefinitions().forEach(x -> {
      logger.trace("Adding default for {} col {}", resource.getTableName(), x.getName());
      if(!resource.getPrimaryKeys().contains(x.getName())) {
        inputGroup.getInputs().add(new FormInput(x.getName(), x.getType()));
        resource.getEditableColumns().add(x.getName());
      }
      resource.getIndexColumns().add(x.getName());
    });
    resource.getFormPage().add(inputGroup);
    resource.getFormPage().add(new FormButtons());
  }
}
