package ca.bc.gov.brmb.common.utils;

import jakarta.servlet.http.HttpServletRequest;

public class HttpServletRequestHolder {
	
	private static ThreadLocal<HttpServletRequest> httpServletRequest = new ThreadLocal<HttpServletRequest>();

	public static HttpServletRequest getHttpServletRequest() {
		return httpServletRequest.get();
	}

	public static void setHttpServletRequest(HttpServletRequest value) {
		httpServletRequest.set(value);
	}

}
