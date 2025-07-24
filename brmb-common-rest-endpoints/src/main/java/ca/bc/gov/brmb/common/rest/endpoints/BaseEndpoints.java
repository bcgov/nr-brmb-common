package ca.bc.gov.brmb.common.rest.endpoints;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;

public interface BaseEndpoints {
	
	@Context 
	public void setUriInfo(UriInfo uriInfo);

	@Context
	public void setHttpServletRequest(HttpServletRequest httpServletRequest);

	@Context
	public void setRequest(Request request);
}
