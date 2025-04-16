package ca.bc.gov.brmb.common.api.rest.code.parameters;

import java.io.Serializable;

public class CodeQueryParameters implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String validDate;
	
	public CodeQueryParameters() {}

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}

}
