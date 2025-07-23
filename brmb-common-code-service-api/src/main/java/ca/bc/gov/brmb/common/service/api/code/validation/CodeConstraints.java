package ca.bc.gov.brmb.common.service.api.code.validation;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
