package ca.bc.gov.nrs.common.wfone.rest.resource;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.nrs.wfone.common.model.CodeHierarchy;
import ca.bc.gov.nrs.wfone.common.model.HierarchyImpl;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.CODE_HIERARCHY_NAME)
@JsonSubTypes({ @Type(value = CodeHierarchyRsrc.class, name = BaseResourceTypes.CODE_HIERARCHY) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class CodeHierarchyRsrc extends BaseResource implements CodeHierarchy {
	
	private static final long serialVersionUID = 1L;

	private String codeHierarchyName;

	private String lowerCodeTableName;

	private String upperCodeTableName;
	
	private List<HierarchyImpl> hierarchy;

	@Override
	public String getCodeHierarchyName() {
		return codeHierarchyName;
	}

	@Override
	public void setCodeHierarchyName(String codeHierarchyName) {
		this.codeHierarchyName = codeHierarchyName;
	}

	@Override
	public String getLowerCodeTableName() {
		return lowerCodeTableName;
	}

	@Override
	public void setLowerCodeTableName(String lowerCodeTableName) {
		this.lowerCodeTableName = lowerCodeTableName;
	}

	@Override
	public String getUpperCodeTableName() {
		return upperCodeTableName;
	}

	@Override
	public void setUpperCodeTableName(String upperCodeTableName) {
		this.upperCodeTableName = upperCodeTableName;
	}

	@Override
	public List<HierarchyImpl> getHierarchy() {
		return hierarchy;
	}

	@Override
	public void setHierarchy(List<HierarchyImpl> hierarchy) {
		this.hierarchy = hierarchy;
	}
	
}
