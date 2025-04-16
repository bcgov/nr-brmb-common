package ca.bc.gov.nrs.wfone.common.api.rest.code.parameters;

import java.io.Serializable;

import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.EffectiveAsOfParameterConstraints;

public class EffectiveAsOfParameter implements EffectiveAsOfParameterConstraints, Serializable {

	private static final long serialVersionUID = 1L;

	private String effectiveAsOfDate;

	public String getEffectiveAsOfDate() {
		return effectiveAsOfDate;
	}

	public void setEffectiveAsOfDate(String effectiveAsOfDate) {
		this.effectiveAsOfDate = effectiveAsOfDate;
	}

}
