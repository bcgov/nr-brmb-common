package ca.bc.gov.nrs.wfone.common.service.api;

public class ForbiddenException extends Exception
{
	private static final long serialVersionUID = -2799482513146961120L;

	public ForbiddenException(Throwable t)
	{
		super(t);
	}

}
