package ca.bc.gov.brmb.common.rest.resource.transformers;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZonedDateTimeJAXBAdapter extends XmlAdapter<String, ZonedDateTime>
{
	private static final Logger logger = LoggerFactory.getLogger(ZonedDateTimeJAXBAdapter.class);

	@Override
	public ZonedDateTime unmarshal(String text) throws Exception {
		logger.trace("<unmarshal");
		ZonedDateTime result = null;

		try {
			
			result =  ZonedDateTime.ofInstant(Instant.parse(text), ZoneId.systemDefault());
		} catch (DateTimeParseException e1) {
				
			try {
			
				long epochMillis = Long.parseLong(text);
				
				result =  ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
			} catch(NumberFormatException e3) {
				
				throw new RuntimeException("Unable to parse string as ZonedDateTime: '"+text+"'");
			}
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(ZonedDateTime zonedDateTime) throws Exception {
		logger.trace("<marshal");
		String result = null;

		try {

			result = zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
			
		} catch (RuntimeException e) {
			logger.error("Failed to serialize ZonedDateTime: " + zonedDateTime, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
