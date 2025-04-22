package ca.bc.gov.brmb.common.persistence.code.dto;

import java.io.Serializable;
import java.util.List;

public class CodeTableDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String codeTableName;

	private List<CodeDto> codes;

	public String getCodeTableName() {
		return codeTableName;
	}

	public void setCodeTableName(String codeTableName) {
		this.codeTableName = codeTableName;
	}

	public List<CodeDto> getCodes() {
		return codes;
	}

	public void setCodes(List<CodeDto> codes) {
		this.codes = codes;
	}
}
