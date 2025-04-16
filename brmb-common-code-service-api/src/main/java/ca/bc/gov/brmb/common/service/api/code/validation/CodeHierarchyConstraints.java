package ca.bc.gov.brmb.common.service.api.code.validation;

import javax.validation.constraints.NotBlank;

public interface CodeHierarchyConstraints {

	@NotBlank(message=Errors.CODE_HIERARCHY_NAME_NOTBLANK, groups=CodeHierarchyConstraints.class)
	public String getCodeHierarchyName();

	@NotBlank(message=Errors.CODE_HIERARCHY_LOWER_TABLE_NAME_NOTBLANK, groups=CodeHierarchyConstraints.class)
	public String getLowerCodeTableName();

	@NotBlank(message=Errors.CODE_HIERARCHY_UPPER_TABLE_NAME_NOTBLANK, groups=CodeHierarchyConstraints.class)
	public String getUpperCodeTableName();
}
