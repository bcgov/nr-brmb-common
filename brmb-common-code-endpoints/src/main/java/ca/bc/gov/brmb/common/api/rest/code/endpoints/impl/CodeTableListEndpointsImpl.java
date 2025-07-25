package ca.bc.gov.brmb.common.api.rest.code.endpoints.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.brmb.common.rest.resource.CodeTableListRsrc;
import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.api.rest.code.endpoints.CodeTableListEndpoints;
import ca.bc.gov.brmb.common.api.rest.code.parameters.CodeTableQueryParameters;
import ca.bc.gov.brmb.common.api.rest.code.parameters.validation.CodeParameterValidator;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.brmb.common.service.api.code.CodeService;
import ca.bc.gov.brmb.common.utils.DateUtils;

public class CodeTableListEndpointsImpl extends BaseEndpointsImpl implements
		CodeTableListEndpoints {
	private static final Logger logger = LoggerFactory
			.getLogger(CodeTableListEndpointsImpl.class);

	@Autowired
	private CodeService service;

	@Autowired
	private CodeParameterValidator codeParameterValidator;

	@Override
	public Response getCodeTableList(String effectiveAsOfDate, String codeTableName) {
		logger.debug("<getCodeTableList");
		Response response = null;
		
		logRequest();

		try {

			CodeTableQueryParameters parameters = new CodeTableQueryParameters();

			parameters.setEffectiveAsOfDate(effectiveAsOfDate);
			parameters.setCodeTableName(codeTableName);

			List<Message> validation = new ArrayList<>();
			validation.addAll(this.codeParameterValidator.validateCodeTableQueryParameters(parameters));
			
			MessageListRsrc validationMessages = new MessageListRsrc(validation);
			if (validationMessages.hasMessages()) {
				response = Response.status(Status.BAD_REQUEST)
						.entity(validationMessages).build();
			} else {

				CodeTableListRsrc results = (CodeTableListRsrc)service.getCodeTableList(DateUtils.toLocalDate(effectiveAsOfDate), codeTableName, getFactoryContext());
	
				GenericEntity<CodeTableListRsrc> entity = new GenericEntity<CodeTableListRsrc>(
						results) {
					/* do nothing */
				};
	
				response = Response.ok(entity).tag(results.getUnquotedETag()).build();
			}
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		logger.debug(">getCodeTableList " + response.getStatus());
		return response;
	}
}
