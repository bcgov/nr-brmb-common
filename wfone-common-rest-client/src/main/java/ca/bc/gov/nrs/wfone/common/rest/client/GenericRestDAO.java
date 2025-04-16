package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.brmb.common.rest.resource.BaseResource;
import ca.bc.gov.brmb.common.rest.resource.RelLink;
import ca.bc.gov.brmb.common.rest.resource.TypedResource;
import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;
import ca.bc.gov.nrs.wfone.common.rest.client.factory.MessageListFactory;
import ca.bc.gov.nrs.wfone.common.rest.client.factory.RedirectFactory;
import ca.bc.gov.nrs.wfone.common.rest.client.factory.ResourceFactory;


public abstract class GenericRestDAO<T> {
	
	private Class<T> clazz;
	private String clientVersion;
	private String requestIdHeader;
	private String log4jRequestIdMdcKey;

	public GenericRestDAO(Class<T> clazz, String clientVersion, String requestIdHeader, String log4jRequestIdMdcKey) {
		this.clazz = clazz;
		this.clientVersion = clientVersion;
		this.requestIdHeader = requestIdHeader;
		this.log4jRequestIdMdcKey = log4jRequestIdMdcKey;
	}

	public abstract void setResourceFactory(ResourceFactory<T> resourceFactory);

	public abstract void setMessageListFactory(MessageListFactory messageListFactory);

	public abstract void setRedirectfactory(RedirectFactory redirectfactory);
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, parent, resource, null, (Map<String,String>)null, null, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource resource, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, resource, resource, null, (Map<String,String>)null, null, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource resource, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, resource, resource, null, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, parent, resource, null, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, parent, resource, null, headerParams, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, MultipartData[] files, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(resourceType, transformer, parent, resource, files, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, MultipartData[] files, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		String urlString = null;
		String method = null;
		for(RelLink link:parent.getLinks()) {
			if(link.getRel().equals(resourceType)) {
				urlString = link.getHref();
				method = link.getMethod();
				break;
			}
		}
		
		String eTag = resource.getQuotedETag();
		
		Response<T> result = Process(transformer, urlString, method, eTag, resource, files, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(Transformer transformer, String urlString, String method, String eTag, Object resource, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(transformer, urlString, method, eTag, resource, null, (Map<String,String>)null, null, webClient);

		return result;
	}
	
	public Response<T> Process(Transformer transformer, String urlString, String method, String eTag, Object resource, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(transformer, urlString, method, eTag, resource, null, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
	
	public Response<T> Process(Transformer transformer, String urlString, String method, String eTag, Object resource, MultipartData[] files, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<T> result = Process(transformer, urlString, method, eTag, resource, files, (Map<String,String>)null, queryParams, webClient);

		return result;
	}
		
	public abstract Response<T> Process(Transformer transformer, String urlString, String method, String eTag, Object resource, MultipartData[] files, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException;
	
	protected Map<String, String> parseHeaderDirectives(String contentDisposition) {
		Map<String, String> result = new HashMap<String, String>();
		
		if(contentDisposition!=null&&contentDisposition.trim().length()>0) {
			
			String[] directives = contentDisposition.split(";");
			for(String directive:directives) {

				if(directive.indexOf("=")!=-1) {
					
					String[] pair = directive.split("=");
					if(pair.length==2) {
						
						String name = pair[0].trim();
						String value = pair[1].trim();
						result.put(name, value);
					}
				}
			}
		}
		
		return result;
	}	
	
	@SuppressWarnings({ "unchecked", "hiding" })
	protected <T> T cast(Object item) {
		return (T)item;
	}

	protected Class<T> getClazz() {
		return clazz;
	}

	protected String getClientVersion() {
		return clientVersion;
	}

	protected String getRequestIdHeader() {
		return requestIdHeader;
	}

	protected String getLog4jRequestIdMdcKey() {
		return log4jRequestIdMdcKey;
	}
	
}
