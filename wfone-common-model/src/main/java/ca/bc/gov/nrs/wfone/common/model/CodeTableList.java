package ca.bc.gov.nrs.wfone.common.model;

import java.io.Serializable;
import java.util.List;

public interface CodeTableList<E extends CodeTable<?>> extends Serializable {

	public List<E> getCodeTableList();

	public void setCodeTableList(List<E> codeTableList);
}
