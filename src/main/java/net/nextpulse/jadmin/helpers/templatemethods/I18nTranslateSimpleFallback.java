package net.nextpulse.jadmin.helpers.templatemethods;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Freemarker method that facilitates translating template strings, falling back on the last chunk of the translation
 * key when no value was found. Used to display resource column names, which would otherwise return a less
 * readable string, such as resources.locations.name.
 *
 * @author yholkamp
 */
public class I18nTranslateSimpleFallback extends I18nTranslate {
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
    
    // either return the translation string or return the last chunk of the translation string
    return new SimpleScalar(translationOpt.orElseGet(() -> {
      String[] keyChunks = ((String) translationKey).split("\\.");
      return keyChunks[keyChunks.length - 1];
    }));
  }
  
}