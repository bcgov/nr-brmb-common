package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "http://www.webade.org/webade-xml-user-info", name = "bc-services-card-user-info")
public class BcServicesCardUserInfo extends UserInfo {

	private String sex;
	private String transactionIdentifier;
	private String identityAssuranceLevel;

	@XmlAttribute
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@XmlAttribute
	public String getTransactionIdentifier() {
		return transactionIdentifier;
	}

	public void setTransactionIdentifier(String transactionIdentifier) {
		this.transactionIdentifier = transactionIdentifier;
	}

	@XmlAttribute
	public String getIdentityAssuranceLevel() {
		return identityAssuranceLevel;
	}

	public void setIdentityAssuranceLevel(String identityAssuranceLevel) {
		this.identityAssuranceLevel = identityAssuranceLevel;
	}
	
	@Override
	@XmlTransient
	public String getUserType() {
		return "BCS";
	}

	@Override
	@XmlTransient
	public String getAccountType() {
		return "VerifiedIndividual";
	}

	@Override
	public String getUserId() {
		return ("BCS\\"+this.getGuid()).toUpperCase();
	}
}
