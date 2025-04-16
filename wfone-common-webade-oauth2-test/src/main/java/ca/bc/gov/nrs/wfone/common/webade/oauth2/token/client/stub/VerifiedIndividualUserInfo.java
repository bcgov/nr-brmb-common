package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "http://www.webade.org/webade-xml-user-info", name = "verified-individual-user-info")
public class VerifiedIndividualUserInfo extends UserInfo {

	@Override
	@XmlTransient
	public String getUserType() {
		return "VIN";
	}

	@Override
	@XmlTransient
	public String getAccountType() {
		return "VerifiedIndividual";
	}
}
