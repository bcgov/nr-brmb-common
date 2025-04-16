package ca.bc.gov.nrs.wfone.common.rest.client;

import java.util.ArrayList;
import java.util.List;

import ca.bc.gov.brmb.common.model.Message;

public class BadRequestException extends ClientErrorException {

	private static final long serialVersionUID = 1L;
	
	private List<Message> messages = new ArrayList<>();
	
	public BadRequestException(List<? extends Message> messages) {
		super(400, messages, "Unkown cause for Bad Request.");
		
		if(messages!=null) {
			
			for(Message message:messages) {
				
				this.messages.add(message);
			}
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

}
