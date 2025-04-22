package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalDate;
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

public class LocalDateJacksonDeserializer extends JsonDeserializer<LocalDate>
{

	private static final Logger logger = LoggerFactory.getLogger(LocalDateJacksonDeserializer.class);

	@Override
	public LocalDate deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException
	{
		logger.trace("<deserialize");
		LocalDate result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String dateString = node.asText();

		try {
			
			result =  LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e1) {
				
			throw new JsonParseException(jsonParser, "Unable to parse string as LocalDate: '"+dateString+"'", jsonParser.getCurrentLocation());
		}
		
		logger.trace(">deserialize " + result);
		return result;
	}

}
