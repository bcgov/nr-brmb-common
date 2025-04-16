package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.IOException;
import java.time.Instant;
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

public class InstantJacksonDeserializer extends JsonDeserializer<Instant> {

	private static final Logger logger = LoggerFactory.getLogger(InstantJacksonDeserializer.class);

	@Override
	public Instant deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException {
		logger.trace("<deserialize");
		Instant result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String dateString = node.asText();

		try {
			
			result =  Instant.parse(dateString);
		} catch (DateTimeParseException e1) {
				
			try {
			
				long epochMillis = Long.parseLong(dateString);
				
				result =  Instant.ofEpochMilli(epochMillis);
			} catch(NumberFormatException e3) {
				
				throw new JsonParseException(jsonParser, "Unable to parse string as Instant: '"+dateString+"'", jsonParser.getCurrentLocation());
			}
		}
		
		logger.trace(">deserialize " + result);
		return result;
	}

}
