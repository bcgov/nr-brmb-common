package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/** 
 * Adapter class that maps java.sql.Timestamp values to/from strings, for the purpose
 * of XML streaming of a class containing Timestamp fields.
 */
public class TimestampJAXBAdapter extends XmlAdapter<String, Timestamp>
{
	@Override
	public Timestamp unmarshal(String tsText)
		throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
		Date date = dateFormat.parse(tsText);
		return new Timestamp(date.getTime());
	}

	@Override
	public String marshal(Timestamp ts)
		throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
		return dateFormat.format(new Date(ts.getTime()));
	}
}
