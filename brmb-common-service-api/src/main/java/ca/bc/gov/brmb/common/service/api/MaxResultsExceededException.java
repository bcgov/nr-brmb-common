package ca.bc.gov.brmb.common.service.api;

public class MaxResultsExceededException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public MaxResultsExceededException(String message, Throwable t) {
		super(message, t);
	}

}
