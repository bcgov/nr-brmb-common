package ca.bc.gov.brmb.common.service.api.code.validation;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import ca.bc.gov.brmb.common.model.Hierarchy;

public interface HierarchyConstraints extends Hierarchy {

	@Override
	@NotBlank(message=Errors.HIERARCHY_LOWER_CODE_NOTBLANK, groups=HierarchyConstraints.class)
	public String getLowerCode();

	@Override
	@NotBlank(message=Errors.HIERARCHY_UPPER_CODE_NOTBLANK, groups=HierarchyConstraints.class)
	public String getUpperCode();

	@Override
	@NotNull(message=Errors.HIERARCHY_EFFECTIVE_DATE_NOTBLANK, groups=HierarchyConstraints.class)
	public LocalDate getEffectiveDate();

	@Override
	@NotNull(message=Errors.HIERARCHY_EXPIRY_DATE_NOTBLANK, groups=HierarchyConstraints.class)
	public LocalDate getExpiryDate();

}
