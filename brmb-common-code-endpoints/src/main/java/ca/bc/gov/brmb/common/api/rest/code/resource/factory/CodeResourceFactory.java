package ca.bc.gov.brmb.common.api.rest.code.resource.factory;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import ca.bc.gov.brmb.common.rest.resource.CodeRsrc;
import ca.bc.gov.brmb.common.rest.resource.RelLink;
import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.api.rest.code.endpoints.CodeEndpoints;
import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeDto;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.rest.endpoints.resource.factory.BaseResourceFactory;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeFactory;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryException;

public class CodeResourceFactory extends BaseResourceFactory implements CodeFactory {

    private static final String UPDATE_CODE = BaseResourceTypes.COMMON_NAMESPACE + "updateCode";

    @Override
    public Code getCode(CodeTableDto codeTableDto, CodeDto codeDto, FactoryContext context) throws FactoryException {

        URI baseUri = getBaseURI(context);

        CodeRsrc result = populate(codeDto);

        String eTag = getEtag(result);
        result.setETag(eTag);

        setSelfLink(result, codeTableDto.getCodeTableName(), baseUri);

        setLinks(result, codeTableDto.getCodeTableName(), baseUri);

        return result;
    }

    private static void setLinks(CodeRsrc resource, String codeTableName, URI baseUri) {

        String result = UriBuilder
                .fromUri(baseUri)
                .path(CodeEndpoints.class)
                .build(codeTableName).toString();
        resource.getLinks().add(
                new RelLink(UPDATE_CODE,
                        result + "/" + resource.getCode(), "GET"));
    }

    public static String getCodeSelfUri(String codeTableName, String codeName, URI baseUri) {

        String result = UriBuilder.fromUri(baseUri)
                .path(CodeEndpoints.class)
                .build(codeTableName).toString();

        return result + "/" + codeName;
    }

    public static void setSelfLink(CodeRsrc resource, String codeTableName, URI baseUri) {

        String selfUri = getCodeSelfUri(codeTableName, resource.getCode(), baseUri);

        resource.getLinks().add(new RelLink(BaseResourceTypes.SELF, selfUri, "GET"));

    }

    static CodeRsrc populate(CodeDto dto) {
        CodeRsrc result = new CodeRsrc();

        result.setCode(dto.getCode());
        result.setDescription(dto.getDescription());
        result.setDisplayOrder(dto.getDisplayOrder());
        result.setEffectiveDate(dto.getEffectiveDate());
        result.setExpiryDate(dto.getExpiryDate());

        return result;
    }

}
