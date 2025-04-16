package ca.bc.gov.nrs.wfone.common.api.rest.code.resource.factory;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import ca.bc.gov.nrs.common.wfone.rest.resource.CodeRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.CodeTableListRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.CodeTableRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.RelLink;
import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.CodeTableEndpoints;
import ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.CodeTableListEndpoints;
import ca.bc.gov.nrs.wfone.common.model.Code;
import ca.bc.gov.nrs.wfone.common.model.CodeTable;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.resource.factory.BaseResourceFactory;
import ca.bc.gov.nrs.wfone.common.service.api.code.model.factory.CodeTableFactory;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryException;

public class CodeTableResourceFactory extends BaseResourceFactory implements CodeTableFactory {

	@Override
	public CodeTableListRsrc getCodeTables(LocalDate effectiveAsOfDate, String codeTableName, List<CodeTableDto> dtos, FactoryContext context)
			throws FactoryException {
		
		URI baseUri = getBaseURI(context);
		
		CodeTableListRsrc result = null;
		
		List<CodeTableRsrc> resources = new ArrayList<CodeTableRsrc>();
		
		for (CodeTableDto dto : dtos) {
			
			CodeTableRsrc resource = populate(dto, baseUri);
			setSelfLink(resource, effectiveAsOfDate, baseUri);
			resources.add(resource);
		}
		
		result = new CodeTableListRsrc();
		result.setCodeTableList(resources);
		
		String eTag = getEtag(result);
		result.setETag(eTag);		
		
		setSelfLink(effectiveAsOfDate, codeTableName, result, baseUri);
		
		setLinks(result, baseUri);
		
		return result;
	}

	@Override
	public CodeTable<? extends Code> getCodeTable(CodeTableDto dto, LocalDate effectiveAsOfDate, boolean canUpdate, FactoryContext context)
			throws FactoryException {
		
		URI baseUri = getBaseURI(context);
		
		CodeTableRsrc result = populate(dto, baseUri);
		
		String eTag = getEtag(result);
		result.setETag(eTag);		
		
		setSelfLink(result, effectiveAsOfDate, baseUri);
		
		setLinks(result, effectiveAsOfDate, canUpdate, baseUri);
		
		return result;
	}

	private void setLinks(CodeTableListRsrc resource, URI baseUri) {
		// do nothing
	}

	private static void setLinks(CodeTableRsrc resource, LocalDate effectiveAsOfDate, boolean canUpdate, URI baseUri) {

		if(canUpdate){
			
			String result = UriBuilder
					.fromUri(baseUri)
					.path(CodeTableEndpoints.class)
					.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
					.build(resource.getCodeTableName()).toString();
			resource.getLinks().add(
					new RelLink(BaseResourceTypes.UPDATE_CODE_TABLE,
							result, "PUT"));
		}
	}

	public static String getCodeTablesSelfUri(LocalDate effectiveAsOfDate, String codeTableName, URI baseUri) {

		String result = UriBuilder.fromUri(baseUri)
		.path(CodeTableListEndpoints.class)
		.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
		.queryParam("codeTableName", nvl(codeTableName, ""))
		.build().toString();

		return result;
	}

	public static String getCodeTablesSelfUri(String codeTableName, URI baseUri) {

		return getCodeTablesSelfUri(null, codeTableName, baseUri);
	}

	public static void setSelfLink(LocalDate effectiveAsOfDate, String codeTableName, CodeTableListRsrc resource, URI baseUri) {
		
		String selfUri = getCodeTablesSelfUri(effectiveAsOfDate, codeTableName, baseUri);
		
		resource.getLinks().add(new RelLink(BaseResourceTypes.SELF, selfUri, "GET"));

	}

	public static String getCodeTableSelfUri(String codeTableName, LocalDate effectiveAsOfDate, URI baseUri) {

		String result = UriBuilder.fromUri(baseUri)
		.path(CodeTableEndpoints.class)
		.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
		.build(codeTableName).toString();

		return result;
	}

	public static void setSelfLink(CodeTableRsrc resource, LocalDate effectiveAsOfDate, URI baseUri) {
		
		String selfUri = getCodeTableSelfUri(resource.getCodeTableName(), effectiveAsOfDate, baseUri);
		
		resource.getLinks().add(new RelLink(BaseResourceTypes.SELF, selfUri, "GET"));

	}

	static CodeTableRsrc populate(CodeTableDto dto, URI baseUri) throws FactoryException {

		CodeTableRsrc result = new CodeTableRsrc();
		
		result.setCodeTableName(dto.getCodeTableName());
		
		List<CodeRsrc> codes = new ArrayList<>();
		for(CodeDto codeDto:dto.getCodes()) {
			codes.add(populate(codeDto));
		}
		
		result.setCodes(codes);

		return result;
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
