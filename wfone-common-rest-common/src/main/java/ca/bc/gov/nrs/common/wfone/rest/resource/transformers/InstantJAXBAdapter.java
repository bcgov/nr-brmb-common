package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstantJAXBAdapter extends XmlAdapter<String, Instant>
{
	private static final Logger logger = LoggerFactory.getLogger(InstantJAXBAdapter.class);

	@Override
	public Instant unmarshal(String text) throws Exception {
		logger.trace("<unmarshal");
		Instant result = null;

		try {
			
			result =  Instant.parse(text);
		} catch (DateTimeParseException e1) {
				
			try {
			
				long epochMillis = Long.parseLong(text);
				
				result =  Instant.ofEpochMilli(epochMillis);
			} catch(NumberFormatException e3) {
				
				throw new RuntimeException("Unable to parse string as Instant: '"+text+"'");
			}
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(Instant instant) throws Exception {
		logger.trace("<marshal");
		String result = null;

		try {

			result = DateTimeFormatter.ISO_INSTANT.format(instant);
			
		} catch (RuntimeException e) {
			logger.error("Failed to serialize Instant: " + instant, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
