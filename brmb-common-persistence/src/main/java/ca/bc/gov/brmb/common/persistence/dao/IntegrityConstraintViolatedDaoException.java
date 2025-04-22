package ca.bc.gov.brmb.common.persistence.dao;


public class IntegrityConstraintViolatedDaoException extends DaoException {

	private static final long serialVersionUID = 1L;

	public IntegrityConstraintViolatedDaoException(String message) {
		super(message);
	}

	public IntegrityConstraintViolatedDaoException(String message, Throwable t) {
		super(message, t);
	}
}
