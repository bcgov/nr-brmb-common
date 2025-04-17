package ca.bc.gov.brmb.common.rest.client;

import java.util.List;

import ca.bc.gov.brmb.common.model.Message;

public class RestDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public RestDAOException(String message) {
		super(message);
	}

	public RestDAOException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestDAOException(Throwable cause) {
		super(cause);
	}

	public RestDAOException(List<? extends Message> messages, String alternateMesage) {
		super((messages!=null&&!messages.isEmpty())?messages.get(0).getMessage():alternateMesage);
	}

}
