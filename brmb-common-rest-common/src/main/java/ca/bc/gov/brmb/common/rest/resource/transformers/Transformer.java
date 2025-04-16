package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.InputStream;

public interface Transformer{

	public Object unmarshall(byte[] input, Class<?> clazz) throws TransformerException;

	public Object unmarshall(String input, Class<?> clazz) throws TransformerException;

	public Object unmarshall(InputStream input, Class<?> clazz) throws TransformerException;
	
	public String marshall(Object input) throws TransformerException;
	
	public String getFormat();
	
	public String getContentType();
	
}
