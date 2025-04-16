package ca.bc.gov.nrs.wfone.common.rest.client;

import ca.bc.gov.nrs.wfone.common.rest.client.factory.MessageListFactory;
import ca.bc.gov.nrs.wfone.common.rest.client.factory.RedirectFactory;
import ca.bc.gov.nrs.wfone.common.rest.client.factory.ResourceFactory;

public class RestDAOFactory {

	public static final String DEFAULT_CLIENT_VERSION_HEADER = null;

	public static final String DEFAULT_REQUEST_ID_HEADER = "requestId";

	public static final String DEFAULT_REQUEST_IDLOG4J_MDC_KEY = "requestId";

	protected String requestIdHeader;
	protected String log4jRequestIdMdcKey;

	private String clientVersion;
	
	protected MessageListFactory messageListFactory;
	protected RedirectFactory redirectfactory;
	
	public RestDAOFactory(String clientVersion) {
		this.clientVersion = clientVersion;
		this.requestIdHeader = DEFAULT_REQUEST_ID_HEADER;
		this.log4jRequestIdMdcKey = DEFAULT_REQUEST_IDLOG4J_MDC_KEY;
	}
	
	public <T> GenericRestDAO<T> getGenericRestDAO(Class<T> clazz) {
		GenericRestDAO<T> result = null;
		
		result = new SpringGenericRestDAO<T>(clazz, clientVersion, requestIdHeader, log4jRequestIdMdcKey);
		
		if(messageListFactory!=null) {
			
			result.setMessageListFactory(messageListFactory);
		}
		
		if(redirectfactory!=null) {
			
			result.setRedirectfactory(redirectfactory);
		}
		
		return result;
	}
	
	public <T> GenericRestDAO<T> getGenericRestDAO(Class<T> clazz, ResourceFactory<T> resourceFactory) {
		GenericRestDAO<T> result = null;
		
		result = new SpringGenericRestDAO<T>(clazz, clientVersion, requestIdHeader, log4jRequestIdMdcKey);
		
		if(resourceFactory!=null) {
			
			result.setResourceFactory(resourceFactory);
		}
		
		if(messageListFactory!=null) {
			
			result.setMessageListFactory(messageListFactory);
		}
		
		if(redirectfactory!=null) {
			
			result.setRedirectfactory(redirectfactory);
		}
		
		return result;
	}

	public StringRestDAO getStringRestDAO() {
		StringRestDAO result = null;
		
		result = new SpringStringRestDAO(clientVersion, requestIdHeader, log4jRequestIdMdcKey);
		
		return result;
	}

	public ByteArrayRestDAO getByteArrayRestDAO() {
		ByteArrayRestDAO result = null;
		
		result = new SpringByteArrayRestDAO(clientVersion, requestIdHeader, log4jRequestIdMdcKey);
		
		return result;
	}

	public void setRequestIdHeader(String requestIdHeader) {
		this.requestIdHeader = requestIdHeader;
	}

	public void setLog4jRequestIdMdcKey(String log4jRequestIdMdcKey) {
		this.log4jRequestIdMdcKey = log4jRequestIdMdcKey;
	}

	public void setMessageListFactory(MessageListFactory messageListFactory) {
		this.messageListFactory = messageListFactory;
	}

	public void setRedirectfactory(RedirectFactory redirectfactory) {
		this.redirectfactory = redirectfactory;
	}
}
