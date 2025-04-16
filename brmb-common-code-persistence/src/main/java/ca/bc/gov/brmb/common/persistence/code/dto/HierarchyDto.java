package ca.bc.gov.brmb.common.persistence.code.dto;

import java.time.LocalDate;

public class HierarchyDto extends EffectiveDto {

	private static final long serialVersionUID = 1L;

	private String skey;
	private String lowerCode;
	private String upperCode;
	private LocalDate expiryDate;

	public HierarchyDto() {
		// do nothing
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
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

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean result = false;
		
		if(other instanceof HierarchyDto) {
			
			if(((HierarchyDto)other).lowerCode.equals(lowerCode)&&((HierarchyDto)other).upperCode.equals(upperCode)) {
				result = true;
			}
		}

		return result;
	}
	
	@Override
	public int hashCode() {
		int result = (this.lowerCode+this.upperCode).hashCode();
		
		return result;
	}
	
}
