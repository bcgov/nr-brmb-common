package ca.bc.gov.nrs.wfone.common.persistence.dao.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.nrs.wfone.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.wfone.common.persistence.dao.OptimisticLockingFailureDaoException;

public class BaseDao {

	private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

	protected Long getRevisionCount(String optimisticLock) throws OptimisticLockingFailureDaoException {
		logger.debug("<getRevisionCount");

		Long result = null;

		if (optimisticLock == null || optimisticLock.trim().length() == 0) {
			throw new OptimisticLockingFailureDaoException("lock is null");
		}

		try {
			result = Long.valueOf(optimisticLock);
		} catch (NumberFormatException e) {
			throw new OptimisticLockingFailureDaoException("invalid format "
					+ optimisticLock);
		}

		logger.debug(">getRevisionCount " + result);
		return result;
	}

	protected void handleException(Throwable e) throws DaoException {
		logger.debug("<handleException " + e.getClass());

			throw new DaoException(e);
	}
}
