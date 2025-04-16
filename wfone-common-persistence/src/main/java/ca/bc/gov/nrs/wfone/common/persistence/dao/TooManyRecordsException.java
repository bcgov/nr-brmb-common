package ca.bc.gov.nrs.wfone.common.persistence.dao;

public class TooManyRecordsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public TooManyRecordsException(String message) {
		super(message);
	}

}
