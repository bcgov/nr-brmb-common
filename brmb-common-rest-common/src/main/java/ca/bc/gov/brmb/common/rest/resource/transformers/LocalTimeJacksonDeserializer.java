package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.IOException;
import java.time.LocalTime;
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

public class LocalTimeJacksonDeserializer extends JsonDeserializer<LocalTime>
{

	private static final Logger logger = LoggerFactory.getLogger(LocalTimeJacksonDeserializer.class);

	@Override
	public LocalTime deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException
	{
		logger.trace("<deserialize");
		LocalTime result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String dateString = node.asText();
		
		try {
			
			String text = null;
			if(dateString!=null) {
				
				String[] split = dateString.split(":");
				
				String hours = split[0];
				hours = padLeft(hours, 2, '0');
				
				String minutes = "00";
				if(split.length>1) {
					minutes = split[1];
					minutes = padLeft(minutes, 2, '0');
				}
				
				String seconds = "00";
				if(split.length>2) {
					seconds = split[2];
					seconds = padLeft(seconds, 2, '0');
				}
				
				text = hours+":"+minutes+":"+seconds;
			}
			
			result =  LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
		} catch (DateTimeParseException e1) {
				
			throw new JsonParseException(jsonParser, "Unable to parse string as LocalTime: '"+dateString+"'", jsonParser.getCurrentLocation());
		}
		
		logger.trace(">deserialize " + result);
		return result;
	}
	
	private static String padLeft(String inputString, int length, char character) {
	    if (inputString.length() >= length) {
	        return inputString;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < length - inputString.length()) {
	        sb.append(character);
	    }
	    sb.append(inputString);
	 
	    return sb.toString();
	}

}
