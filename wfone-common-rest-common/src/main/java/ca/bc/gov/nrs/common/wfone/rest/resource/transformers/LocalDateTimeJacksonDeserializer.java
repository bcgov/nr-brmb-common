package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class LocalDateTimeJacksonDeserializer extends JsonDeserializer<LocalDateTime> {

	private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeJacksonDeserializer.class);

	@Override
	public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException {
		logger.trace("<deserialize");
		LocalDateTime result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String dateString = node.asText();

		try {
			
			result =  LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} catch (DateTimeParseException e1) {
				
			throw new JsonParseException(jsonParser, "Unable to parse string as LocalDate: '"+dateString+"'", jsonParser.getCurrentLocation());
		}
		
		logger.trace(">deserialize " + result);
		return result;
	}

}
