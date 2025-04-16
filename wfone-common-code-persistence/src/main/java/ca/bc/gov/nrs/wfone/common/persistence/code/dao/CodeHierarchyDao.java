package ca.bc.gov.nrs.wfone.common.persistence.code.dao;

import java.io.Serializable;
import java.time.LocalDate;

import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;

public interface CodeHierarchyDao extends Serializable {

	public CodeHierarchyDto fetch(CodeHierarchyConfig codeHierarchyConfig, LocalDate effectiveAsOfDate) throws DaoException;

	public void update(
			CodeHierarchyConfig codeHierarchyConfig, 
			CodeHierarchyDto dto,
			String optimisticLock, 
			String currentUserId) throws DaoException;

}
