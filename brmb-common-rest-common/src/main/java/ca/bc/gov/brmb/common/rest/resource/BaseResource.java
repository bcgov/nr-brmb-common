package ca.bc.gov.brmb.common.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import io.swagger.v3.oas.annotations.Hidden;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class BaseResource extends TypedResource {

	private static final long serialVersionUID = 1L;

	private List<RelLink> links = new ArrayList<RelLink>();

	@Hidden
	@JsonIgnore
	@XmlTransient
	public String getSelfLink() {
		String result = null;
		for(RelLink link:getLinks()) {
			if(BaseResourceTypes.SELF.equals(link.getRel())) {
				result = link.getHref();
			}
		}
		return result;
	}

	public boolean hasLink(String resourceType) {
		boolean result = false;

		for (RelLink link : getLinks()) {
			if (resourceType.equals(link.getRel())) {
				result = true;
				break;
			}
		}

		return result;
	}

	public RelLink getLink(String resourceType) {
		RelLink result = null;

		for (RelLink link : getLinks()) {
			if (resourceType.equals(link.getRel())) {
				result = link;
				break;
			}
		}

		return result;
	}

	@XmlElementWrapper(name = "links")
	@XmlElement(name = "link")
	public List<RelLink> getLinks() {
		return links;
	}

	public void setLinks(List<RelLink> links) {
		this.links = links;
	}
}
