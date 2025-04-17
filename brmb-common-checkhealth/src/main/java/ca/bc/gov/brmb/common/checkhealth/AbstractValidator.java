package ca.bc.gov.brmb.common.checkhealth;

import ca.bc.gov.brmb.common.model.ValidationStatus;

public abstract class AbstractValidator implements CheckHealthValidator {

	private ValidationStatus redMapping = ValidationStatus.RED;
	private ValidationStatus yellowMapping = ValidationStatus.YELLOW;
	private ValidationStatus greenMapping = ValidationStatus.GREEN;

	@Override
	public ValidationStatus getRedMapping() {
		return redMapping;
	}

	@Override
	public void setRedMapping(ValidationStatus redMapping) {
		this.redMapping = redMapping;
	}

	@Override
	public ValidationStatus getYellowMapping() {
		return yellowMapping;
	}

	@Override
	public void setYellowMapping(ValidationStatus yellowMapping) {
		this.yellowMapping = yellowMapping;
	}

	@Override
	public ValidationStatus getGreenMapping() {
		return greenMapping;
	}

	@Override
	public void setGreenMapping(ValidationStatus greenMapping) {
		this.greenMapping = greenMapping;
	}

}
