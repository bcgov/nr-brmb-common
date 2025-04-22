package ca.bc.gov.brmb.common.api.rest.code.endpoints.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.brmb.common.rest.resource.CodeHierarchyListRsrc;
import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.api.rest.code.endpoints.CodeHierarchyListEndpoints;
import ca.bc.gov.brmb.common.api.rest.code.parameters.CodeHierarchyQueryParameters;
import ca.bc.gov.brmb.common.api.rest.code.parameters.validation.CodeParameterValidator;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.brmb.common.service.api.code.CodeService;
import ca.bc.gov.brmb.common.utils.DateUtils;

public class CodeHierarchyListEndpointsImpl extends BaseEndpointsImpl implements
		CodeHierarchyListEndpoints {
	private static final Logger logger = LoggerFactory
			.getLogger(CodeHierarchyListEndpointsImpl.class);

	@Autowired
	private CodeService service;

	@Autowired
	private CodeParameterValidator codeParameterValidator;

	@Override
	public Response getCodeHierarchyList(String effectiveAsOfDate, String codeHierarchyName) {
		logger.debug("<getCodeHierarchyList");
		Response response = null;
		
		logRequest();

		try {

			CodeHierarchyQueryParameters parameters = new CodeHierarchyQueryParameters();

			parameters.setEffectiveAsOfDate(effectiveAsOfDate);
			parameters.setCodeHierarchyName(codeHierarchyName);

			List<Message> validation = new ArrayList<>();
			validation.addAll(this.codeParameterValidator.validateCodeHierarchyQueryParameters(parameters));
			
			MessageListRsrc validationMessages = new MessageListRsrc(validation);
			if (validationMessages.hasMessages()) {
				response = Response.status(Status.BAD_REQUEST)
						.entity(validationMessages).build();
			} else {

				CodeHierarchyListRsrc results = (CodeHierarchyListRsrc)service.getCodeHierarchyList(DateUtils.toLocalDate(effectiveAsOfDate), codeHierarchyName, getFactoryContext());
	
				GenericEntity<CodeHierarchyListRsrc> entity = new GenericEntity<CodeHierarchyListRsrc>(
						results) {
					/* do nothing */
				};
	
				response = Response.ok(entity).tag(results.getUnquotedETag()).build();
			}
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		logger.debug(">getCodeHierarchyList " + response.getStatus());
		return response;
	}
}
