package ca.bc.gov.nrs.common.wfone.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.nrs.common.wfone.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.model.MessageList;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.MESSAGE_LIST_NAME)
@JsonSubTypes({ @Type(value = MessageListRsrc.class, name = BaseResourceTypes.MESSAGE_LIST) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MessageListRsrc extends TypedResource implements MessageList<MessageRsrc> {

	private static final long serialVersionUID = 1L;
	
	private List<MessageRsrc> messages = new ArrayList<MessageRsrc>();

	public MessageListRsrc() {

	}

	public MessageListRsrc(List<Message> messages) {

		if(messages!=null) {
			
			for(Message message:messages) {
				
				this.messages.add(new MessageRsrc(message));
			}
		}
	}

	public MessageListRsrc(Message message) {

		if(message!=null) {
			
			this.messages.add(new MessageRsrc(message));
		}
	}

	public MessageListRsrc(String message) {
		this(new MessageRsrc(message));
	}

	@Override
	public List<MessageRsrc> getMessages() {
		return messages;
	}

	@Override
	public void setMessages(List<MessageRsrc> messages) {
		this.messages = messages;
	}

	public boolean hasMessages() {
		boolean result = false;
		
		if(messages!=null) {
			
			result = messages.size()>0;
		}
		
		return result;
	}
}