package ca.bc.gov.nrs.wfone.common.service.api.code.validation;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.bc.gov.brmb.common.model.Code;

public interface CodeConstraints extends Code {

	@Override
	@NotBlank(message=Errors.CODE_NAME_NOTBLANK, groups=CodeConstraints.class)
	public String getCode();

	@Override
	@NotBlank(message=Errors.CODE_DESCRIPTION_NOTBLANK, groups=CodeConstraints.class)
	@Size(max=120, message=Errors.CODE_DESCRIPTION_SIZE, groups=CodeConstraints.class)
	public String getDescription();

	@Override
	@NotNull(message=Errors.CODE_EFFECTIVE_DATE_NOTBLANK, groups=CodeConstraints.class)
	public LocalDate getEffectiveDate();

	@Override
	public LocalDate getExpiryDate();
}
