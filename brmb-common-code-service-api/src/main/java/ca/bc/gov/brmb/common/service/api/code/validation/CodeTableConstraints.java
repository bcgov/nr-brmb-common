package ca.bc.gov.brmb.common.service.api.code.validation;

import jakarta.validation.constraints.NotBlank;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeTable;

public interface CodeTableConstraints extends CodeTable<Code> {
	
	@Override
	@NotBlank(message=Errors.CODE_TABLE_NAME_NOTBLANK, groups=CodeTableConstraints.class)
	public String getCodeTableName();

}
