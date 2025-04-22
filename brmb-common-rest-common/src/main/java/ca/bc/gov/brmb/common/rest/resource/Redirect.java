package ca.bc.gov.brmb.common.rest.resource;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.REDIRECT_NAME)
@JsonSubTypes({ @Type(value = Redirect.class, name = BaseResourceTypes.REDIRECT) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Redirect extends BaseResource {
	private static final long serialVersionUID = 6390172437806209818L;

	public Redirect() {
		super();
	}
}
