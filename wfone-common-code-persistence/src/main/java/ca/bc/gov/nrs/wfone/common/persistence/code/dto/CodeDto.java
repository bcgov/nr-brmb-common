package ca.bc.gov.nrs.wfone.common.persistence.code.dto;

import java.time.LocalDate;

public class CodeDto extends EffectiveDto {

	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private Integer displayOrder;
	private LocalDate expiryDate;

	public CodeDto() {
		// do nothing
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

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Override
	public boolean equals(Object other) {
		boolean result = false;
		
		if(other instanceof CodeDto) {
			
			if(((CodeDto)other).code.equals(code)) {
				result = true;
			}
		}

		return result;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		
		if(this.code!=null) {
			result = this.code.hashCode();
		}
		
		return result;
	}
	
}
