package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.ArrayList;
import java.util.List;

import ca.bc.gov.nrs.wfone.common.model.Message;
import ca.bc.gov.nrs.wfone.common.model.MessageImpl;

public class ServerErrorException extends RestDAOException {

	private static final long serialVersionUID = 1L;
	
	private int code;
	
	private List<Message> messages = new ArrayList<>();
	
	public ServerErrorException(int code, String msg) {
		super(msg);
		this.code = code;
		Message message = new MessageImpl(msg);
		this.messages.add(message);
	}
	
	public ServerErrorException(int code, List<? extends Message> messages) {
		super(messages, "Unkown cause for Server Error.");
		
		if(messages!=null) {
			
			for(Message message:messages) {
				
				this.messages.add(message);
			}
		}
		
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public List<Message> getMessages() {
		return messages;
	}

}
