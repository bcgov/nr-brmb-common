package ca.bc.gov.nrs.common.wfone.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.CodeHierarchyList;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.CODE_HIERARCHY_LIST_NAME)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class CodeHierarchyListRsrc extends BaseResource implements CodeHierarchyList<CodeHierarchyRsrc> {

	private static final long serialVersionUID = 1L;

	private List<CodeHierarchyRsrc> codeHierarchyList = new ArrayList<CodeHierarchyRsrc>(0);

	public CodeHierarchyListRsrc() {
		this.codeHierarchyList = new ArrayList<CodeHierarchyRsrc>();
	}

	@Override
	public List<CodeHierarchyRsrc> getCodeHierarchyList() {
		return codeHierarchyList;
	}

	@Override
	public void setCodeHierarchyList(List<CodeHierarchyRsrc> codeHierarchyList) {
		this.codeHierarchyList = codeHierarchyList;
	}
}
