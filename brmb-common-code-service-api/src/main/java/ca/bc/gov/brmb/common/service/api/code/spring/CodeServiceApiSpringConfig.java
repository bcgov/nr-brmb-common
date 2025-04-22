package ca.bc.gov.brmb.common.service.api.code.spring;

import java.util.List;

import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyConfig;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeHierarchyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.bc.gov.brmb.common.persistence.code.dao.CodeTableConfig;
import ca.bc.gov.brmb.common.service.api.code.impl.CodeServiceImpl;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeTableFactory;
import ca.bc.gov.brmb.common.service.api.code.validation.CodeValidator;

@Configuration
public class CodeServiceApiSpringConfig {

	// Beans provided by EndpointsSpringConfig
	@Autowired ResourceBundleMessageSource messageSource;
	@Autowired CodeTableFactory codeTableFactory;
	@Autowired CodeHierarchyFactory codeHierarchyFactory;
	
	// Beans provided by CodeTableSpringConfig
	@Autowired List<CodeTableConfig> codeTableConfigs;

	// Beans provided by CodeHierarchySpringConfig
	@Autowired List<CodeHierarchyConfig> codeHierarchyConfigs;

	@Bean
	public CodeValidator codeValidator() {
		CodeValidator result;
		
		result = new CodeValidator();
		result.setMessageSource(messageSource);
		
		return result;
	}

	@Bean
	public CodeServiceImpl codeService() {
		CodeServiceImpl result;
		
		result = new CodeServiceImpl();
		result.setCodeValidator(codeValidator());
		result.setCodeTableFactory(codeTableFactory);
		result.setCodeTableConfigs(codeTableConfigs);
		result.setCodeHierarchyFactory(codeHierarchyFactory);
		result.setCodeHierarchyConfigs(codeHierarchyConfigs);
		
		return result;
	}

}
