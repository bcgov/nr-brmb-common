package ca.bc.gov.nrs.wfone.common.checkhealth;

import ca.bc.gov.brmb.common.rest.resource.HealthCheckResponseRsrc;
import ca.bc.gov.brmb.common.model.ValidationStatus;

public interface CheckHealthValidator {

	void init();

	HealthCheckResponseRsrc validate(final String callstack);	
	
	String getComponentIdentifier();

	String getComponentName();

	ValidationStatus getRedMapping();
	void setRedMapping(ValidationStatus redMapping);

	ValidationStatus getYellowMapping();
	void setYellowMapping(ValidationStatus yellowMapping);

	ValidationStatus getGreenMapping();
	void setGreenMapping(ValidationStatus greenMapping);

}
