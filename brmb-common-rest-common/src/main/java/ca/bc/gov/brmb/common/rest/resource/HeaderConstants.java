package ca.bc.gov.brmb.common.rest.resource;

public class HeaderConstants {

	public static final String VERSION_HEADER = "Rest-Version";
	public static final String VERSION_HEADER_DESCRIPTION = "The version of the Rest API supported by the requesting client.";

	public static final String IF_MATCH_HEADER = "If-Match";
	public static final String IF_MATCH_DESCRIPTION = "The If-Match request-header must match the current eTag of the resource or the request will fail.";

	public static final String ETAG_HEADER = "ETag";
	public static final String ETAG_DESCRIPTION = "The ETag response-header field provides the current value of the entity tag for the requested variant.";

	public static final String REQUEST_ID_HEADER = "RequestId";
	public static final String REQUEST_ID_HEADER_DESCRIPTION = "The unique identity of the request assigned by the originator to help trace the request through the logs.";
	
	public static final String ORIGIN_HEADER = "Origin";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String ACCEPT_HEADER = "Accept";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTHORIZATION_HEADER_DESCRIPTION = "Contains the credentials to authenticate a user agent with a server.";

	public static final String CACHE_CONTROL_HEADER = "Cache-Control";
	public static final String CACHE_CONTROL_DESCRIPTION = "Used to specify directives for caches along the request-response chain";

	public static final String LOCATION_HEADER = "Location";
	public static final String LOCATION_DESCRIPTION = "The Location response-header field is used to redirect the recipient to a location other than the Request-URI for completion of the request or identification of a new resource.";
	
	public static final String PRAGMA_HEADER = "Pragma";
	public static final String PRAGMA_HEADER_DESCRIPTION = "An implementation-specific header that may have various effects along the request-response chain. It is used for backwards compatibility with HTTP/1.0 caches where the Cache-Control HTTP/1.1 header is not yet present.";
	
	
}
