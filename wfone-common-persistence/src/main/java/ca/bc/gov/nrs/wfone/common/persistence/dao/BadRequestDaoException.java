package ca.bc.gov.nrs.wfone.common.persistence.dao;


public class BadRequestDaoException extends DaoException {

	private static final long serialVersionUID = 1L;

	public BadRequestDaoException(String message) {
		super(message);
	}

	public BadRequestDaoException(String message, Throwable t) {
		super(message, t);
	}
}
