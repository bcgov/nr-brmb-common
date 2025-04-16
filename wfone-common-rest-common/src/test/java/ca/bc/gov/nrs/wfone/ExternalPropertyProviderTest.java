package ca.bc.gov.nrs.wfone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ca.bc.gov.nrs.common.wfone.rest.spring.ExternalPropertyProvider;

public class ExternalPropertyProviderTest {

  private ExternalPropertyProvider externalPropertyProvider;

  @Before
  public void setUp() {
    externalPropertyProvider = new ExternalPropertyProvider();
  }

  @Test
  public void testCreateHttpRequest() throws URISyntaxException {
    String uriString = "https://example.com/api/properties";
    String accessToken = "testAccessToken";
    URI uri = new URI(uriString);

    HttpRequest request = externalPropertyProvider.createHttpRequest(uri, accessToken);

    assertEquals(uri, request.uri());
    assertEquals("GET", request.method());
    assertTrue(request.headers().map().containsKey("Accept"));
    assertTrue(request.headers().map().containsKey("Authorization"));
    assertEquals("application/json", request.headers().firstValue("Accept").orElse(""));
    assertEquals("Bearer " + accessToken, request.headers().firstValue("Authorization").orElse(""));
  }

  @Test
  public void testFetchExternalProperties() throws URISyntaxException, IOException, InterruptedException {
    String uriString = "https://example.com/api/properties";
    String accessToken = "testAccessToken";
    URI uri = new URI(uriString);

    // Mock the HttpClient and HttpResponse
    HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
    HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);

    // Mock the response behavior
    Mockito.when(mockResponse.statusCode()).thenReturn(200);
    Mockito.when(mockResponse.body()).thenReturn("{\"key1\":\"value1\",\"key2\":\"value2\"}");
    Mockito.when(mockHttpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    // Inject the mock HttpClient into the ExternalPropertyProvider
    ExternalPropertyProvider externalPropertyProvider = new ExternalPropertyProvider() {
      @Override
      protected HttpClient getHttpClient() {
        return mockHttpClient;
      }
    };

    Properties properties = externalPropertyProvider.fetchExternalProperties(uri, accessToken);

    assertEquals("value1", properties.getProperty("key1"));
    assertEquals("value2", properties.getProperty("key2"));
  }

  @Test
  public void testFetchExternalPropertiesAsync() throws URISyntaxException, IOException, InterruptedException {
    String uriString = "https://example.com/api/properties";
    String accessToken = "testAccessToken";
    URI uri = new URI(uriString);

    // Mock the HttpClient and HttpResponse
    HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
    HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);

    // Mock the response behavior
    Mockito.when(mockResponse.statusCode()).thenReturn(200);
    Mockito.when(mockResponse.body()).thenReturn("{\"key1\":\"value1\",\"key2\":\"value2\"}");
    CompletableFuture<HttpResponse<String>> futureResponse = CompletableFuture.completedFuture(mockResponse);
    Mockito.when(mockHttpClient.sendAsync(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class)))
        .thenReturn(futureResponse);

    // Inject the mock HttpClient into the ExternalPropertyProvider
    ExternalPropertyProvider externalPropertyProvider = new ExternalPropertyProvider() {
      @Override
      protected HttpClient getHttpClient() {
        return mockHttpClient;
      }
    };

    CompletableFuture<HttpResponse<String>> future = externalPropertyProvider.fetchExternalPropertiesAsync(uri, accessToken);
    try {
    HttpResponse<String> response = future.get();

    assertEquals(200, response.statusCode());
    assertEquals("{\"key1\":\"value1\",\"key2\":\"value2\"}", response.body());
    } catch (ExecutionException e) {
      assertFalse(true);
    }
  }

  @Test
  public void testJsonToProperties() {
    String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
    Properties properties = ExternalPropertyProvider.jsonToProperties(json);

    assertEquals("value1", properties.getProperty("key1"));
    assertEquals("value2", properties.getProperty("key2"));
  }

  @Test
  public void testPropertiesToJson() {
    Properties properties = new Properties();
    properties.setProperty("key1", "value1");
    properties.setProperty("key2", "value2");

    String json = ExternalPropertyProvider.propertiesToJson(properties);

    assertEquals("{ \"key1\": \"value1\", \"key2\": \"value2\" }", json);
  }
}
