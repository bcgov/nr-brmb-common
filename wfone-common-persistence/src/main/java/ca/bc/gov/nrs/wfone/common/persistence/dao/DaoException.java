package ca.bc.gov.nrs.wfone.common.persistence.dao;


public class DaoException extends Exception {

	private static final long serialVersionUID = 1L;

	public DaoException(Throwable t) {
		super(t);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable t) {
		super(message, t);
	}
}
