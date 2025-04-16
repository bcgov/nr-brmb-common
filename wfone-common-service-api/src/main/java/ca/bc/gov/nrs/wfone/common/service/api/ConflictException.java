package ca.bc.gov.nrs.wfone.common.service.api;

public class ConflictException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public ConflictException(String message) {
        super(message);
    }
}
