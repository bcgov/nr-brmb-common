package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateJacksonSerializer extends JsonSerializer<LocalDate>
{

	private static final Logger logger = LoggerFactory.getLogger(LocalDateJacksonSerializer.class);

	@Override
	public void serialize(LocalDate localDate, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
		logger.trace("<serialize");

		try {

			String json = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

			generator.writeRawValue("\""+json+"\"");

		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalDate: " + localDate, e);
		}

		logger.trace(">serialize");
	}

}
