package ca.bc.gov.nrs.wfone.common.persistence.dao;


public class OptimisticLockingFailureDaoException extends DaoException {

	private static final long serialVersionUID = 1L;

	public OptimisticLockingFailureDaoException(String message) {
		super(message);
	}

	public OptimisticLockingFailureDaoException(String message, Throwable t) {
		super(message, t);
	}
}
