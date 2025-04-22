package ca.bc.gov.brmb.common.rest.resource.transformers;

import java.io.InputStream;

public class NullTransformer implements Transformer {

	private String contentType;
	private String format;

	public NullTransformer(String contentType, String format) {
		if (contentType == null || contentType.trim().length() == 0) {
			throw new IllegalArgumentException("contentType is required.");
		}
		this.contentType = contentType;
		this.format = format;
	}

	@Override
	public Object unmarshall(byte[] input, Class<?> clazz)
			throws TransformerException {
		return input;
	}

	@Override
	public Object unmarshall(String input, Class<?> clazz)
			throws TransformerException {
		return input;
	}

	@Override
	public Object unmarshall(InputStream input, Class<?> clazz)
			throws TransformerException {
		return input;
	}

	@Override
	public String marshall(Object input) throws TransformerException {
		return input.toString();
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

}
