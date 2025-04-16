package ca.bc.gov.brmb.common.rest.resource.transformers;

public class TransformerException extends Exception {

	private static final long serialVersionUID = 1L;

	public TransformerException() {}

	public TransformerException(String message) {
		super(message);
	}

	public TransformerException(Throwable cause) {
		super(cause);
	}

	public TransformerException(String message, Throwable cause) {
		super(message, cause);
	}

}
