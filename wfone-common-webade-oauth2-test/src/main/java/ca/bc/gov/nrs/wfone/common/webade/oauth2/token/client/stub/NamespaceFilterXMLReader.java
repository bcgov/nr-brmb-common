package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class NamespaceFilterXMLReader implements XMLReader {

	private XMLReader xmlReader;

	public NamespaceFilterXMLReader(boolean validate)
			throws ParserConfigurationException, SAXException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(validate);
		xmlReader = parserFactory.newSAXParser().getXMLReader();
	}

	@Override
	public ContentHandler getContentHandler() {
		return xmlReader.getContentHandler();
	}

	@Override
	public DTDHandler getDTDHandler() {
		return xmlReader.getDTDHandler();
	}

	@Override
	public EntityResolver getEntityResolver() {
		return xmlReader.getEntityResolver();
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return xmlReader.getErrorHandler();
	}

	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return xmlReader.getFeature(name);
	}

	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return xmlReader.getProperty(name);
	}

	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		xmlReader.parse(input);
	}

	@Override
	public void parse(String systemId) throws IOException, SAXException {
		xmlReader.parse(systemId);
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		xmlReader.setContentHandler(new NamespaceFilterHandler(handler));
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
		xmlReader.setDTDHandler(handler);
	}

	@Override
	public void setEntityResolver(EntityResolver handler) {
		xmlReader.setEntityResolver(handler);
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		xmlReader.setErrorHandler(handler);
	}

	@Override
	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		xmlReader.setFeature(name, value);
	}

	@Override
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		xmlReader.setProperty(name, value);
	}

}