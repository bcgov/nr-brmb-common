package ca.bc.gov.nrs.wfone.common.model.transformers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDateJAXBAdapter extends XmlAdapter<String, LocalDate>
{
	private static final Logger logger = LoggerFactory.getLogger(LocalDateJAXBAdapter.class);

	@Override
	public LocalDate unmarshal(String text) throws Exception {
		logger.trace("<unmarshal");
		LocalDate result = null;

		try {
			
			result =  LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e1) {
				
			throw new RuntimeException("Unable to parse string as LocalDate: '"+text+"'");
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(LocalDate localDate) throws Exception {
		logger.trace("<marshal");
		String result = null;

		try {

			result = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
			
		} catch (RuntimeException e) {
			logger.error("Failed to serialize LocalDate: " + localDate, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
