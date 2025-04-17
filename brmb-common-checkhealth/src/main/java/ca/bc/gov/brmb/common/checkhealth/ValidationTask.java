package ca.bc.gov.brmb.common.checkhealth;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.rest.resource.HealthCheckResponseRsrc;

public class ValidationTask extends RequestAwareRunnable {

	private final Logger logger = LoggerFactory.getLogger(ValidationTask.class);

	private CountDownLatch latch;
	private CheckHealthValidator validator;
	private String callstack;
	private HealthCheckResponseRsrc response;
	
	public ValidationTask(CountDownLatch latch, CheckHealthValidator validator, String callstack) {
		super();
		
		this.validator = validator;
		this.callstack = callstack;
		this.latch = latch;
	}
	
	@Override
	protected void onRun() {
		logger.debug("<onRun "+this.validator.getComponentName());
		
		this.response = this.validator.validate(this.callstack);
		
		logger.debug(this.validator.getComponentName()+" completed.");
		
		this.latch.countDown();
		
		logger.debug(">onRun "+this.validator.getComponentName());
	}

	public HealthCheckResponseRsrc getResponse() {
		return response;
	}

}
