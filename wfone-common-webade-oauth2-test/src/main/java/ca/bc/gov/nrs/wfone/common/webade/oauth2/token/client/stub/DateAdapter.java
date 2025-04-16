package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";


	SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    @Override
    public String marshal(Date source) throws Exception {
    	String result = null;
		if(source!=null) {
				result = dateTimeFormat.format(source);
		}
		return result;
    }

    @Override
    public Date unmarshal(String source) throws Exception {
    	Date result = null;
		if(source!=null) {
			if(source.length()>10) {
				result = dateTimeFormat.parse(source);
			} else {
				result = dateFormat.parse(source);
			}
		}
		return result;
    }

}