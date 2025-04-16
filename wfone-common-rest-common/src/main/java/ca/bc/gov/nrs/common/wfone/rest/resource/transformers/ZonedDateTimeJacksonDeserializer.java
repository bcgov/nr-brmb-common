package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

public class ZonedDateTimeJacksonDeserializer extends JsonDeserializer<ZonedDateTime> {

	private static final Logger logger = LoggerFactory.getLogger(ZonedDateTimeJacksonDeserializer.class);

	@Override
	public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException {
		logger.trace("<deserialize");
		ZonedDateTime result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String dateString = node.asText();

		try {
			
			result =  ZonedDateTime.ofInstant(Instant.parse(dateString), ZoneId.systemDefault());
		} catch (DateTimeParseException e1) {
				
			try {
			
				long epochMillis = Long.parseLong(dateString);
				
				result =  ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
			} catch(NumberFormatException e3) {
				
				throw new JsonParseException(jsonParser, "Unable to parse string as ZonedDateTime: '"+dateString+"'", jsonParser.getCurrentLocation());
			}
		}
		
		logger.trace(">deserialize " + result);
		return result;
	}

}
