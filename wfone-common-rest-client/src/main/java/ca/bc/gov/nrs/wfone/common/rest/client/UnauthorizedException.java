package ca.bc.gov.nrs.wfone.common.rest.client;

public class UnauthorizedException extends ClientErrorException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
		super(401, message);
	}

}
