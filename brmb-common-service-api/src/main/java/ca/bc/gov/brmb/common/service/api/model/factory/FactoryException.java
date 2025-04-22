package ca.bc.gov.brmb.common.service.api.model.factory;

public class FactoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FactoryException(String message) {
		super(message);
	}

	public FactoryException(String message, Throwable t) {
		super(message, t);
	}

}
