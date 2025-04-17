package ca.bc.gov.brmb.common.api.rest.code.endpoints.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.brmb.common.rest.resource.CodeTableRsrc;
import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.api.rest.code.endpoints.CodeTableEndpoints;
import ca.bc.gov.brmb.common.api.rest.code.parameters.EffectiveAsOfParameter;
import ca.bc.gov.brmb.common.api.rest.code.parameters.validation.CodeParameterValidator;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.brmb.common.service.api.ConflictException;
import ca.bc.gov.brmb.common.service.api.ForbiddenException;
import ca.bc.gov.brmb.common.service.api.NotFoundException;
import ca.bc.gov.brmb.common.service.api.ValidationFailureException;
import ca.bc.gov.brmb.common.service.api.code.CodeService;
import ca.bc.gov.brmb.common.utils.DateUtils;

public class CodeTableEndpointsImpl extends BaseEndpointsImpl implements
		CodeTableEndpoints {
	private static final Logger logger = LoggerFactory
			.getLogger(CodeTableEndpointsImpl.class);

	@Autowired
	private CodeService service;

	@Autowired
	private CodeParameterValidator codeParameterValidator;

	@Override
	public Response getCodeTable(String codeTableName, String effectiveAsOfDate) {
		logger.debug("<getCodeTable");
		Response response = null;
		
		logRequest();

		try {		
			
			EffectiveAsOfParameter parameters = new EffectiveAsOfParameter();

			parameters.setEffectiveAsOfDate(effectiveAsOfDate);

			List<Message> validation = new ArrayList<>();
			validation.addAll(this.codeParameterValidator.validateEffectiveAsOfParameter(parameters));
			
			MessageListRsrc validationMessages = new MessageListRsrc(validation);
			if (validationMessages.hasMessages()) {
				
				response = Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
			} else {

				CodeTableRsrc result = (CodeTableRsrc) service.getCodeTable(codeTableName, DateUtils.toLocalDate(effectiveAsOfDate), getFactoryContext(), getWebAdeAuthentication());
	
				response = Response.ok(result).tag(result.getUnquotedETag()).build();
			}

		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
			
		} catch (ForbiddenException fe) {
			response = Response.status(Status.FORBIDDEN).build();
			
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		logger.debug(">getCodeTable " + response);
		return response;
	}

	@Override
	public Response updateCodeTable(String codeTableName, CodeTableRsrc updatedCodeTable) {
		logger.debug("<updateCodeTable");

		Response response = null;
		
		logRequest();

		try {
			CodeTableRsrc currentCodeTable = (CodeTableRsrc) this.service.getCodeTable(codeTableName, null, getFactoryContext(), getWebAdeAuthentication());

			EntityTag currentTag = EntityTag.valueOf(currentCodeTable.getQuotedETag());

			ResponseBuilder responseBuilder = this.evaluatePreconditions(currentTag);

			if (responseBuilder == null) {
				// Preconditions Are Met

				String optimisticLock = getIfMatchHeader();

				CodeTableRsrc result = (CodeTableRsrc) service.updateCodeTable(codeTableName, optimisticLock, updatedCodeTable, getFactoryContext(), getWebAdeAuthentication());

				response = Response.ok(result).tag(result.getUnquotedETag()).build();
				
			} else {
				// Preconditions Are NOT Met

				response = responseBuilder.tag(currentTag).build();
			}
		} catch (ForbiddenException e) {
			response = Response.status(Status.FORBIDDEN).build();
		} catch(ValidationFailureException e) {
			response = Response.status(Status.BAD_REQUEST).entity(new MessageListRsrc(e.getValidationErrors())).build();
		} catch (ConflictException e) {
			response = Response.status(Status.CONFLICT).entity(e.getMessage()).build();
		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		logger.debug(">updateCodeTable " + response.getStatus());
		return response;
	}
}
