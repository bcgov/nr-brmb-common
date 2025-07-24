package ca.bc.gov.brmb.common.rest.resource;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.CodeTableList;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.CODE_TABLE_LIST_NAME)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class CodeTableListRsrc extends BaseResource implements CodeTableList<CodeTableRsrc> {

	private static final long serialVersionUID = 1L;

	private List<CodeTableRsrc> codeTableList = new ArrayList<CodeTableRsrc>(0);

	public CodeTableListRsrc() {
		this.codeTableList = new ArrayList<CodeTableRsrc>();
	}

	@Override
	public List<CodeTableRsrc> getCodeTableList() {
		return codeTableList;
	}

	@Override
	public void setCodeTableList(List<CodeTableRsrc> codeTableList) {
		this.codeTableList = codeTableList;
	}
}
