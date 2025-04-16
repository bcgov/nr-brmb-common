package ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.CodeHierarchyQueryParameters;
import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.CodeTableQueryParameters;
import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.EffectiveAsOfParameter;
import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.CodeHierarchyQueryParametersConstraints;
import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.CodeTableQueryParametersConstraints;
import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.constraints.EffectiveAsOfParameterConstraints;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.nrs.wfone.common.service.api.validation.BaseValidator;

public class CodeParameterValidator extends BaseValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(CodeParameterValidator.class);

	public List<Message> validateEffectiveAsOfParameter(EffectiveAsOfParameter parameters) {
		logger.debug("<validateEffectiveAsOfParameter");
		
		List<Message> results = this.validate(parameters, EffectiveAsOfParameterConstraints.class);

		logger.debug(">validateEffectiveAsOfParameter " + results);
		return results;
	}

	public List<Message>  validateCodeTableQueryParameters(
			CodeTableQueryParameters parameters) {
		logger.debug("<validateCodeTableQueryParameters");
		
		List<Message> results = this.validate(parameters, CodeTableQueryParametersConstraints.class);

		logger.debug(">validateCodeTableQueryParameters " + results);
		return results;
	}

	public List<Message>  validateCodeHierarchyQueryParameters(
			CodeHierarchyQueryParameters parameters) {
		logger.debug("<validateCodeHierarchyQueryParameters");
		
		List<Message> results = this.validate(parameters, CodeHierarchyQueryParametersConstraints.class);

		logger.debug(">validateCodeHierarchyQueryParameters " + results);
		return results;
	}
}
