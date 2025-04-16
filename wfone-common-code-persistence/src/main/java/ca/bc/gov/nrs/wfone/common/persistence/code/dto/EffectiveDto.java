package ca.bc.gov.nrs.wfone.common.persistence.code.dto;

import java.time.LocalDate;

public abstract class EffectiveDto extends AuditDto implements Comparable<EffectiveDto> {
	
	private static final long serialVersionUID = 1L;
	
	private LocalDate effectiveDate;

	public final LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public final void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public int compareTo(EffectiveDto o) {
		int result = 0;
		
		result = o.effectiveDate.compareTo(this.effectiveDate);
		
		return result;
	}
}
