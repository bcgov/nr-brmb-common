package ca.bc.gov.brmb.common.service.api.code.validation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.persistence.code.dao.CodeTableConfig;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeTableDao;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeDto;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;
import ca.bc.gov.brmb.common.utils.ApplicationContextProvider;

public class CodeValueValidator implements
		ConstraintValidator<CodeValue, String> {

	private static final Logger logger = LoggerFactory
			.getLogger(CodeValueValidator.class);

	private String codeTableName;
	
	@Override
	public void initialize(CodeValue annotation) {

		this.codeTableName = annotation.codeTableName();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		logger.debug("<isValid");

		boolean result = true;

		if (value != null && value.trim().length()>0) {

			CodeTableDao codeTableDao = (CodeTableDao) ApplicationContextProvider.getBean("codeTableDao");
			
			try {
				
				CodeTableConfig codeTableConfig = new CodeTableConfig();
				codeTableConfig.setCodeTableName(this.codeTableName);
				
				CodeTableDto codeTable = codeTableDao.fetch(codeTableConfig, LocalDate.now());
				
				boolean found = false;
				for(CodeDto dto:codeTable.getCodes()) {
					if(dto.getCode().equals(value)) {
						
						found = true;
						break;
					}
				}
				
				result = found;

			} catch (DaoException e) {
				throw new RuntimeException(e);
			}

		}

		logger.debug(">isValid " + result);
		return result;
	}
}
