package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalTimeJacksonSerializer extends JsonSerializer<LocalTime>
{

	private static final Logger logger = LoggerFactory.getLogger(LocalTimeJacksonSerializer.class);

	@Override
	public void serialize(LocalTime localDate, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		logger.trace("<serialize");

		try {

			String json = localDate.format(DateTimeFormatter.ISO_LOCAL_TIME);

			generator.writeRawValue("\""+json+"\"");

		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalTime: " + localDate, e);
		}

		logger.trace(">serialize");
	}

}
