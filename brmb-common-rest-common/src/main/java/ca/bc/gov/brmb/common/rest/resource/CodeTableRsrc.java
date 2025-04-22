package ca.bc.gov.brmb.common.rest.resource;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.CodeTable;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.CODE_TABLE_NAME)
@JsonSubTypes({ @Type(value = CodeTableRsrc.class, name = BaseResourceTypes.CODE_TABLE) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class CodeTableRsrc extends BaseResource implements CodeTable<CodeRsrc> {
	
	private static final long serialVersionUID = 1L;

	private String codeTableName;
	
	private List<CodeRsrc> codes;
	
	@Override
	public String getCodeTableName() {
		return codeTableName;
	}

	@Override
	public void setCodeTableName(String codeTableName) {
		this.codeTableName = codeTableName;
	}

	@Override
	public List<CodeRsrc> getCodes() {
		return codes;
	}

	@Override
	public void setCodes(List<CodeRsrc> codes) {
		this.codes = codes;
	}
	
}
