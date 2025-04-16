package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InstantJacksonSerializer extends JsonSerializer<Instant>
{

	private static final Logger logger = LoggerFactory.getLogger(InstantJacksonSerializer.class);

	@Override
	public void serialize(Instant instant, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		logger.trace("<serialize");

		try {

			String json = DateTimeFormatter.ISO_INSTANT.format(instant);

			generator.writeRawValue("\""+json+"\"");

		} catch (RuntimeException e) {
			logger.error("Failed to serialize Instant: " + instant, e);
		}

		logger.trace(">serialize");
	}

}
