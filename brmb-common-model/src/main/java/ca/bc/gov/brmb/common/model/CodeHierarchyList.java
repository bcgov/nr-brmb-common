package ca.bc.gov.brmb.common.model;

import java.io.Serializable;
import java.util.List;

public interface CodeHierarchyList<E extends CodeHierarchy> extends Serializable {

	public List<E> getCodeHierarchyList();

	public void setCodeHierarchyList(List<E> codeHierarchyList);
}
