package ca.bc.gov.nrs.wfone.common.service.api.code.validation;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeHierarchy;
import ca.bc.gov.brmb.common.model.CodeTable;
import ca.bc.gov.brmb.common.model.Hierarchy;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.service.api.validation.BaseValidator;

public class CodeValidator extends BaseValidator {
	
	private static final Logger logger = LoggerFactory
			.getLogger(CodeValidator.class);

	public Collection<? extends Message> validateUpdateCodeTable(
			CodeTable<? extends Code> codeTable) {
		logger.debug("<validateUpdateCodeTable");
		
		List<Message> results = this.validate(codeTable, new Class<?>[] { CodeTableConstraints.class });
		
		int index = 0;
		for (Code code : codeTable.getCodes()) {
			List<Message> codeMessages = this.validate(code, new Class<?>[] { CodeConstraints.class });
			
			//Pass validation process if there are no validation messages for the feeItem.
			if (codeMessages == null) {
				index++;
				continue;
			}
			//Add the code index to the validation message path.
			for (Message codeMsg: codeMessages) {
				String path = codeMsg.getPath();
				path = (path == null) ? "" : path;
				path = "codes[" + index + "]." + path;
				codeMsg.setPath(path);
			}
			index++;
			results.addAll(codeMessages);
		}

		logger.debug(">validateUpdateCodeTable");
		return results;
	}

	public Collection<? extends Message> validateUpdateCodeHierarchy(
			CodeHierarchy codeHierarchy) {
		logger.debug("<validateUpdateCodeHierarchy");
		
		List<Message> results = this.validate(codeHierarchy, new Class<?>[] { CodeHierarchyConstraints.class });
		
		int index = 0;
		for (Hierarchy hierarchy : codeHierarchy.getHierarchy()) {
			List<Message> hierarchyMessages = this.validate(hierarchy, new Class<?>[] { HierarchyConstraints.class });
			
			//Pass validation process if there are no validation messages for the feeItem.
			if (hierarchyMessages == null) {
				index++;
				continue;
			}
			//Add the code index to the validation message path.
			for (Message hierarchyMsg: hierarchyMessages) {
				String path = hierarchyMsg.getPath();
				path = (path == null) ? "" : path;
				path = "hierarchy[" + index + "]." + path;
				hierarchyMsg.setPath(path);
			}
			index++;
			results.addAll(hierarchyMessages);
		}

		logger.debug(">validateUpdateCodeHierarchy");
		return results;
	}

}
