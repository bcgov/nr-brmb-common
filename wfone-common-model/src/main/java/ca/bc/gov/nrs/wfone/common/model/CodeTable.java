package ca.bc.gov.nrs.wfone.common.model;

import java.io.Serializable;
import java.util.List;

public interface CodeTable<C extends Code> extends Serializable {
	
	public String getCodeTableName();

	public void setCodeTableName(String codeTableName);

	public List<C> getCodes();

	public void setCodes(List<C> codes);
}
