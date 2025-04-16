package ca.bc.gov.brmb.common.model;

import java.io.Serializable;
import java.time.LocalDate;

public interface Code extends Serializable {
	
	public String getCode();
	public void setCode(String code);

	public String getDescription();
	public void setDescription(String description);

	public Integer getDisplayOrder();
	public void setDisplayOrder(Integer displayOrder);

	public LocalDate getEffectiveDate();
	public void setEffectiveDate(LocalDate effectiveDate);

	public LocalDate getExpiryDate();
	public void setExpiryDate(LocalDate expiryDate);
}
