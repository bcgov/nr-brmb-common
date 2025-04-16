package ca.bc.gov.nrs.wfone.common.rest.client.factory;

import ca.bc.gov.brmb.common.rest.resource.MessageListRsrc;
import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;

public interface MessageListFactory {

	MessageListRsrc getMessageList(Transformer transformer, byte[] body);

}
