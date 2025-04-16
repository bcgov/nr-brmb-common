package ca.bc.gov.brmb.common.webade.oauth2.token.client.resource.transformers;

import java.io.InputStream;


public interface Transformer{

	public Object unmarshall(String input, Class<?> clazz) throws TransformerException;

	public Object unmarshall(InputStream input, Class<?> clazz) throws TransformerException;
	
	public String marshall(Object input) throws TransformerException;
	
	public String getFormat();
	
	public String getContentType();
	
}
