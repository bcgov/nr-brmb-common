package ca.bc.gov.brmb.common.rest.resource.transformers;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class XmlTransformer implements Transformer {

	private JAXBContext jc;
	private Unmarshaller u;
	private Marshaller m;
	
	public XmlTransformer(Class<?>[] clazzes) throws TransformerException {
		try {
			
			// Include all of the classes that will need to be un/marshalled
			this.jc = JAXBContext.newInstance(
					clazzes
					);
			this.u = jc.createUnmarshaller();
			this.m = jc.createMarshaller();
			this.m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}catch(Exception e) {
			e.printStackTrace();
			throw new TransformerException(e);
		}
	}
	
	@Override
	public Object unmarshall(byte[] input, Class<?> clazz) throws TransformerException {
		Object result = null;
		try {
			result = u.unmarshal(new StreamSource(new ByteArrayInputStream(input)));
		
		} catch (JAXBException e) {
			throw new TransformerException(e);
		}
		
		return result;
	}
	
	@Override
	public Object unmarshall(String input, Class<?> clazz) throws TransformerException {
		Object result = null;
		try {
			result = u.unmarshal(new StreamSource(new StringReader(input)));
		
		} catch (JAXBException e) {
			throw new TransformerException(e);
		}
		
		return result;
	}

	@Override
	public Object unmarshall(InputStream input, Class<?> clazz)
			throws TransformerException {
		Object result = null;
		try {
			result = u.unmarshal(new StreamSource(input));
		
		} catch (JAXBException e) {
			throw new TransformerException(e);
		}
		
		return result;
	}

	@Override
	public String marshall(Object input) throws TransformerException {
	
		String result = null;
		try {
		
			StringWriter sw = new StringWriter();
			this.m.marshal(input, sw);
			result = sw.toString();
		
		} catch (JAXBException e) {
			throw new TransformerException(e);
		}
		
		return result;
	}

	@Override
	public String getFormat() {
		return "xml";
	}

	@Override
	public String getContentType() {
		return "application/xml";
	}

}
