package ca.bc.gov.nrs.wfone.common.service.api.code.model.factory;

import java.time.LocalDate;
import java.util.List;

import ca.bc.gov.brmb.common.model.CodeHierarchy;
import ca.bc.gov.brmb.common.model.CodeHierarchyList;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryException;

public interface CodeHierarchyFactory {

	public CodeHierarchyList<? extends CodeHierarchy> getCodeHierarchys(LocalDate effectiveAsOfDate, String codeHierarchyName, List<CodeHierarchyDto> dtos, FactoryContext context) throws FactoryException;

	public CodeHierarchy getCodeHierarchy(CodeHierarchyDto dto, LocalDate effectiveAsOfDate, boolean canUpdate, FactoryContext context) throws FactoryException;
}
