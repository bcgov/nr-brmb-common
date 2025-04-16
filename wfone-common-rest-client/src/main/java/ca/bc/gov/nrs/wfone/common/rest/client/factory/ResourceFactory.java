package ca.bc.gov.nrs.wfone.common.rest.client.factory;

import ca.bc.gov.brmb.common.rest.resource.transformers.Transformer;
import ca.bc.gov.brmb.common.rest.resource.transformers.TransformerException;

public interface ResourceFactory<T> {

	T getResource(Transformer transformer, byte[] body, Class<T> clazz, String eTag, Long cacheExpiresMillis) throws TransformerException;

}
