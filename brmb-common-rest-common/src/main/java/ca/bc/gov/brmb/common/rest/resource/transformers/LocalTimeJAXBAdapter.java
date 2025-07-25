package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTimeJAXBAdapter extends XmlAdapter<String, LocalTime>
{
	private static final Logger logger = LoggerFactory.getLogger(LocalTimeJAXBAdapter.class);

	@Override
	public LocalTime unmarshal(String text) throws Exception {
		logger.trace("<unmarshal");
		LocalTime result = null;

		try {
			
			result =  LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
		} catch (DateTimeParseException e1) {
				
			throw new RuntimeException("Unable to parse string as LocalTime: '"+text+"'");
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(LocalTime localDate) throws Exception {
		logger.trace("<marshal");
		String result = null;

		try {

			result = localDate.format(DateTimeFormatter.ISO_LOCAL_TIME);
			
		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalTime: " + localDate, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
