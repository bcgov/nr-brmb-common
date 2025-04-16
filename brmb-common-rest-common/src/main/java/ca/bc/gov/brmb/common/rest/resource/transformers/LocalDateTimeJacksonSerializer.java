package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeJacksonSerializer extends JsonSerializer<LocalDateTime>
{

	private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeJacksonSerializer.class);

	@Override
	public void serialize(LocalDateTime localDate, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		logger.trace("<serialize");

		try {

			String json = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

			generator.writeRawValue("\""+json+"\"");

		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalDateTime: " + localDate, e);
		}

		logger.trace(">serialize");
	}

}
