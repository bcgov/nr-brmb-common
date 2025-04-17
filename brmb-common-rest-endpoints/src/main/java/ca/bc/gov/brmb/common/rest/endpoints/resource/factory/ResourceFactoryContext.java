package ca.bc.gov.brmb.common.rest.endpoints.resource.factory;

import java.net.URI;

import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;

public class ResourceFactoryContext implements FactoryContext {

	private URI baseURI;

	private int resourceDepth;

	public ResourceFactoryContext(URI baseURI) {
		this.baseURI = baseURI;
	}
	
	public ResourceFactoryContext(URI baseURI, int resourceDepth) {
		this(baseURI);

		this.resourceDepth = resourceDepth;
	}

	public URI getBaseURI() {
		return baseURI;
	}

	public int getResourceDepth() {
		return resourceDepth;
	}
}
