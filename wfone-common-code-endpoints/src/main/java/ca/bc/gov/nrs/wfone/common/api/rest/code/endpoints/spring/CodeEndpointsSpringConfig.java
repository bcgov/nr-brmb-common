package ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.bc.gov.nrs.wfone.common.api.rest.code.parameters.validation.CodeParameterValidator;
import ca.bc.gov.nrs.wfone.common.api.rest.code.resource.factory.CodeHierarchyResourceFactory;
import ca.bc.gov.nrs.wfone.common.api.rest.code.resource.factory.CodeTableResourceFactory;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeHierarchyFactory;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeTableFactory;
import ca.bc.gov.brmb.common.service.api.code.spring.CodeServiceApiSpringConfig;

@Configuration
@Import({
	CodeServiceApiSpringConfig.class
})
public class CodeEndpointsSpringConfig {
	
	// Beans provided by EndpointsSpringConfig
	@Autowired ResourceBundleMessageSource messageSource;

	@Bean
	public CodeParameterValidator codeParameterValidator() {
		CodeParameterValidator result;
		
		result = new CodeParameterValidator();
		result.setMessageSource(messageSource);
		
		return result;
	}

	@Bean
	public CodeTableFactory codeTableFactory() {
		CodeTableResourceFactory result;
		
		result = new CodeTableResourceFactory();
		
		return result;
	}

	@Bean
	public CodeHierarchyFactory codeHierarchyFactory() {
		CodeHierarchyResourceFactory result;
		
		result = new CodeHierarchyResourceFactory();
		
		return result;
	}
}
