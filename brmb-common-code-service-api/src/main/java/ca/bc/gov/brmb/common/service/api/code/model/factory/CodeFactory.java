package ca.bc.gov.brmb.common.service.api.code.model.factory;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeDto;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryException;

public interface CodeFactory {

    public Code getCode(CodeTableDto codeTableDto, CodeDto codeDto, FactoryContext context) throws FactoryException;
}
