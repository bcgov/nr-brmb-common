package ca.bc.gov.nrs.common.wfone.rest.resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.RELLINK_NAME)
@JsonSubTypes({ @Type(value = RelLink.class, name = BaseResourceTypes.RELLINK) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class RelLink extends TypedResource {
	private static final long serialVersionUID = 6390172437806209818L;

	private String rel;
	private String href;
	private String method;

	public RelLink()
	{
		super();
	}

	public RelLink(String rel, String href, String method)
	{
		this.rel = rel;
		this.href = href;
		this.method = method;
	}

	@XmlAttribute
	public String getRel()
	{
		return rel;
	}

	public void setRel(String rel)
	{
		this.rel = rel;
	}

	@XmlAttribute
	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	@XmlAttribute
	public String getMethod()
	{
		return method;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}
}
