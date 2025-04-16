package ca.bc.gov.brmb.common.service.api.code.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyConfig;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyDao;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.brmb.common.persistence.code.dto.HierarchyDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.wfone.common.utils.ApplicationContextProvider;

public class CodeHierarchyValidator {

	private static final Logger logger = LoggerFactory
			.getLogger(CodeHierarchyValidator.class);
	
	private List<CodeHierarchyConfig> codeHierarchyConfigs = new ArrayList<>();
	
	public CodeHierarchyValidator() {
		
	}

	public boolean isValid(String codeHierarchyTableName, String upperCode, String lowerCode) {
		logger.debug("<isValid");

		boolean result = true;
		
		CodeHierarchyConfig codeHierarchyConfig = null;
		for(CodeHierarchyConfig tmp:this.codeHierarchyConfigs) {
			
			if(tmp.getCodeHierarchyTableName().equals(codeHierarchyTableName)) {
				codeHierarchyConfig = tmp;
				break;
			}
		}
		
		if(codeHierarchyConfig == null) {
			throw new RuntimeException("Unsupported code hierarchy: "+codeHierarchyTableName);
		}

		if (upperCode != null && upperCode.trim().length()>0 && lowerCode != null && lowerCode.trim().length()>0 ) {

			CodeHierarchyDao codeHierarchyDao = (CodeHierarchyDao) ApplicationContextProvider.getBean("codeHierarchyDao");
			
			try {
				
				CodeHierarchyDto codeHierarchy = codeHierarchyDao.fetch(codeHierarchyConfig, LocalDate.now());
				
				boolean found = false;
				for(HierarchyDto dto:codeHierarchy.getHierarchy()) {
					if(dto.getUpperCode().equals(upperCode)&&dto.getLowerCode().equals(lowerCode)) {
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

	public void setCodeHierarchyConfigs(
			List<CodeHierarchyConfig> codeHierarchyConfigs) {
		this.codeHierarchyConfigs = codeHierarchyConfigs;
	}
}
