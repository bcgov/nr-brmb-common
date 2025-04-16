package ca.bc.gov.nrs.common.wfone.rest.resource.transformers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDateTimeJAXBAdapter extends XmlAdapter<String, LocalDateTime>
{
	private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeJAXBAdapter.class);

	@Override
	public LocalDateTime unmarshal(String text) throws Exception {
		logger.trace("<unmarshal");
		LocalDateTime result = null;

		try {
			
			result =  LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} catch (DateTimeParseException e1) {
				
			throw new RuntimeException("Unable to parse string as LocalDateTime: '"+text+"'");
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(LocalDateTime localDate) throws Exception {
		logger.trace("<marshal");
		String result = null;

		try {

			result = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			
		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalDateTime: " + localDate, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
