package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.geojson.GeoJsonReader;
import com.vividsolutions.jts.io.geojson.GeoJsonWriter;

/**
 * Adapter class that maps com.vividsolutions.jts.geom.Geometry values to/from
 * strings, for the purpose of XML streaming of a class containing Geometry
 * fields.
 */
public class GeoJsonJAXBAdapter extends XmlAdapter<String, Geometry>
{
	private static final Logger logger = LoggerFactory.getLogger(GeoJsonJAXBAdapter.class);

	@Override
	public Geometry unmarshal(String geoText)
		throws Exception
	{
		logger.trace("<unmarshal");
		Geometry result = null;

		GeoJsonReader geoJsonReader = new GeoJsonReader();

		try
		{
			Reader reader = new StringReader(geoText);
			result = geoJsonReader.read(reader);
		}
		catch (RuntimeException e)
		{
			logger.error("Failed to unmarshal geojson: " + geoText, e);
		}

		logger.trace(">unmarshal " + result);
		return result;
	}

	@Override
	public String marshal(Geometry geometry)
		throws Exception
	{
		logger.trace("<marshal");
		String result = null;

		try
		{
			GeoJsonWriter geoJsonWriter = new GeoJsonWriter();

			StringWriter writer = new StringWriter();

			geoJsonWriter.write(geometry, writer);

			result = writer.toString();
		}
		catch (RuntimeException e)
		{
			logger.error("Failed to marshal Geometry: " + geometry, e);
		}

		logger.trace(">marshal " + result);
		return result;
	}
}
