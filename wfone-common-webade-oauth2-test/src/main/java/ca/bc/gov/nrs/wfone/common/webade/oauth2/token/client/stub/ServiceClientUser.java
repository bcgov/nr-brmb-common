package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

public class ServiceClientUser extends User {

	@Override
	public String getUserType() {
		return "SCL";
	}

	@Override
	public String getAccountType() {
		return "ServiceClient";
	}
}
