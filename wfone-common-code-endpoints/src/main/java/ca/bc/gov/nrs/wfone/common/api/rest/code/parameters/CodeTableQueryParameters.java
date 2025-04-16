package ca.bc.gov.nrs.wfone.common.api.rest.code.parameters;

import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.CodeTableQueryParametersConstraints;

public class CodeTableQueryParameters extends EffectiveAsOfParameter implements CodeTableQueryParametersConstraints {

	private static final long serialVersionUID = 1L;
	
	private String codeTableName;
	
	public CodeTableQueryParameters() {}

	@Override
	public String getCodeTableName() {
		return codeTableName;
	}

	public void setCodeTableName(String codeTableName) {
		this.codeTableName = codeTableName;
	}

}
