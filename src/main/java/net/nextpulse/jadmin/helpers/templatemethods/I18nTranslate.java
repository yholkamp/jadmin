package net.nextpulse.jadmin.helpers.templatemethods;

import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import net.nextpulse.jadmin.helpers.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Freemarker method that facilitates translating template strings.
 *
 * @author yholkamp
 */
public class I18nTranslate implements TemplateMethodModelEx {
  private static final Logger logger = LogManager.getLogger();
  
  /**
   * Translation method that takes a translation key and optionally a list of parameters and returns the translation
   * string.
   *
   * @param arguments arguments, at least the translation string key
   * @return the translated string
   * @throws TemplateModelException if no translation key was provided
   */
  @Override
  public Object exec(List arguments) throws TemplateModelException {
    logger.trace("I18n template method called with {}", arguments);
    if(arguments.isEmpty()) {
      throw new TemplateModelException("At least the translation key should be provided.");
    }
    Object translationKey = getTranslationKey(arguments);
    Optional<String> translationOpt = getTranslation((String) translationKey, arguments);
    
    // either return the translation string or the translation string
    return new SimpleScalar(translationOpt.orElse((String) translationKey));
  }
  
  /**
   * Retrieves the translation key, the first argument, or throws an error.
   *
   * @param arguments list of arguments
   * @return translation key
   * @throws TemplateModelException if no valid argument was provided
   */
  Object getTranslationKey(List arguments) throws TemplateModelException {
    Object translationKey = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
    if(!(translationKey instanceof String)) {
      throw new TemplateModelException("The translation key should be a string.");
    }
    return translationKey;
  }
  
  /**
   * Retrieves the translation for the provided key and arguments.
   *
   * @param translationKey translation key
   * @param arguments      optional list of arguments to pass to the translation string
   * @return translated string
   */
  Optional<String> getTranslation(String translationKey, List arguments) {
    Object[] params = new Object[0];
    if(arguments.size() > 1) {
      params = arguments.stream().skip(1).map(this::runtimeThrowingUnwrap).toArray();
    }
    return I18n.getOptional(translationKey, params);
  }
  
  /**
   * Unwraps a Freemarker TemplateModel object to a regular object, throwing runtime rather than checked exceptions to
   * allow this method to be used in a Stream.map call.
   *
   * @param x object to unwrap
   * @return unwrapped object
   * @throws RuntimeException if the provided model could not be unwrapped
   */
  private Object runtimeThrowingUnwrap(Object x) throws RuntimeException {
    try {
      Object unwrappedValue = DeepUnwrap.unwrap((TemplateModel) x);
      
      // reduce a BigDecimal to a primitive value for more convenient String.format usage
      if(unwrappedValue instanceof BigDecimal) {
        BigDecimal value = (BigDecimal) unwrappedValue;
        if(value.remainder(BigDecimal.ONE).signum() > 0) {
          return value.doubleValue();
        } else {
          return value.intValue();
        }
      } else {
        return unwrappedValue;
      }
    } catch(TemplateModelException e) {
      throw new RuntimeException(e);
    }
  }
  
}