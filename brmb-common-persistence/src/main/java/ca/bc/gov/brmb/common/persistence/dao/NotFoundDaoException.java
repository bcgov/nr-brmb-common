package ca.bc.gov.brmb.common.persistence.dao;


public class NotFoundDaoException extends DaoException {

	private static final long serialVersionUID = 1L;

	public NotFoundDaoException(String message) {
		super(message);
	}

	public NotFoundDaoException(String message, Throwable t) {
		super(message, t);
	}
}
