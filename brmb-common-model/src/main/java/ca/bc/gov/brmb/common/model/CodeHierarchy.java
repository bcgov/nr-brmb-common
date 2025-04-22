package ca.bc.gov.brmb.common.model;

import java.io.Serializable;
import java.util.List;

public interface CodeHierarchy extends Serializable {
	
	public String getCodeHierarchyName();
	public void setCodeHierarchyName(String codeHierarchyName);

	public String getLowerCodeTableName();
	public void setLowerCodeTableName(String lowerCodeTableName);

	public String getUpperCodeTableName();
	public void setUpperCodeTableName(String upperCodeTableName);

	public List<HierarchyImpl> getHierarchy();
	public void setHierarchy(List<HierarchyImpl> hierarchy);
}
