package ca.bc.gov.nrs.wfone.common.model;

import java.io.Serializable;
import java.util.List;

public interface MessageList<M extends Message> extends Serializable {

	public List<M> getMessages();

	public void setMessages(List<M> messageList);
}
