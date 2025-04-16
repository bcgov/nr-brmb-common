package ca.bc.gov.webade.oauth2.rest.test.client.impl;

class Cookie {

	private String name;
	private String value;
	
	Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object object) {
		boolean result = false;
		
		if(object instanceof Cookie) {
			result = name.equals(((Cookie) object).name);
		}

		return result;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
