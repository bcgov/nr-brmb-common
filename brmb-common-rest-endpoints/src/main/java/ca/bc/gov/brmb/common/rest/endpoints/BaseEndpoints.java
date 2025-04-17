package ca.bc.gov.brmb.common.rest.endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

public interface BaseEndpoints {
	
	@Context 
	public void setUriInfo(UriInfo uriInfo);

	@Context
	public void setHttpServletRequest(HttpServletRequest httpServletRequest);

	@Context
	public void setRequest(Request request);
}
