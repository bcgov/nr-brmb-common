package ca.bc.gov.nrs.wfone.common.api.rest.code.parameters;

import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.CodeHierarchyQueryParametersConstraints;

public class CodeHierarchyQueryParameters extends EffectiveAsOfParameter implements CodeHierarchyQueryParametersConstraints {

	private static final long serialVersionUID = 1L;
	
	private String codeHierarchyName;
	
	public CodeHierarchyQueryParameters() {}

	@Override
	public String getCodeHierarchyName() {
		return codeHierarchyName;
	}

	public void setCodeHierarchyName(String codeHierarchyName) {
		this.codeHierarchyName = codeHierarchyName;
	}

}
