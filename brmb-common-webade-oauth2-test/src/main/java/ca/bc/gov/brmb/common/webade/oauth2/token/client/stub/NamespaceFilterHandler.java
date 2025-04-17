package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

final class NamespaceFilterHandler implements ContentHandler {

	private final static String NAMESPACE = "http://www.webade.org/webade-xml-user-info";
	private ContentHandler contentHandler;

	public NamespaceFilterHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		contentHandler.startElement(NAMESPACE, localName, qName, atts);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contentHandler.characters(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		contentHandler.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		contentHandler.endElement(uri, localName, qName);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		contentHandler.endPrefixMapping(prefix);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		contentHandler.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		contentHandler.processingInstruction(target, data);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		contentHandler.setDocumentLocator(locator);
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		contentHandler.skippedEntity(name);
	}

	@Override
	public void startDocument() throws SAXException {
		contentHandler.startDocument();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		contentHandler.startPrefixMapping(prefix, uri);
	}

}