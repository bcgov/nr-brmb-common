package ca.bc.gov.brmb.common.rest.client;

public class GoneException extends ClientErrorException {

	private static final long serialVersionUID = 1L;

	public GoneException(String message) {
		super(410, message);
	}

}
