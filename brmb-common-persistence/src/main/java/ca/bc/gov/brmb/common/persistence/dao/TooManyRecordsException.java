package ca.bc.gov.brmb.common.persistence.dao;

public class TooManyRecordsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TooManyRecordsException(String message) {
		super(message);
	}

}
