package ca.bc.gov.nrs.wfone.common.persistence.code.dto;

import java.io.Serializable;
import java.util.List;

public class CodeHierarchyDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String codeHierarchyName;
	private String lowerCodeTableName;
	private String upperCodeTableName;

	private List<HierarchyDto> hierarchy;

	public String getCodeHierarchyName() {
		return codeHierarchyName;
	}

	public void setCodeHierarchyName(String codeHierarchyName) {
		this.codeHierarchyName = codeHierarchyName;
	}

	public String getLowerCodeTableName() {
		return lowerCodeTableName;
	}

	public void setLowerCodeTableName(String lowerCodeTableName) {
		this.lowerCodeTableName = lowerCodeTableName;
	}

	public String getUpperCodeTableName() {
		return upperCodeTableName;
	}

	public void setUpperCodeTableName(String upperCodeTableName) {
		this.upperCodeTableName = upperCodeTableName;
	}

	public List<HierarchyDto> getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(List<HierarchyDto> hierarchy) {
		this.hierarchy = hierarchy;
	}
}
