package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.brmb.common.rest.resource.BaseResource;
import ca.bc.gov.brmb.common.rest.resource.TypedResource;
import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;


public abstract class StringRestDAO {

	private String clientVersion;
	private String requestIdHeader;
	private String log4jRequestIdMdcKey;

	public StringRestDAO(String clientVersion, String requestIdHeader, String log4jRequestIdMdcKey) {
		this.clientVersion = clientVersion;
		this.requestIdHeader = requestIdHeader;
		this.log4jRequestIdMdcKey = log4jRequestIdMdcKey;
	}
	
	public Response<String> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, WebClient webClient) throws RestDAOException {

		Response<String> result = Process(resourceType, transformer, parent, resource, null, null, webClient);

		return result;
	}
	
	public Response<String> Process(String resourceType, Transformer transformer, BaseResource resource, WebClient webClient) throws RestDAOException {

		Response<String> result = Process(resourceType, transformer, resource, resource, null, null, webClient);

		return result;
	}
	
	public Response<String> Process(String resourceType, Transformer transformer, BaseResource resource, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<String> result = Process(resourceType, transformer, resource, resource, null, queryParams, webClient);

		return result;
	}
	
	public Response<String> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, Map<String,String> queryParams, WebClient webClient) throws RestDAOException {

		Response<String> result = Process(resourceType, transformer, parent, resource, null, queryParams, webClient);

		return result;
	}
	
	public abstract Response<String> Process(String resourceType, Transformer transformer, BaseResource parent, TypedResource resource, Map<String,String> headerParams, Map<String,String> queryParams, WebClient webClient) throws RestDAOException;

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
	
	public String getClientVersion() {
		return clientVersion;
	}

	public String getRequestIdHeader() {
		return requestIdHeader;
	}

	public String getLog4jRequestIdMdcKey() {
		return log4jRequestIdMdcKey;
	}
	
}
