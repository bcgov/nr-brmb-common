package ca.bc.gov.nrs.wfone.common.model;

import java.util.List;

public interface HealthCheckResponse<R extends HealthCheckResponse<?>> {

	public String getComponentIdentifier();
	public void setComponentIdentifier(String componentIdentifier);

	public String getComponentName();
	public void setComponentName(String componentName);

	public ValidationStatus getValidationStatus();
	public void setValidationStatus(ValidationStatus validationStatus);

	public String getStatusDetails();
	public void setStatusDetails(String statusDetails);

	public List<R> getDependencyComponentResponses();
	public void setDependencyComponentResponses(List<R> dependencyComponentResponses);
}
