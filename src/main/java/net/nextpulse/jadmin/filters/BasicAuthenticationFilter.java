package net.nextpulse.jadmin.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.FilterImpl;
import spark.Request;
import spark.Response;
import spark.utils.SparkUtils;

import java.util.Base64;

import static spark.Spark.halt;

/**
 * Example authentication filter that provides a simple user &amp; password prompt using Basic Authentication.
 */
public class BasicAuthenticationFilter extends FilterImpl {
  private static final Logger logger = LogManager.getLogger();
  
  private static final String AUTHENTICATION_PREFIX = "Basic ";
  private static final String AUTHENTICATION_HEADER = "Authorization";
  private final String username;
  private final String password;
  
  /**
   * Creates a new basic authentication filter that only accepts the provided username and password.
   *
   * @param username accepted username
   * @param password accepted password
   */
  public BasicAuthenticationFilter(String username, String password) {
    super(SparkUtils.ALL_PATHS, "*/*");
    this.username = username;
    this.password = password;
  }
  
  /**
   * Handle method invoked by the SparkJava framework.
   * 
   * @param request   user request
   * @param response  response object
   * @throws Exception if we could not authenticate, halting further processing.
   */
  @Override
  public void handle(Request request, Response response) throws Exception {
    if(!authenticateWithHeader(request.headers(AUTHENTICATION_HEADER))) {
      response.header("WWW-Authenticate", "Basic");
      throw halt(401);
    }
  }
  
  /**
   * Attempt to authenticate the request using the provided Authentication header
   *
   * @param rawHeader user provided header
   * @return true iff the header contained the correct user and password
   */
  protected boolean authenticateWithHeader(String rawHeader) {
    // we require a header
    if(rawHeader == null) {
      logger.debug("No authentication header provided");
      return false;
    }
    
    // and we need at least the "Basic ENCODED_DATA" portion 
    String[] splitHeader = rawHeader.split(AUTHENTICATION_PREFIX, 2);
    if(splitHeader.length != 2) {
      logger.debug("Provided authentication header did not consist of username and password");
      return false;
    }
    
    // attempt to decode the header
    try {
      String decodedHeader = new String(Base64.getDecoder().decode(splitHeader[1]));
      String[] userAndPass = decodedHeader.split(":", 2);
      
      return username.equals(userAndPass[0]) && password.equals(userAndPass[1]);
    } catch(IllegalArgumentException e) {
      logger.info("Could not decode the authentication header", e);
      return false;
    }
  }
  
}
