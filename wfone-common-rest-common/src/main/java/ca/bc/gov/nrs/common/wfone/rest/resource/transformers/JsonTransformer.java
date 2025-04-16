package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;


import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonTransformer implements Transformer {

	private static final Logger logger = LoggerFactory
			.getLogger(JsonTransformer.class);

	// Initialize a default mapper
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private ObjectMapper mapper;
	
	public JsonTransformer() {
		
		this.mapper = objectMapper;
	}
	
	public JsonTransformer(ObjectMapper mapper) {
		
		this.mapper = mapper;
	}
	
	@Override
	public Object unmarshall(byte[] input, Class<?> clazz) throws TransformerException {
		Object result = null;
		long start = System.currentTimeMillis();
		try {
			
			result = this.mapper.readValue(input, clazz);
			
			if (logger.isDebugEnabled()) {
				// Pretty Print it
				marshall(result);
			}

		} catch (IOException e) {
			throw new TransformerException(e);
		}
		logger.info("Time taken for unmarshalling response from bytes to {} is {} ms", clazz.getName(), Long.valueOf(System.currentTimeMillis() - start));
		return result;
	}
	
	@Override
	public Object unmarshall(String input, Class<?> clazz) throws TransformerException {
		Object result = null;
		long start = System.currentTimeMillis();
		try {
			
			result = this.mapper.readValue(input, clazz);
			
			if (logger.isDebugEnabled()) {
				// Pretty Print it
				marshall(result);
			}

		} catch (IOException e) {
			throw new TransformerException(e);
		}
		logger.info("Time taken for unmarshalling response from String to {} is {} ms", clazz.getName(), Long.valueOf(System.currentTimeMillis() - start));
		return result;
	}

	@Override
	public Object unmarshall(InputStream input, Class<?> clazz) throws TransformerException {
		Object result = null;
		long start = System.currentTimeMillis();
		try {
			
			result = this.mapper.readValue(input, clazz);
			
			if (logger.isDebugEnabled()) {
				// Pretty Print it
				marshall(result);
			}

		} catch (IOException e) {
			throw new TransformerException(e);
		}
		logger.info("Time taken for unmarshalling response from stream to {} is {} ms", clazz.getName(), Long.valueOf(System.currentTimeMillis() - start));
		return result;
	}

	@Override
	public String marshall(Object input) throws TransformerException {
		ObjectWriter prettyPrinter = this.mapper.writerWithDefaultPrettyPrinter();
		String result = null;
		try {
			
			result = prettyPrinter.writeValueAsString(input);
			logger.debug(result);	
			
		} catch (IOException e) {
			throw new TransformerException(e);
		}

		return result;
	}

	@Override
	public String getFormat() {
		return "json";
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

}
