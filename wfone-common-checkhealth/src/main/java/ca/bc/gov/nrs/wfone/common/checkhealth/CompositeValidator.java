package ca.bc.gov.nrs.wfone.common.checkhealth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.nrs.common.wfone.rest.resource.HealthCheckResponseRsrc;
import ca.bc.gov.brmb.common.model.ValidationStatus;

public class CompositeValidator extends AbstractValidator {

	private final Logger logger = LoggerFactory.getLogger(CompositeValidator.class);

	protected String componentIdentifier;
	protected String componentName;
	protected List<CheckHealthValidator> validators;
	
	@Override
	public void init() {
		if(componentIdentifier==null||componentIdentifier.trim().length()==0) {
			throw new IllegalArgumentException("componentIdentifier cannot be null.");
		}
		if(componentName==null||componentName.trim().length()==0) {
			throw new IllegalArgumentException("componentName cannot be null.");
		}
		if(validators==null) {
			throw new IllegalArgumentException("validators cannot be null.");
		}
	}
	
	@Override
	public HealthCheckResponseRsrc validate(final String callstack) {
		logger.debug("<validate "+this.getComponentName());
		
		HealthCheckResponseRsrc result = new HealthCheckResponseRsrc();
		result.setComponentIdentifier(componentIdentifier);
		result.setComponentName(componentName);
		result.setValidationStatus(ValidationStatus.GREEN);
		
		try {	
			
			final CountDownLatch latch = new CountDownLatch(validators.size());
			List<ValidationTask> tasks = new ArrayList<>();
			
			for(CheckHealthValidator validator:validators) {
				
				ValidationTask task = new ValidationTask(latch, validator, callstack);
				tasks.add(task);
				new Thread(task).start();
			}
			
			while(latch.getCount()>0) {
				try {
					latch.await();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
			
			for(ValidationTask task:tasks) {
				
				HealthCheckResponseRsrc response = task.getResponse();
				
				if(response.getValidationStatus().intValue()>result.getValidationStatus().intValue()) {
					result.setValidationStatus(response.getValidationStatus());
				}
				result.getDependencyComponentResponses().add(response);
			}
			
			ValidationStatus validationStatus = result.getValidationStatus();
			switch(validationStatus) {
			case GREEN:
				result.setStatusDetails("All dependencies reporting Green.");
				break;
			case RED:
				result.setStatusDetails("One or more dependencies reporting Red.");
				break;
			case YELLOW:
				result.setStatusDetails("One or more dependencies reporting Yellow.");
				break;
			default:
				throw new IllegalStateException("Unsupported validation status: "+validationStatus);
			}
			
		} catch(Throwable t) {
			result.setValidationStatus(this.getRedMapping());
			result.setStatusDetails(componentName+" Health Check failed: "+t.getMessage());
		}

		logger.debug(">validate "+this.getComponentName());
		return result;
	}

	@Override
	public String getComponentIdentifier() {
		return componentIdentifier;
	}

	public void setComponentIdentifier(String componentIdentifier) {
		this.componentIdentifier = componentIdentifier;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public void setValidators(List<CheckHealthValidator> validators) {
		this.validators = validators;
	}

}
