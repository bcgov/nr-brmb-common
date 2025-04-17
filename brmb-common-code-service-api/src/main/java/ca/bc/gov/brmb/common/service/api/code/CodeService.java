package ca.bc.gov.brmb.common.service.api.code;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeHierarchy;
import ca.bc.gov.brmb.common.model.CodeHierarchyList;
import ca.bc.gov.brmb.common.model.CodeTable;
import ca.bc.gov.brmb.common.model.CodeTableList;
import ca.bc.gov.brmb.common.service.api.ConflictException;
import ca.bc.gov.brmb.common.service.api.ForbiddenException;
import ca.bc.gov.brmb.common.service.api.NotFoundException;
import ca.bc.gov.brmb.common.service.api.ServiceException;
import ca.bc.gov.brmb.common.service.api.ValidationFailureException;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;

public interface CodeService {

	@Transactional(transactionManager="transactionManager", readOnly = true, rollbackFor = Exception.class)
	public CodeTableList<? extends CodeTable<? extends Code>> getCodeTableList(
			LocalDate effectiveAsOfDate, 
			String codeTableName,
			FactoryContext context) throws ServiceException;

	@Transactional(transactionManager="transactionManager", readOnly = true, rollbackFor = Exception.class)
	public CodeTable<? extends Code> getCodeTable(
			String codeTableName, 
			LocalDate effectiveAsOfDate, 
			FactoryContext factoryContext)
			throws ServiceException, ForbiddenException, NotFoundException;

	@Transactional(transactionManager="transactionManager",readOnly = false, rollbackFor = Exception.class)
	public CodeTable<? extends Code> updateCodeTable(
			String codeTableName, 
			String optimisticLock, 
			CodeTable<? extends Code> updatedCodeTable,
			FactoryContext factoryContext) throws ServiceException, NotFoundException,
			ForbiddenException, ConflictException, ValidationFailureException;

	@Transactional(transactionManager="transactionManager", readOnly = true, rollbackFor = Exception.class)
	public CodeHierarchyList<? extends CodeHierarchy> getCodeHierarchyList(
			LocalDate effectiveAsOfDate,
			String codeHierarchyName, 
			FactoryContext context) throws ServiceException;

	@Transactional(transactionManager="transactionManager", readOnly = true, rollbackFor = Exception.class)
	public CodeHierarchy getCodeHierarchy(
			String codeHierarchyName, 
			LocalDate effectiveAsOfDate,
			FactoryContext factoryContext) throws ServiceException, ForbiddenException, NotFoundException;

	@Transactional(transactionManager="transactionManager",readOnly = false, rollbackFor = Exception.class)
	public CodeHierarchy updateCodeHierarchy(
			String codeHierarchyName, 
			String optimisticLock,
			CodeHierarchy updatedCodeHierarchy, 
			FactoryContext factoryContext)
			throws ServiceException, NotFoundException, ForbiddenException, ConflictException,
			ValidationFailureException;

}
