package ca.bc.gov.nrs.wfone.common.service.api.code.validation;

import javax.validation.constraints.NotBlank;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeTable;

public interface CodeTableConstraints extends CodeTable<Code> {
	
	@Override
	@NotBlank(message=Errors.CODE_TABLE_NAME_NOTBLANK, groups=CodeTableConstraints.class)
	public String getCodeTableName();

}
