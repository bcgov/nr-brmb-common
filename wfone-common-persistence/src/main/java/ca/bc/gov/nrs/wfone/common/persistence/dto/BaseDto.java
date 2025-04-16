package ca.bc.gov.nrs.wfone.common.persistence.dto;

import java.io.Serializable;

import org.slf4j.Logger;

public abstract class BaseDto<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T _dirtyCopy;
	
	public final boolean isDirty() {
		boolean result = !this.equalsAll(_dirtyCopy);
		return result;
	}
	
	public final void resetDirty() {
		
		_dirtyCopy = copy();
	}
	
	public abstract T copy();
	
	public abstract Logger getLogger();
	
	public abstract boolean equalsBK(T other);
	
	public abstract boolean equalsAll(T other);
	
}
