package ca.bc.gov.nrs.wfone.common.rest.client.factory;

import ca.bc.gov.nrs.common.wfone.rest.resource.MessageListRsrc;
import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.Transformer;

public interface MessageListFactory {

	MessageListRsrc getMessageList(Transformer transformer, byte[] body);

}
