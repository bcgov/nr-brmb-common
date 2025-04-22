package ca.bc.gov.brmb.common.rest.resource.transformers;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeJacksonSerializer extends JsonSerializer<ZonedDateTime>
{

	private static final Logger logger = LoggerFactory.getLogger(ZonedDateTimeJacksonSerializer.class);

	@Override
	public void serialize(ZonedDateTime zonedDateTime, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		logger.trace("<serialize");

		try {

			String json = zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);

			generator.writeRawValue("\""+json+"\"");

		} catch (RuntimeException e) {
			logger.error("Failed to serialize ZonedDateTime: " + zonedDateTime, e);
		}

		logger.trace(">serialize");
	}

}
