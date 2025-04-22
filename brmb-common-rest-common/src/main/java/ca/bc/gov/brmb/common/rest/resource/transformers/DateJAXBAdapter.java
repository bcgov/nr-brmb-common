package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateJAXBAdapter extends XmlAdapter<String, Date> {
	private static final Logger logger = LoggerFactory
			.getLogger(DateJAXBAdapter.class);

	@Override
	public Date unmarshal(String dateText) throws Exception {
		logger.trace("<unmarshal");
		Date result = null;

		if (dateText != null && dateText.trim().length() > 0) {

			try {
				long epoch = Long.parseLong(dateText);

				result = new Date(epoch);
			} catch (NumberFormatException e) {
				logger.error("Failed to unmarshal dateText: " + dateText, e);
			}
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(Date date) throws Exception {
		logger.trace("<marshal");
		String result = null;

		if (date != null) {
			try {
				result = Long.toString(date.getTime());
			} catch (RuntimeException e) {
				logger.error("Failed to marshal Date: " + date, e);
			}
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
