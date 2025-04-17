package ca.bc.gov.brmb.common.rest.client;

public class ConflictException extends ClientErrorException {

	private static final long serialVersionUID = 1L;

	public ConflictException(String message) {
		super(409, message);
	}

}
