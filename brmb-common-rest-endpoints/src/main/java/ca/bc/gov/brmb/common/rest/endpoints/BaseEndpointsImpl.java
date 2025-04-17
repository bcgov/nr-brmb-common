package ca.bc.gov.brmb.common.rest.endpoints;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.bc.gov.brmb.common.rest.resource.HeaderConstants;
import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.rest.resource.Redirect;
import ca.bc.gov.brmb.common.rest.resource.RelLink;
import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.rest.endpoints.resource.factory.ResourceFactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.utils.ByteUtils;
import ca.bc.gov.brmb.common.utils.HttpServletRequestHolder;
import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public abstract class BaseEndpointsImpl implements BaseEndpoints {

	private static final Logger logger = LoggerFactory
			.getLogger(BaseEndpointsImpl.class);
	
	public static String RESOURCE_DEPTH_QUERY_PARAMETER = "expand";

	@Autowired
	private Properties applicationProperties;
	
	private UriInfo uriInfo;
	
	private HttpServletRequest httpServletRequest;
	
	private Request request;

	protected final static boolean hasAuthority(String... authorityName) {
		boolean result = false;
		
		List<String> authorityNames = Arrays.asList(authorityName);
		
		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			for (GrantedAuthority grantedAuthority : authentication
					.getAuthorities()) {
				
				String authority = grantedAuthority.getAuthority();
				
				if (authorityNames.contains(authority)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}
	
	protected void logRequest() {
		if(logger.isInfoEnabled()) {
			String method = this.httpServletRequest.getMethod();
			URI uri = this.uriInfo.getRequestUri();
			String path = uri.getPath();
			String query = uri.getQuery();
			logger.info(method+" "+path+(query==null?"":"?"+query));
			WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
			if(authentication!=null) {
				Long organizationId = authentication.getOnBehalfOfOrganizationId();
				logger.info("Authorized User:"+authentication.getUserTypeCode()+"/"+authentication.getUserGuid()+(organizationId==null?"":"@"+organizationId));
			}
		}
	}
	
	protected void logResponse(Response response) {
		if(logger.isInfoEnabled()) {
			
			int statusCode = response.getStatus();
			Response.Status responseStatus = Response.Status.fromStatusCode(statusCode);
			String reasonPhrase = responseStatus.getReasonPhrase();
			logger.info(statusCode+" "+reasonPhrase);
			
			MultivaluedMap<String, Object> metaData = response.getMetadata();
			if(!metaData.isEmpty()) {
				logger.info("Response Metadata:");
				for(String key:metaData.keySet()) {
					Object metaDatum = metaData.get(key);
					logger.info(key+" "+metaDatum);
				}
			}
			
			Object entity = response.getEntity();
			if(entity!=null&&entity instanceof MessageListRsrc) {
				MessageListRsrc messages = (MessageListRsrc) entity;
				logger.info("Messages:");
				for(Message message:messages.getMessages()) {
					logger.info("path:"+message.getPath());
					logger.info("message:"+message.getMessage());
					logger.info("messageTemplate:"+message.getMessageTemplate());
					if(message.getMessageArguments()!=null) {

						logger.info("messageArguments:"+Arrays.toString(message.getMessageArguments()));
					}
				}
			}
		}
	}
	
	protected Response getInternalServerErrorResponse(Throwable t) {
		Response result = null;
		logger.error(t.getMessage(), t);
		
		Throwable tmp = t;
		String msg = null;
		while(true) {
			msg = tmp.getMessage();
			if(msg == null) {
				if(tmp.getCause() == null) {
					msg = tmp.getClass().getName();
					break;
				}
				{
					tmp = tmp.getCause();
				}
			} else {
				break;
			}
		}

		MessageListRsrc messageList = new MessageListRsrc(msg);
		
		GenericEntity<MessageListRsrc> entity = new GenericEntity<MessageListRsrc>(messageList) {
			/* do nothing */
		};
		
		result = Response.serverError().entity(entity).build();
		return result;
	}
	
	protected String nvl(String value, String defaultValue) {
		return (value==null)?defaultValue:value;
	}

	protected Redirect getRedirect(URI uri) {
		Redirect result = new Redirect();
		result.getLinks().add(new RelLink(BaseResourceTypes.REDIRECT, uri.toString(), "GET"));
		return result;
	}
	
	protected final static String getCurrentApplicationCode() {
		String result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getClientAppCode();
		}

		return result;
	}

	protected final static String getCurrentUserTypeCode() {
		String result = null;
		
		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getUserTypeCode();
		}

		return result;
	}

	protected final static Long getOnBehalfOfOrganizationId() {
		Long result = null;
		
		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getOnBehalfOfOrganizationId();
		}

		return result;
	}

	protected final static String getOnBehalfOfOrganizationCode() {
		String result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getOnBehalfOfOrganizationCode();
		}

		return result;
	}

	protected final static String getOnBehalfOfOrganizationName() {
		String result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getOnBehalfOfOrganizationName();
		}

		return result;
	}

	protected final static Map<String, String> getOnBehalfOfOrganizationAdditionalInfo() {
		Map<String, String> result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getOnBehalfOfOrganizationAdditionalInfo();
		}

		return result;
	}

	protected final static String getCurrentUserId() {
		String result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getUserId();
		}

		return result;
	}

	protected final static WebAdeAuthentication getWebAdeAuthentication() {
		return (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
	}

	protected final static String getCurrentUserGuid() {
		String result = null;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getUserGuid();
		}

		return result;
	}

	protected final static boolean isCurrentUser(String userTypeCode, String userGuid) {
		boolean result = false;

		WebAdeAuthentication authentication = (WebAdeAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			result = authentication.getUserTypeCode().equals(userTypeCode)&& authentication.getUserGuid().equals(userGuid);
		}

		return result;
	}
	
	protected FactoryContext getFactoryContext() {
		
		int resourceDepth = Integer.MAX_VALUE;
		
		String resrouceDepthQueryParameter = this.httpServletRequest.getParameter(RESOURCE_DEPTH_QUERY_PARAMETER);
		
		if(resrouceDepthQueryParameter!=null&&resrouceDepthQueryParameter.trim().length()>0) {
			
			try {
			
				resourceDepth = Integer.parseInt(resrouceDepthQueryParameter);
			} catch(NumberFormatException e) {
				// do nothing
			}
		}
		
		return new ResourceFactoryContext(getBaseUri(), resourceDepth);
	}

	protected URI getBaseUri() {
		logger.debug("<getBaseUri");

		URI result = null;
		
		try {
			
			URI baseUri = uriInfo.getBaseUri();
			
			boolean unversionedInd = Boolean.parseBoolean(this.applicationProperties.getProperty("base.uri.unversioned", "false"));
			logger.debug("unversionedInd=" + unversionedInd);
			
			String basePath = baseUri.getPath();
			if(unversionedInd) {
				String[] split = basePath.split("/");
				String tmp = "";
				for(int i=0;i<split.length-1;++i) {
					tmp += split[i]+"/";
				}
				basePath = tmp;
			}

			String hostOverride = this.applicationProperties.getProperty("base.uri.override");
			
			String forwardedProtoHeader = this.httpServletRequest.getHeader("X-Forwarded-Proto");

			String forwardedHostHeader = this.httpServletRequest.getHeader("X-Forwarded-Host");

			// check for preference
			if (hostOverride != null && hostOverride.trim().length() > 0) {

				URI uri = new URI(hostOverride + basePath);

				if (uri.getScheme() == null) {
					uri = new URI("http://" + hostOverride + basePath);
				}

				result = uri;

				// check for apache reverse proxy header (X-Forwarded-Host)
			} else if (forwardedHostHeader != null
					&& forwardedHostHeader.trim().length() > 0) {
				
				String forwardedHost;
				{
					String[] split = forwardedHostHeader.split(",");
					forwardedHost = split[0].trim();
				}

				URI uri = new URI(forwardedHost + basePath);

				if (uri.getScheme() == null) {
					
					if(forwardedProtoHeader != null && forwardedProtoHeader.trim().length() > 0) {
						
						String forwardedProto;
						{
							String[] split = forwardedProtoHeader.split(",");
							forwardedProto = split[0].trim();
						}
						
						uri = new URI(forwardedProto+"://" + forwardedHost + basePath);
					} else {
						uri = new URI("http://" + forwardedHost + basePath);
					}
				}

				result = uri;

			} else {

				// use default request information
				result = new URI(baseUri.getScheme(),
						baseUri.getUserInfo(), baseUri.getHost(), baseUri.getPort(),
						basePath, baseUri.getQuery(), baseUri.getFragment());
			}

			logger.debug("baseUri=" + baseUri);

		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		logger.debug(">getBaseUri " + result);
		return result;
	}

	protected String getIfMatchHeader() {
		logger.debug("<getIfMatchHeader");
		String result = this.httpServletRequest.getHeader("If-Match");
		logger.debug("result="+result);
		if(result!=null) {
			// strip Weak tag marker
			if (result.startsWith("W/")) {
				result = result.substring(2);
			}
			// strip quotes
			if(result.startsWith("\"")&&result.endsWith("\"")) {
				StringBuilder sb = new StringBuilder();
				for(int i=1;result.length()-1>i;++i) {
					sb.append(result.charAt(i));
				}
				result = sb.toString();
			}
		}
		logger.info(">getIfMatchHeader '"+result+"'");
		return result;
	}

	protected String getEtag(Object object) {

		String result = null;

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] mdBytes = md.digest(getEntityToBytes(object));

			UUID uuid = ByteUtils.getUUID(mdBytes);

			result = uuid.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create MD5 hash.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to serialize object.", e);
		}

		return result;
	}

	private static byte[] getEntityToBytes(Object object) throws IOException {

		byte[] result = null;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			
			try (ObjectOutput out = new ObjectOutputStream(bos)) {

				out.writeObject(object);
				result = bos.toByteArray();
			}
		}

		return result;
	}

	protected ResponseBuilder evaluatePreconditions(EntityTag eTag) {
		logger.info("> evaluatePreconditions: Resource ETag " + eTag.getValue());
		// Check for weak eTag, then ignore
		String requestIfMatch = httpServletRequest.getHeader(HeaderConstants.IF_MATCH_HEADER);
		EntityTag requestETag = EntityTag.valueOf(requestIfMatch);
		logger.info("> evaluatePreconditions: Request ETag " + requestETag.getValue());
		logger.info("> evaluatePreconditions: request ETag weak? " + requestETag.isWeak());
		// Check request.getValue against etag.getValue. DO NOT remove W\, as its removed by creating the
		// EntityTag object
		if (requestETag.isWeak() && requestETag.getValue().equals(eTag.getValue())) {
			// If this etag is weak, strip the W/ and check as if it's strong
			// If the request eTag and the resource eTag match, we can
			// assume preconditions on the check are met
			return null;
		} else {
			// otherwise, return a proper check
			return this.request.evaluatePreconditions(eTag);
		}
	}

	protected URI getRequestUri() {
		return this.uriInfo.getRequestUri();
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	@Override
	@Context 
	public void setUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

	@Override
	@Context 
	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
		
		HttpServletRequestHolder.setHttpServletRequest(httpServletRequest);
	}

	@Override
	@Context 
	public void setRequest(Request request) {
		this.request = request;
	}

	protected static Double toDouble(String value) {
		Double result = null;
		if(value!=null&&value.trim().length()>0) {
			result = Double.valueOf(value);
		}
		return result;
	}

	protected static Integer toInteger(String value) {
		Integer result = null;
		if(value!=null&&value.trim().length()>0) {
			result = Integer.valueOf(value);
		}
		return result;
	}
	
	protected static Long toLong(String value) {
		Long result = null;
		if(value!=null && value.trim().length()>0) {
			result = Long.valueOf(value);
		}
		return result;
	}

	protected static String[] toStringArray(List<String> values) throws UnsupportedEncodingException {
		String[] result = null;
		
		if(values!=null) {
			List<String> tmp = new ArrayList<>();
			
			for(String value:values) {
				
				String[] valuesArray = toStringArray(value);
				if(valuesArray!=null) {
					tmp.addAll(Arrays.asList(valuesArray));
				}
			}
			
			result = tmp.toArray(new String[] {});
		}
		return result;
	}
	
	protected static String[] toStringArray(String values) throws UnsupportedEncodingException {
		String[] result = null;
		if(values!=null && values.trim().length() > 0) {
			result = toString(values).split(",");
		}
		return result;
	}

	private static String DATE_MASK = "yyyy-MM-dd";
	private static String TIMESTAMP_MASK = "yyyy-MM-dd'T'HH:mm:ss";
	
	protected static Date toDate(String value) {
		Date d = null;
		if (value != null && value.trim().length() > 0) {
		
			try {
				d = new Date(Long.parseLong(value));
				
			} catch(NumberFormatException e) {

				SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_MASK);
				
				try {
					d = timestampFormat.parse(value);
				} catch (ParseException e1) {
					
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_MASK);
					
					try {
						d = dateFormat.parse(value);
					} catch (ParseException e2) {
						throw new RuntimeException("Either the parameter is not being validated or the validator mask does not match either '"+TIMESTAMP_MASK+"': "+value, e2);
					}
				}
				
			}
		}
		
		return d;
	}

	protected static Boolean toBoolean(String value) {
		Boolean result = null;
		if(value!=null&&value.trim().length()>0) {
			if(value.equalsIgnoreCase("Y")||value.equalsIgnoreCase("T")||value.equalsIgnoreCase("True")||value.equalsIgnoreCase("1")) {
				result = Boolean.TRUE;
			} else {
				result = Boolean.FALSE;
			}
		}
		return result;
	}

	protected static String toString(String value) throws UnsupportedEncodingException {
		String result = null;
		if(value!=null&&value.trim().length()>0) {

			try {
			result = URLDecoder.decode(value, "UTF-8");
			} catch(IllegalArgumentException e) {
				logger.warn("badly encoded URL parameter '"+value+"'", e);
				result = value;
			}
		}
		return result;
	}
}
