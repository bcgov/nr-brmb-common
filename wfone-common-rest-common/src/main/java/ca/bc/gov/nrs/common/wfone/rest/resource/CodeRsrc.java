package ca.bc.gov.nrs.common.wfone.rest.resource;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.LocalDateJAXBAdapter;
import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.LocalDateJacksonDeserializer;
import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.LocalDateJacksonSerializer;
import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.nrs.wfone.common.model.Code;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.CODE_NAME)
@JsonSubTypes({ @Type(value = CodeRsrc.class, name = BaseResourceTypes.CODE) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class CodeRsrc implements Code {
	private static final long serialVersionUID = 1L;

	private String code;

	private String description;

	private Integer displayOrder;

	private LocalDate effectiveDate;

	private LocalDate expiryDate;

	public CodeRsrc() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	@JsonSerialize(using = LocalDateJacksonSerializer.class)
	@JsonDeserialize(using = LocalDateJacksonDeserializer.class)
	@XmlJavaTypeAdapter(LocalDateJAXBAdapter.class)
	@Schema(type="string")
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	@JsonSerialize(using = LocalDateJacksonSerializer.class)
	@JsonDeserialize(using = LocalDateJacksonDeserializer.class)
	@XmlJavaTypeAdapter(LocalDateJAXBAdapter.class)
	@Schema(type="string")
	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
}