package ca.bc.gov.nrs.wfone.common.service.api;

public class NotFoundException extends Exception {
	
	private static final long serialVersionUID = -2815494869807318386L;
	
	public NotFoundException(String msg) {
		super(msg);
	}

	public <T> NotFoundException(Class<T> clazz, String id) {
		super(clazz.getName() + "[" + id + "]");
	}
}
