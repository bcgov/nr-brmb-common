package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.geojson.GeoJsonReader;

public class GeoJsonJacksonDeserializer extends JsonDeserializer<Geometry>
{

	private static final Logger logger = LoggerFactory.getLogger(GeoJsonJacksonDeserializer.class);

	@Override
	public Geometry deserialize(JsonParser jsonParser, DeserializationContext context)
		throws IOException, JsonProcessingException
	{
		logger.trace("<deserialize");
		Geometry result = null;

		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		String geoJson = node.toString();

		GeoJsonReader geoJsonReader = new GeoJsonReader();

		try
		{

			Reader reader = new StringReader(geoJson);
			result = geoJsonReader.read(reader);

		}
		catch (ParseException e)
		{
			logger.error("Failed to deserialize geojson: " + geoJson, e);
			throw new IOException("Failed to deserialize geojson: " + geoJson, e);
		}
		catch (RuntimeException e)
		{
			logger.error("Failed to deserialize geojson: " + geoJson, e);
		}

		logger.trace(">deserialize " + result);
		return result;
	}

}
