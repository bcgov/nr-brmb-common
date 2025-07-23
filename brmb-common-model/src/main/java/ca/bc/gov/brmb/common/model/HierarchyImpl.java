package ca.bc.gov.brmb.common.model;

import java.time.LocalDate;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ca.bc.gov.brmb.common.model.transformers.LocalDateJAXBAdapter;
import ca.bc.gov.brmb.common.model.transformers.LocalDateJacksonDeserializer;
import ca.bc.gov.brmb.common.model.transformers.LocalDateJacksonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(namespace = HierarchyImpl.COMMON_NAMESPACE, name = HierarchyImpl.HIERARCHY_NAME)
@JsonSubTypes({ @Type(value =  HierarchyImpl.class, name = HierarchyImpl.HIERARCHY) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class HierarchyImpl implements Hierarchy {

	private static final long serialVersionUID = 1L;
	
	public static final String COMMON_NAMESPACE = "http://common.brmb.gov.bc.ca/v1/";	

	public static final String HIERARCHY_NAME = "Hierarchy";
	public static final String HIERARCHY = COMMON_NAMESPACE + HIERARCHY_NAME;
	
	
	private String lowerCode;
	private String upperCode;
	private LocalDate effectiveDate;
	private LocalDate expiryDate;

	public HierarchyImpl() {
		// do nothing
	}

	public String getLowerCode() {
		return lowerCode;
	}

	public void setLowerCode(String lowerCode) {
		this.lowerCode = lowerCode;
	}

	public String getUpperCode() {
		return upperCode;
	}

	public void setUpperCode(String upperCode) {
		this.upperCode = upperCode;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lowerCode == null) ? 0 : lowerCode.hashCode());
		result = prime * result
				+ ((upperCode == null) ? 0 : upperCode.hashCode());
		result = prime * result
				+ ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		result = prime * result
				+ ((expiryDate == null) ? 0 : expiryDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HierarchyImpl other = (HierarchyImpl) obj;
		if (lowerCode == null) {
			if (other.lowerCode != null)
				return false;
		} else if (!lowerCode.equals(other.lowerCode))
			return false;
		if (upperCode == null) {
			if (other.upperCode != null)
				return false;
		} else if (!upperCode.equals(other.upperCode))
			return false;
		if (effectiveDate == null) {
			if (other.effectiveDate != null)
				return false;
		} else if (!effectiveDate.equals(other.effectiveDate))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		return true;
	}
	
	
}
