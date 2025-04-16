package ca.bc.gov.nrs.common.wfone.rest.spring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExternalPropertyProvider is a helper class used by the Spring Framework
 * API's to provide a common interface for loading properties from an
 * external API
 */
public class ExternalPropertyProvider {
  private static final Logger logger = LoggerFactory.getLogger(ExternalPropertyProvider.class);

  public ExternalPropertyProvider() { /*Empty Constructor */ }

  /**
   * Fetch External Properties from the provided URI
   * @param uri A String representing the URI to call for property fetch
   * @throws URISyntaxException
   * @throws InterruptedException 
   * @throws IOException 
  */
  public Properties fetchExternalProperties(String uri, String accessToken) throws URISyntaxException, IOException, InterruptedException {
    return this.fetchExternalProperties(new URI(uri), accessToken);
  }

  /**
   * Fetch External Properties from the provided URI
   * @param uri A URI to call for property fetch
   * @throws InterruptedException 
   * @throws IOException 
   */
  public Properties fetchExternalProperties(URI uri, String accessToken) throws IOException, InterruptedException{
    logger.debug(" >> fetchExternalProperties()");
    logger.debug("    Attempting to fetch properties from {}", uri);
    
    Properties properties;
    HttpRequest request = createHttpRequest(uri, accessToken);

    logger.debug("    Calling the URI and waiting for the response...");

    // Send the request and receive the response
    HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    logger.debug("    ## Request complete ##");
    logger.debug("    >> Response Code: {}", response.statusCode());
    logger.debug("    >> Response Body: {}", response.body());

    if (response.statusCode() == 200) {
      logger.debug("    Converting response to Properties set");
      // Convert response to property set
      properties = jsonToProperties(response.body());
    } else {
      throw new IOException("Failed to fetch from External Service. Status " + response.statusCode());
    }

    logger.debug(" << fetchExternalProperties()");
    return properties;
  }

  /**
   * Fetch External Properties from the provided URI asynchronously
   * @param uri A String representing the URI to call for property fetch
   * @throws URISyntaxException
   */
  public CompletableFuture<HttpResponse<String>> fetchExternalPropertiesAsync(String uri, String accessToken) throws URISyntaxException {
    return this.fetchExternalPropertiesAsync(new URI(uri), accessToken);
  }

  /**
   * Fetch External Properties from the provided URI asynchronously
   * @param uri A URI to call for property fetch
   * @throws URISyntaxException
   */
  public CompletableFuture<HttpResponse<String>> fetchExternalPropertiesAsync(URI uri, String accessToken) {
    logger.debug(" >> fetchExternalProperties()");
    logger.debug("    Attempting to fetch properties from {}", uri);

    HttpRequest request = createHttpRequest(uri, accessToken);

    logger.debug("    Calling the URI and waiting for the response...");
    logger.debug(" << fetchExternalProperties()");

    return getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
    
    /* likely async handler structure
    CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    futureResponse.thenAccept(response -> {
      logger.debug("    ## Request complete ##");
      logger.debug("    >> Response Code: {}", response.statusCode());
      logger.debug("    >> Response Body: {}", response.body());

      logger.debug("    Converting response to Properties set");

      // Convert response to property set
      properties = jsonToProperties(response.body());
    }).join();
    */
  }

  /**
   * Create a HttpRequest object with the provided URI and access token
   * @param uri URI to call
   * @param accessToken Access token to use for the request
   * @return HttpRequest object
   */
  public HttpRequest createHttpRequest(URI uri, String accessToken) {
    return HttpRequest.newBuilder().uri(uri).GET()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + accessToken)
            .build();
  }

  /**
   * Convert a JSON string to a Properties object
   * @param json JSON string to convert
   * @return Properties object
   */
  public static Properties jsonToProperties(String json) {
    Properties properties = new Properties();

    json = json.trim().replaceAll("[{}\"]", ""); // Remove `{}`, `"` for clean parsing
    String[] keyValues = json.split(",");

    for (String keyValue : keyValues) {
        String[] entry = keyValue.split(":");

        if (entry.length == 2) {
            String key = entry[0].trim();
            String value = entry[1].trim();
            properties.setProperty(key, value);
        }
    }
    return properties;
  }

  /**
   * Convert a Properties object to a JSON string
   * @param properties Properties object to convert
   * @return JSON string
   */
  public static String propertiesToJson(Properties properties) {
      return "{ " + properties.entrySet()
              .stream()
              .map(entry -> "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"")
              .collect(Collectors.joining(", ")) + " }";
  }

  /**
   * Get the HttpClient to use for making requests
   * @return HttpClient
   */
  protected HttpClient getHttpClient() {
    return HttpClient.newHttpClient();
  }
}
