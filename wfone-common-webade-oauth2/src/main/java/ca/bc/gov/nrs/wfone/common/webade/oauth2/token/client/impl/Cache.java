package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.impl;

public interface Cache<O> {
	
	public O get(String key);
	
	public void put(String key, O value) ;

	public void remove(String key);
}
