package ca.bc.gov.brmb.common.rest.client;

import java.util.List;

import ca.bc.gov.brmb.common.model.Message;

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
