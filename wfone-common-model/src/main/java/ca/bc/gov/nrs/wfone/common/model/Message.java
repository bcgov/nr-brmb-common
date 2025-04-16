package ca.bc.gov.nrs.wfone.common.model;

import java.io.Serializable;

public interface Message extends Serializable {

	public String getPath();
	public void setPath(String path);

	public String getMessage();
	public void setMessage(String message);

	public String getMessageTemplate();
	public void setMessageTemplate(String messageTemplate);

	public String[] getMessageArguments();
	public void setMessageArguments(String[] messageArguments);
}