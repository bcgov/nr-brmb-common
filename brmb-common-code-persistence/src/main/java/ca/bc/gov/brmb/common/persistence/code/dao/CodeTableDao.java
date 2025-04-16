package ca.bc.gov.brmb.common.persistence.code.dao;

import java.io.Serializable;
import java.time.LocalDate;

import ca.bc.gov.brmb.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;

public interface CodeTableDao extends Serializable {

	public CodeTableDto fetch(CodeTableConfig codeTableConfig, LocalDate effectiveAsOfDate) throws DaoException;

	public void update(
			CodeTableConfig codeTableConfig, 
			CodeTableDto dto,
			String optimisticLock, 
			String currentUserId) throws DaoException;

}
