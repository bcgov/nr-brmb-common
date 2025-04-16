package ca.bc.gov.nrs.wfone.common.api.rest.code.resource.factory;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import ca.bc.gov.nrs.common.wfone.rest.resource.CodeHierarchyListRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.CodeHierarchyRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.RelLink;
import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.CodeHierarchyEndpoints;
import ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.CodeHierarchyListEndpoints;
import ca.bc.gov.nrs.wfone.common.model.CodeHierarchy;
import ca.bc.gov.nrs.wfone.common.model.HierarchyImpl;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.HierarchyDto;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.resource.factory.BaseResourceFactory;
import ca.bc.gov.nrs.wfone.common.service.api.code.model.factory.CodeHierarchyFactory;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryException;

public class CodeHierarchyResourceFactory extends BaseResourceFactory implements CodeHierarchyFactory {

	@Override
	public CodeHierarchyListRsrc getCodeHierarchys(LocalDate effectiveAsOfDate, String codeHierarchyName, List<CodeHierarchyDto> dtos, FactoryContext context)
			throws FactoryException {
		
		URI baseUri = getBaseURI(context);
		
		CodeHierarchyListRsrc result = null;
		
		List<CodeHierarchyRsrc> resources = new ArrayList<CodeHierarchyRsrc>();
		
		for (CodeHierarchyDto dto : dtos) {
			
			CodeHierarchyRsrc resource = populate(dto, baseUri);
			setSelfLink(resource, effectiveAsOfDate, baseUri);
			resources.add(resource);
		}
		
		result = new CodeHierarchyListRsrc();
		result.setCodeHierarchyList(resources);
		
		String eTag = getEtag(result);
		result.setETag(eTag);		
		
		setSelfLink(effectiveAsOfDate, codeHierarchyName, result, baseUri);
		
		setLinks(result, baseUri);
		
		return result;
	}

	@Override
	public CodeHierarchy getCodeHierarchy(CodeHierarchyDto dto, LocalDate effectiveAsOfDate, boolean canUpdate, FactoryContext context)
			throws FactoryException {
		
		URI baseUri = getBaseURI(context);
		
		CodeHierarchyRsrc result = populate(dto, baseUri);
		
		String eTag = getEtag(result);
		result.setETag(eTag);		
		
		setSelfLink(result, effectiveAsOfDate, baseUri);
		
		setLinks(result, effectiveAsOfDate, canUpdate, baseUri);
		
		return result;
	}

	private void setLinks(CodeHierarchyListRsrc resource, URI baseUri) {
		// do nothing
	}

	private static void setLinks(CodeHierarchyRsrc resource, LocalDate effectiveAsOfDate, boolean canUpdate, URI baseUri) {

		if(canUpdate){
			
			String result = UriBuilder
					.fromUri(baseUri)
					.path(CodeHierarchyEndpoints.class)
					.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
					.build(resource.getCodeHierarchyName()).toString();
			resource.getLinks().add(
					new RelLink(BaseResourceTypes.UPDATE_CODE_HIERARCHY,
							result, "PUT"));
		}
	}

	public static String getCodeHierarchyListSelfUri(LocalDate effectiveAsOfDate, String codeHierarchyName, URI baseUri) {

		String result = UriBuilder.fromUri(baseUri)
		.path(CodeHierarchyListEndpoints.class)
		.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
		.queryParam("codeHierarchyName", nvl(codeHierarchyName, ""))
		.build().toString();

		return result;
	}

	public static String getCodeHierarchyListSelfUri(String codeHierarchyName, URI baseUri) {

		return getCodeHierarchyListSelfUri(null, codeHierarchyName, baseUri);
	}

	public static void setSelfLink(LocalDate effectiveAsOfDate, String codeHierarchyName, CodeHierarchyListRsrc resource, URI baseUri) {
		
		String selfUri = getCodeHierarchyListSelfUri(effectiveAsOfDate, codeHierarchyName, baseUri);
		
		resource.getLinks().add(new RelLink(BaseResourceTypes.SELF, selfUri, "GET"));

	}

	public static String getCodeHierarchySelfUri(String codeHierarchyName, LocalDate effectiveAsOfDate, URI baseUri) {

		String result = UriBuilder.fromUri(baseUri)
		.path(CodeHierarchyEndpoints.class)
		.queryParam("effectiveAsOfDate", nvl(toString(effectiveAsOfDate), ""))
		.build(codeHierarchyName).toString();

		return result;
	}

	public static void setSelfLink(CodeHierarchyRsrc resource, LocalDate effectiveAsOfDate, URI baseUri) {
		
		String selfUri = getCodeHierarchySelfUri(resource.getCodeHierarchyName(), effectiveAsOfDate, baseUri);
		
		resource.getLinks().add(new RelLink(BaseResourceTypes.SELF, selfUri, "GET"));

	}

	static CodeHierarchyRsrc populate(CodeHierarchyDto dto, URI baseUri) throws FactoryException {

		CodeHierarchyRsrc result = new CodeHierarchyRsrc();
		
		result.setCodeHierarchyName(dto.getCodeHierarchyName());
		result.setUpperCodeTableName(dto.getUpperCodeTableName());
		result.setLowerCodeTableName(dto.getLowerCodeTableName());
		
		List<HierarchyImpl> hierarchy = new ArrayList<HierarchyImpl>();
		for(HierarchyDto hierarchyDto:dto.getHierarchy()) {
			hierarchy.add(populate(hierarchyDto));
		}
		
		result.setHierarchy(hierarchy);

		return result;
	}
	
	static HierarchyImpl populate(HierarchyDto dto) {

		HierarchyImpl result = new HierarchyImpl();

		result.setLowerCode(dto.getLowerCode());
		result.setUpperCode(dto.getUpperCode());
		result.setEffectiveDate(dto.getEffectiveDate());
		result.setExpiryDate(dto.getExpiryDate());
		
		return result;
	}

}
