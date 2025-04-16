package ca.bc.gov.nrs.wfone.common.service.api.code.model.factory;

import java.time.LocalDate;
import java.util.List;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeTable;
import ca.bc.gov.brmb.common.model.CodeTableList;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryException;

public interface CodeTableFactory {

	public CodeTableList<? extends CodeTable<? extends Code>> getCodeTables(LocalDate effectiveAsOfDate, String codeTableName, List<CodeTableDto> dtos, FactoryContext context) throws FactoryException;

	public CodeTable<? extends Code> getCodeTable(CodeTableDto dto, LocalDate effectiveAsOfDate, boolean canUpdate, FactoryContext context) throws FactoryException;
}
