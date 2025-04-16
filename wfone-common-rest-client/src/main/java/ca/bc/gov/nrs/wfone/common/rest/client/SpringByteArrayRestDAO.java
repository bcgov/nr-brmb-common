package ca.bc.gov.nrs.wfone.common.rest.client;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import ca.bc.gov.brmb.common.rest.resource.BaseResource;
import ca.bc.gov.brmb.common.rest.resource.HeaderConstants;
import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.rest.resource.Redirect;
import ca.bc.gov.brmb.common.rest.resource.RelLink;
import ca.bc.gov.brmb.common.rest.resource.TypedResource;
import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;
import ca.bc.gov.brmb.common.rest.resource.transformers.TransformerException;
import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;

public class SpringByteArrayRestDAO extends ByteArrayRestDAO {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SpringByteArrayRestDAO.class);

	public SpringByteArrayRestDAO(String clientVersion, String requestIdHeader, String log4jRequestIdMdcKey) {
		super(clientVersion, requestIdHeader, log4jRequestIdMdcKey);
	}
		
	@Override
	public Response<byte[]> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {
		logger.debug("<Process "+resourceType);
		
		long startTime = System.currentTimeMillis();

		Response<byte[]> result = null;
		
		String urlString = null;
		String method = null;
		for(RelLink link:parent.getLinks()) {
			if(link.getRel().equals(resourceType)) {
				urlString = link.getHref();
				method = link.getMethod();
				break;
			}
		}
		
		logger.info("Rest call: "+urlString);
		
		if(urlString==null) {
			throw new UnsupportedOperationException(resourceType+" Not Supported");
		}
		
		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
		UriBuilder uriBuilder = uriBuilderFactory.uriString(urlString);
		
		if(queryParams != null) {
			
			for(String key:queryParams.keySet()) {
				String value = queryParams.get(key);
				if(value==null) {
					value = "";
				}
				uriBuilder.replaceQueryParam(key, value);
			}
			
			urlString = uriBuilder.build().toString();
			
			logger.debug("urlString="+urlString);
		}
		
		try {
		
		URL url = new URL(urlString);
		
		String queryString = url.getQuery();
		logger.debug("query="+queryString);
		
		if(queryString!=null) {
			urlString = urlString.substring(0, (urlString.length()-queryString.length())-1);
			
			logger.debug("urlString="+urlString);
			
			urlString = urlString +"?" + queryString;
			
		} 
		
		logger.debug("urlString="+urlString);
		
		url = new URL(urlString);

			HttpHeaders requestHeaders = new HttpHeaders();
			
			if (headerParams != null) {
				
				for (String key : headerParams.keySet()) {
					
					String value = headerParams.get(key);

					if (value != null) {
						
						if (key.contains("\n") || value.contains("\n")) {
							
							logger.warn("Ignoring header with invalid value: " + key);
						} else {
							
							logger.debug("Adding header "+key+"=" + value);
							requestHeaders.set(key, value);
						}
					}
				}
			}
			
			String eTag = resource.getQuotedETag();
			if(eTag!=null) {
			
				requestHeaders.set("If-Match", eTag);
				
				logger.debug("If-Match:"+eTag);
			}
			
			requestHeaders.set("Accept", transformer.getContentType());
			
			if(getLog4jRequestIdMdcKey()!=null&&getLog4jRequestIdMdcKey().trim().length()>0&&getRequestIdHeader()!=null&&getRequestIdHeader().trim().length()>0) {
				String requestId = MDC.get(getLog4jRequestIdMdcKey());
				if(requestId!=null) {
					logger.debug("requestId=" + requestId);
					requestHeaders.set(getRequestIdHeader(), requestId);
				}
			}
			
			if(getClientVersion()!=null&&getClientVersion().trim().length()>0) {
				requestHeaders.set(HeaderConstants.VERSION_HEADER, getClientVersion());
			}
			
			RequestHeadersSpec<?> requestHeadersSpec = webClient.method(HttpMethod.valueOf(method)).uri(url.toURI());
			
			Object body = null;
			
			if("GET".equals(method) || "DELETE".equals(method)) {
				
				requestHeadersSpec.headers(httpHeaders -> httpHeaders.putAll(requestHeaders));
				
			} else if("POST".equals(method) || "PUT".equals(method)) {
				
				String resourceString = transformer.marshall(resource);

				byte[] resourceBytes = resourceString.getBytes("UTF-8");
			
				requestHeaders.setContentType(MediaType.parseMediaType(transformer.getContentType()));
				
				requestHeadersSpec.headers(httpHeaders -> httpHeaders.putAll(requestHeaders));
				
				body = resourceBytes;
			} 
			
			if(body!=null) {
				
				requestHeadersSpec = ((RequestBodySpec)requestHeadersSpec).bodyValue(body);
			}
			
			ClientResponse clientResponse = requestHeadersSpec.exchange().block();
			
			ResponseEntity<byte[]> responseEntity = clientResponse.toEntity(byte[].class).block();
			
			HttpStatus statusCode = responseEntity.getStatusCode();
			logger.info("Rest call response: "+statusCode.value()+":"+statusCode.getReasonPhrase());
			
			HttpHeaders responseHeaders = responseEntity.getHeaders();
			
			eTag = responseHeaders.getETag();
			logger.debug("eTag="+eTag);
			
			String responseVersion = responseHeaders.getFirst(HeaderConstants.VERSION_HEADER);
			logger.debug("responseVersion="+responseVersion);
			if(getClientVersion()!=null&&getClientVersion().trim().length()>0) {
				if(!getClientVersion().equals(responseVersion)) {
					String message = "The reponse version '"+responseVersion+"' does not match the client version '"+getClientVersion()+"'.";
					logger.warn(message);
				}
			}
			
			String contentType = null;
			if(responseHeaders.getContentType()!=null) {
				contentType = responseHeaders.getContentType().toString();
			}

			if(statusCode.value() > 500) {
				throw new ServerErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 500) {
				
				MessageListRsrc messages = new MessageListRsrc();
				if(responseEntity.getBody()!=null) {
					try {
						messages = (MessageListRsrc) transformer.unmarshall(responseEntity.getBody(), MessageListRsrc.class);
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						String responseString = new String(responseEntity.getBody());
						logger.debug("responseString=\n"+responseString);
						messages = new MessageListRsrc(responseString);
					} catch(TransformerException e) {
						logger.warn(e.getMessage());
						String responseString = new String(responseEntity.getBody());
						logger.debug("responseString=\n"+responseString);
						messages = new MessageListRsrc(responseString);
					}
				}
				
				throw new ServerErrorException(statusCode.value(), messages.getMessages());	

			} else if(statusCode.value() == 417) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());			
			} else if(statusCode.value() == 416) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());		
			} else if(statusCode.value() == 415) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());		
			} else if(statusCode.value() == 414) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());		
			} else if(statusCode.value() == 413) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());
			} else if(statusCode.value() == 412) {
				throw new PreconditionFailedException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 411) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 410) {
				throw new GoneException(statusCode.getReasonPhrase());
			} else if(statusCode.value() == 409) {
				throw new ConflictException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 408) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 407) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 406) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 405) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());				
			} else if(statusCode.value() == 404) {
				result = new Response<byte[]>(statusCode.value(), null);
			} else if(statusCode.value() == 403) {
				throw new ForbiddenException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 402) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 401) {
				throw new UnauthorizedException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 400) {
				
				MessageListRsrc messages = new MessageListRsrc();
				if(responseEntity.getBody()!=null) {
					try {
						messages = (MessageListRsrc) transformer.unmarshall(responseEntity.getBody(), MessageListRsrc.class);
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						String responseString = new String(responseEntity.getBody());
						logger.debug("responseString=\n"+responseString);
						messages = new MessageListRsrc(responseString);
					} catch(TransformerException e) {
						logger.warn(e.getMessage());
						String responseString = new String(responseEntity.getBody());
						logger.debug("responseString=\n"+responseString);
						messages = new MessageListRsrc(responseString);
					}
				}
				
				throw new BadRequestException(messages.getMessages());
				
			} else if(statusCode.value() == 307) {
				
				boolean useLocation = false;
				Redirect redirect = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						redirect = (Redirect) transformer.unmarshall(responseEntity.getBody(), Redirect.class);
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					} catch (TransformerException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					}
				}
				
				if(useLocation) {
					String location = responseHeaders.getFirst("Location");
					redirect = new Redirect();
					redirect.getLinks().add(new RelLink(BaseResourceTypes.REDIRECT, location, method));
				}
				
				if(!("GET".equals(method) || "HEAD".equals(method))) {
					String location = null;
					if(redirect!=null) {
						for(RelLink link:redirect.getLinks()) {
							if(BaseResourceTypes.REDIRECT.equals(link.getRel())) {
								location = link.getHref();
								break;
							}
						}
					}
					throw new RedirectException(statusCode.value(), statusCode.getReasonPhrase(), location);
				}
				
				result = this.Process(BaseResourceTypes.REDIRECT, transformer, redirect, queryParams, webClient);
				
			} else if(statusCode.value() == 305) {
				
				String location = responseHeaders.getFirst("Location");
				throw new RedirectException(statusCode.value(), statusCode.getReasonPhrase(), location);
			
			} else if(statusCode.value() == 304) {
				
				result = new Response<byte[]>(statusCode.value(), null);
				
			} else if(statusCode.value() == 303) {
				
				boolean useLocation = false;
				Redirect redirect = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						redirect = (Redirect) transformer.unmarshall(responseEntity.getBody(), Redirect.class);
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					} catch (TransformerException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					}
				}
				
				if(useLocation) {
					String location = responseHeaders.getFirst("Location");
					redirect = new Redirect();
					redirect.getLinks().add(new RelLink(BaseResourceTypes.REDIRECT, location, method));
				}
				
				result = this.Process(BaseResourceTypes.REDIRECT, transformer, redirect, queryParams, webClient);
				
			} else if(statusCode.value() == 302) {	
				
				boolean useLocation = false;
				Redirect redirect = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						redirect = (Redirect) transformer.unmarshall(responseEntity.getBody(), Redirect.class);
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					} catch (TransformerException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					}
				}
				
				if(useLocation) {
					String location = responseHeaders.getFirst("Location");
					redirect = new Redirect();
					redirect.getLinks().add(new RelLink(BaseResourceTypes.REDIRECT, location, method));
				}
				
				if(!("GET".equals(method) || "HEAD".equals(method))) {
					String location = null;
					if(redirect!=null) {
						for(RelLink link:redirect.getLinks()) {
							if(BaseResourceTypes.REDIRECT.equals(link.getRel())) {
								location = link.getHref();
								break;
							}
						}
					}
					throw new RedirectException(statusCode.value(), statusCode.getReasonPhrase(), location);
				}
				
				result = this.Process(BaseResourceTypes.REDIRECT, transformer, redirect, queryParams, webClient);
				
			} else if(statusCode.value() == 301) {	
				
				boolean useLocation = false;
				String location = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						Redirect redirect = (Redirect) transformer.unmarshall(responseEntity.getBody(), Redirect.class);
						for(RelLink link:redirect.getLinks()) {
							if(BaseResourceTypes.REDIRECT.equals(link.getRel())) {
								location = link.getHref();
								break;
							}
						}
					} catch(ClassCastException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					} catch (TransformerException e) {
						logger.warn(e.getMessage());
						useLocation = true;
					}
				}
				
				if(useLocation) {
					location = responseHeaders.getFirst("Location");
				}
				
				throw new RedirectException(statusCode.value(), statusCode.getReasonPhrase(), location);
				
			} else if(statusCode.value() == 205) {
				result = new Response<byte[]>(statusCode.value(), null);
			} else if(statusCode.value() == 204) {
				result = new Response<byte[]>(statusCode.value(), null);
			} else if(statusCode.value() == 203 || statusCode.value() == 202|| statusCode.value() == 201 || statusCode.value() == 200) {
				
				if(responseEntity.getBody()==null) {
					result = new Response<byte[]>(statusCode.value(), null);
				} else {
					
					String contentDisposition = responseHeaders.getFirst("Content-Disposition");
					logger.debug("contentDisposition="+contentDisposition);
					Map<String, String> parameters = parseHeaderDirectives(contentDisposition);
					String filename = parameters.get("filename");
					logger.debug("filename="+filename);
				
					byte[] response = responseEntity.getBody();
					
					result = new Response<byte[]>(statusCode.value(), response, contentType);
				}
				
			} else {
				throw new RestDAOException("Unsupported HTTP Response: "+statusCode.value()+" "+statusCode.getReasonPhrase());
			}
			
		} catch (MalformedURLException e) {
			throw new RestDAOException(e);		
		} catch (UnsupportedEncodingException e) {
			throw new RestDAOException(e);		
		} catch (URISyntaxException e) {
			throw new RestDAOException(e);		
		} catch (TransformerException e) {
			throw new RestDAOException(e);		
		}

		long duration = System.currentTimeMillis() - startTime;
		
		double seconds = (double)duration / (double)1000;
		
		logger.info("Rest call completed: "+seconds+" seconds");
		
		logger.debug(">Process");
		return result;
	}
	
}
