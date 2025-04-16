package ca.bc.gov.nrs.common.wfone.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.nrs.wfone.common.model.HealthCheckResponse;
import ca.bc.gov.nrs.wfone.common.model.ValidationStatus;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.HEALTH_CHECK_RESPONSE_NAME)
@JsonSubTypes({ @Type(value = HealthCheckResponseRsrc.class, name = BaseResourceTypes.HEALTH_CHECK_RESPONSE) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class HealthCheckResponseRsrc implements HealthCheckResponse<HealthCheckResponseRsrc> {

	private String componentIdentifier;
	private String componentName;
	private ValidationStatus validationStatus;
	private String statusDetails;
	private List<HealthCheckResponseRsrc> dependencyComponentResponses = new ArrayList<HealthCheckResponseRsrc>();

	public String getComponentIdentifier() {
		return componentIdentifier;
	}

	public void setComponentIdentifier(String componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public ValidationStatus getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(ValidationStatus validationStatus) {
		this.validationStatus = validationStatus;
	}

	public String getStatusDetails() {
		return statusDetails;
	}

	public void setStatusDetails(String statusDetails) {
		this.statusDetails = statusDetails;
	}

	public List<HealthCheckResponseRsrc> getDependencyComponentResponses() {
		return dependencyComponentResponses;
	}

	public void setDependencyComponentResponses(List<HealthCheckResponseRsrc> dependencyComponentResponses) {
		this.dependencyComponentResponses = dependencyComponentResponses;
	}
}
