package ca.bc.gov.nrs.wfone.common.rest.client.factory;

import ca.bc.gov.nrs.common.wfone.rest.resource.Redirect;
import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.Transformer;
import ca.bc.gov.nrs.common.wfone.rest.resource.transformers.TransformerException;

public interface RedirectFactory {

	Redirect getRedirect(Transformer transformer, byte[] body) throws TransformerException;

}
