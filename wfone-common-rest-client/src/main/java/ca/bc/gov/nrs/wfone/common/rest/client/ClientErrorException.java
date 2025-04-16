package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.List;

import ca.bc.gov.nrs.wfone.common.model.Message;

public class ClientErrorException extends RestDAOException {

	private static final long serialVersionUID = 1L;

	private int code;
	
	public ClientErrorException(int code, String message) {
		super(message);
	}

	public ClientErrorException(int code, List<? extends Message> messages, String alternateMesage) {
		super(messages, alternateMesage);
	}

	public int getCode() {
		return code;
	}

}
