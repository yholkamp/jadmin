package net.nextpulse.jadmin.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Optional;
import java.util.Properties;

/**
 * Simple I18n wrapper that loads the library-provided base.properties file as well as the user-configured language file.
 *
 * @author yholkamp
 */
public class I18n {
  private static final Logger logger = LogManager.getLogger();
  /**
   * Location where translation files are expected to be.
   */
  private static final String I18N_PATH = "/jadmin/i18n";
  /**
   * Currently loaded configuration
   */
  protected static Properties configuration = null;
  /**
   * Configured language identifier
   */
  private static String language = "en";
  
  /**
   * Given the key, returns the translation string or the provided key if the string was not found.
   *
   * @param key    translation key
   * @param params  optional list of parameters to apply to the translation file, using String.format
   * @return translation string or key if the string is not available
   */
  public static String get(String key, Object... params) {
    return getOptional(key, params).orElse(key);
  }
  
  /**
   * Given the key, returns an option containing either nothing or the translation string.
   *
   * @param key    translation key
   * @param params  optional list of parameters to apply to the translation file, using String.format
   * @return optional containing nothing or the translation string
   */
  public static Optional<String> getOptional(String key, Object... params) {
    if(configuration == null) {
      loadProperties();
    }
  
    if(configuration.containsKey(key)) {
      try {
        return Optional.of(String.format((String) configuration.get(key), (Object[]) params));
      } catch(IllegalFormatException e) {
        logger.error("Could not process the specified format for key {}", key, e);
      }
    }
  
    return Optional.empty();
  }
  
  /**
   * Sets the language when translating strings.
   * 
   * @param language  language string, defaults to "en" and should correspond to the file name of the i18n file, i.e. resources/jadmin/i18n/{{language}}.properties
   */
  public static void setLanguage(String language) {
    I18n.language = language;
    loadProperties();
  }
  
  /**
   * Loads the default translation file as well as the properties file for the configured language.
   */
  private static void loadProperties() {
    configuration = new Properties();
    loadFile("base");
    loadFile(language);
  }
  
  /**
   * Loads the provided properties file name, assuming it exists 
   * 
   * @param filename
   */
  private static void loadFile(String filename) {
    try(InputStream in = I18n.class.getResourceAsStream(I18N_PATH + "/" + filename + ".properties")) {
      configuration.load(in);
    } catch(NullPointerException e) {
      logger.error("Translation file " + I18N_PATH + "/{}.properties does not exist in the class path", I18n.language);
    } catch(IOException e) {
      logger.error("Translation file " + I18N_PATH + "/{}.properties could not be loaded from the class path", I18n.language, e);
    }
  }
}
