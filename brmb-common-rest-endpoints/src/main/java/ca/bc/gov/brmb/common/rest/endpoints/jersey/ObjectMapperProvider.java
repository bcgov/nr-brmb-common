package ca.bc.gov.brmb.common.rest.endpoints.jersey;

import jakarta.ws.rs.ext.ContextResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ObjectMapperProvider.class);

	private static ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public ObjectMapper getContext(Class<?> type) {
		logger.debug("<getContext "+type);
		ObjectMapper result = mapper;
		logger.debug(">getContext "+result);
		return result;
	}

}
