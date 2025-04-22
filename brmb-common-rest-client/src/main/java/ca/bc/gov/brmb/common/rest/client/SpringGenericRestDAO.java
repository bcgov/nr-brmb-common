package ca.bc.gov.brmb.common.rest.client;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.MultipartBodyBuilder.PartBuilder;
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
import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;
import ca.bc.gov.brmb.common.rest.resource.transformers.TransformerException;
import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.rest.client.factory.MessageListFactory;
import ca.bc.gov.brmb.common.rest.client.factory.RedirectFactory;
import ca.bc.gov.brmb.common.rest.client.factory.ResourceFactory;
import reactor.core.publisher.Flux;

public class SpringGenericRestDAO<T> extends GenericRestDAO<T> {
	
	static final Logger logger = LoggerFactory
			.getLogger(SpringGenericRestDAO.class);
	
	private ResourceFactory<T> resourceFactory = new ResourceFactory<T>() {

		@Override
		public T getResource(Transformer transformer, byte[] body, Class<T> clazz, String eTag, Long cacheExpiresMillis) throws TransformerException {
			logger.debug("<getResource");
			T result = null;
			
			Object responseResource = transformer.unmarshall(body, clazz);
			
			
			if(responseResource instanceof BaseResource) {
				BaseResource baseResource = (BaseResource) responseResource;
				baseResource.setETag(eTag);
				baseResource.setCacheExpiresMillis(cacheExpiresMillis);
			}
			
			result = cast(responseResource);
			
			logger.debug(">getResource "+result);
			return result;
		}};
	
	private MessageListFactory messageListFactory = new MessageListFactory() {

		@Override
		public MessageListRsrc getMessageList(Transformer transformer, byte[] body) {
			logger.debug("<getMessageList");
			MessageListRsrc result = null;
			
			result = new MessageListRsrc();
			if(body!=null) {
				try {
					result = (MessageListRsrc) transformer.unmarshall(body, MessageListRsrc.class);
				} catch(ClassCastException e) {
					logger.warn(e.getMessage());
					String responseString = new String(body);
					logger.debug("responseString=\n"+responseString);
					result = new MessageListRsrc(responseString);
				} catch(TransformerException e) {
					logger.warn(e.getMessage());
					String responseString = new String(body);
					logger.debug("responseString=\n"+responseString);
					result = new MessageListRsrc(responseString);
				}
			}
			
			logger.debug(">getMessageList "+result);
			return result;
		}};
		
		private RedirectFactory redirectfactory = new RedirectFactory() {

			@Override
			public Redirect getRedirect(Transformer transformer, byte[] body) throws TransformerException {
				logger.debug("<getRedirect");
				Redirect result = null;
				
				result = (Redirect) transformer.unmarshall(body, Redirect.class);
				
				logger.debug(">getRedirect "+result);
				return result;
			}};

	public SpringGenericRestDAO(Class<T> clazz, String clientVersion, String requestIdHeader, String log4jRequestIdMdcKey) {
		super(clazz, clientVersion, requestIdHeader, log4jRequestIdMdcKey);
	}
	
	@Override
	public Response<T> Process(Transformer transformer, String urlString, String method, String eTag, Object resource, MultipartData[] files, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {
		logger.debug("<Process "+urlString);
		
		long startTime = System.currentTimeMillis();

		Response<T> result = null;
		
		boolean isMultipart = (files!=null&&files.length>0);
		logger.info("isMultipart="+isMultipart);
		
		logger.info("Rest call: "+urlString);
		
		if(urlString==null) {
			throw new UnsupportedOperationException("Url cannot be null.");
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
				
			} else if("POST".equals(method) || "PUT".equals(method)|| "PATCH".equals(method)) {
				
				String resourceString = transformer.marshall(resource);
				
				byte[] resourceBytes = resourceString.getBytes("UTF-8");
			
				if(isMultipart) {
					
					//requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

					MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
					
					multipartBodyBuilder.part("resource", resourceBytes, MediaType.parseMediaType(transformer.getContentType()));
					
					for(int i=0;i<files.length;++i) {
						MultipartData file = files[i];
						
						final String fileName = file.getFileName();
						logger.debug("fileName="+fileName);
						
						String fileContentType = file.getContentType();
						logger.debug("fileContentType="+fileContentType);
						if(fileContentType==null||fileContentType.trim().length()==0) {
							throw new IllegalArgumentException("MultipartData contentType is required.");
						}

						String partName = i==0?"file":"file"+1;

						/*
						 * Update May 3rd, 2022
						 * Adding InputStream to MultipartData to support streaming in WFDM
						 * Existing Byte[] resource should work as <=1.3.1. For 1.3.2
						 * You can supply an InputStream to Multipart data.
						 */
						logger.debug("MultipartData is stream = " + file.isStreamable());

						PartBuilder partbuilder = null;
						if (file.isStreamable()) {
							InputStream inputStream = file.isStreamable() ? file.getInputStream() : null;
							if(file.isStreamable() && inputStream == null) {
								throw new IllegalArgumentException("MultipartData stream is required.");
							}
							MultipartInputStreamResource inputStreamResource = new MultipartInputStreamResource(inputStream, file.getFileName());
							partbuilder = multipartBodyBuilder.part(partName, inputStreamResource, MediaType.parseMediaType(fileContentType));
						} else {
							byte[] fileBytes = file.getBytes();
							if(fileBytes==null||fileBytes.length==0) {
								throw new IllegalArgumentException("MultipartData bytes is required.");
							}

							partbuilder = multipartBodyBuilder.part(partName, fileBytes, MediaType.parseMediaType(fileContentType));
						}

						if (partbuilder != null && fileName != null && fileName.trim().length() > 0) {
							partbuilder.header("Content-Disposition", "form-data; name=\"" + partName + "\"; filename=\"" + fileName + "\"");
						}
					}
					
					requestHeadersSpec.headers(httpHeaders -> httpHeaders.putAll(requestHeaders));
					
					//body = BodyInserters.fromMultipartData(multipartBodyBuilder.build());
					
					body = multipartBodyBuilder.build();
					
				} else {
					// MediaType.MULTIPART_FORM_DATA
					requestHeaders.setContentType(MediaType.parseMediaType(transformer.getContentType()));
					
					requestHeadersSpec.headers(httpHeaders -> httpHeaders.putAll(requestHeaders));
					
					body = resourceBytes;
				}
				
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
			
			Long cacheExpiresMillis = Long.valueOf(responseHeaders.getExpires());

			if(statusCode.value() > 500) {
				throw new ServerErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 500) {
				
				MessageListRsrc messages = this.messageListFactory.getMessageList(transformer, responseEntity.getBody());
				
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
				result = new Response<T>(statusCode.value(), null);
			} else if(statusCode.value() == 403) {
				throw new ForbiddenException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 402) {
				throw new ClientErrorException(statusCode.value(), statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 401) {
				throw new UnauthorizedException(statusCode.getReasonPhrase());	
			} else if(statusCode.value() == 400) {
				
				MessageListRsrc messages = this.messageListFactory.getMessageList(transformer, responseEntity.getBody());
				
				throw new BadRequestException(messages.getMessages());
				
			} else if(statusCode.value() == 307) {
				
				boolean useLocation = false;
				Redirect redirect = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						redirect = redirectfactory.getRedirect(transformer, responseEntity.getBody());
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
				
				result = new Response<T>(statusCode.value(), null);
				
			} else if(statusCode.value() == 303) {
				
				boolean useLocation = false;
				Redirect redirect = null;
				if(responseEntity.getBody()==null) {
					useLocation = true;
				} else {
					try {
						redirect = redirectfactory.getRedirect(transformer, responseEntity.getBody());
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
						redirect = redirectfactory.getRedirect(transformer, responseEntity.getBody());
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
						Redirect redirect = redirectfactory.getRedirect(transformer, responseEntity.getBody());
						for(RelLink link:redirect.getLinks()) {
							if(BaseResourceTypes.REDIRECT.equals(link.getRel())) {
								location = link.getHref();
								break;
							}
						}
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
				result = new Response<T>(statusCode.value(), null);
			} else if(statusCode.value() == 204) {
				result = new Response<T>(statusCode.value(), null);
			} else if(statusCode.value() == 203 || statusCode.value() == 202|| statusCode.value() == 201 || statusCode.value() == 200) {
				
				if(responseEntity.getBody()==null) {
					result = new Response<T>(statusCode.value(), null);
				} else {
					
					T genericResource = this.resourceFactory.getResource(transformer, responseEntity.getBody(), getClazz(), eTag, cacheExpiresMillis);
					
					result = new Response<T>(statusCode.value(), genericResource, contentType);
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

	public void setResourceFactory(ResourceFactory<T> resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public void setMessageListFactory(MessageListFactory messageListFactory) {
		this.messageListFactory = messageListFactory;
	}

	public void setRedirectfactory(RedirectFactory redirectfactory) {
		this.redirectfactory = redirectfactory;
	}
	
}
