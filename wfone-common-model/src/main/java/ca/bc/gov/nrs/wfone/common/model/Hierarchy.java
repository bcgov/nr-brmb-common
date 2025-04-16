package ca.bc.gov.nrs.wfone.common.model;

import java.io.Serializable;
import java.time.LocalDate;

public interface Hierarchy extends Serializable {

	public String getLowerCode();
	public void setLowerCode(String lowerCode);

	public String getUpperCode();
	public void setUpperCode(String upperCode);

	public LocalDate getEffectiveDate();
	public void setEffectiveDate(LocalDate effectiveDate);

	public LocalDate getExpiryDate();
	public void setExpiryDate(LocalDate expiryDate);
}
