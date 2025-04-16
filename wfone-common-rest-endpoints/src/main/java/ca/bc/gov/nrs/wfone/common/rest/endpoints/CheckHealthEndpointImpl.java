package ca.bc.gov.nrs.wfone.common.rest.endpoints;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.nrs.common.wfone.rest.resource.HealthCheckResponseRsrc;
import ca.bc.gov.nrs.wfone.common.checkhealth.CheckHealthValidator;
import ca.bc.gov.nrs.wfone.common.checkhealth.DependencyLoopException;
import ca.bc.gov.nrs.wfone.common.checkhealth.MissingCallstackException;
import ca.bc.gov.nrs.wfone.common.model.ValidationStatus;

public class CheckHealthEndpointImpl extends BaseEndpointsImpl implements CheckHealthEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(CheckHealthEndpointImpl.class);
	
	@Autowired
	private CheckHealthValidator checkHealthValidator;
	
	@Override
	public Response checkHealth(final String callstack) {
		logger.debug("<checkHealth "+callstack);
		Response response = null;
		
		logRequest();
		
		try {
			HealthCheckResponseRsrc result = doCheckHealth(callstack);
			response = Response.ok(result).build();
		} catch (MissingCallstackException e) {
			response = Response.status(Status.BAD_REQUEST).build();
		} catch (DependencyLoopException e) {
			response = Response.status(Status.NO_CONTENT).build();
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		logger.debug(">checkHealth");
		return response;
	}	
	
	private HealthCheckResponseRsrc doCheckHealth(String callstack)
			throws DependencyLoopException, MissingCallstackException {
		logger.debug("<checkHealth");
		
		if(callstack==null||callstack.trim().length()==0) {
			throw new MissingCallstackException();
		}
		
		String componentIdentifier = checkHealthValidator.getComponentIdentifier().trim();
		String componentName = checkHealthValidator.getComponentName();
		
		String[] identifiers = callstack.split(",");
		for(String identifier:identifiers) {
			if(identifier.trim().equals(componentIdentifier)) {
				throw new DependencyLoopException();
			}
		}
		
		HealthCheckResponseRsrc result = null;
		
		try {
			
			result = checkHealthValidator.validate(callstack+","+componentIdentifier);
		
		} catch(Throwable t) {
			result = new HealthCheckResponseRsrc();
			result.setComponentIdentifier(componentIdentifier);
			result.setComponentName(componentName);
			result.setValidationStatus(ValidationStatus.RED);
			result.setStatusDetails("Failed to perform healthcheck due to unexpected error: "+t.getMessage());
		}
		
		logger.debug(">checkHealth "+result);
		return result;
	}

}
