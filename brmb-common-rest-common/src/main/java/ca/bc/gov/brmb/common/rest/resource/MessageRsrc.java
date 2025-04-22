package ca.bc.gov.brmb.common.rest.resource;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ca.bc.gov.brmb.common.rest.resource.types.BaseResourceTypes;
import ca.bc.gov.brmb.common.model.Message;

@XmlRootElement(namespace = BaseResourceTypes.COMMON_NAMESPACE, name = BaseResourceTypes.MESSAGE_NAME)
@JsonSubTypes({ @Type(value = MessageRsrc.class, name = BaseResourceTypes.MESSAGE) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MessageRsrc implements Message {
	private static final long serialVersionUID = 1L;

	private String path;

	private String message;

	private String messageTemplate;

	private String[] messageArguments;

	public MessageRsrc() {
		super();
	}

	public MessageRsrc(String message) {
		this.message = message;
	}

	public MessageRsrc(String path, String message, String messageTemplate, String[] messageArguments) {
		this.path = path;
		this.message = message;
		this.messageTemplate = messageTemplate;
		this.messageArguments = messageArguments;
	}

	public MessageRsrc(Message message) {

		this(message.getPath(), message.getMessage(), message.getMessageTemplate(), message.getMessageArguments());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String[] getMessageArguments() {
		return messageArguments;
	}

	public void setMessageArguments(String[] messageArguments) {
		this.messageArguments = messageArguments;
	}
}