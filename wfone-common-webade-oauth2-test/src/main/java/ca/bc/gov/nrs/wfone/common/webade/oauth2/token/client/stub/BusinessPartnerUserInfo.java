package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "http://www.webade.org/webade-xml-user-info", name = "business-partner-user-info")
public class BusinessPartnerUserInfo extends UserInfo {

	private String businessAddressLine1;
	private String businessAddressLine2;
	private String businessAddressCity;
	private String businessAddressProvince;
	private String businessAddressCountry;
	private String businessAddressPostalCode;
	private String businessAddressUnstructured;
	private String businessGuid;
	private String businessLegalName;
	private String businessNumber;
	private Boolean businessNumberVerified;
	private String doingBusinessAsName;
	private Boolean suspended;
	private String businessType;
	private String statementOfRegistrationNumber;
	private String jurisdictionOfIncorporation;
	private String businessTypeOther;
	private String incorporationNumber;
	private String bnHubBusinessType;

	@XmlAttribute
	public String getBusinessAddressLine1() {
		return businessAddressLine1;
	}

	public void setBusinessAddressLine1(String businessAddressLine1) {
		this.businessAddressLine1 = businessAddressLine1;
	}

	@XmlAttribute
	public String getBusinessAddressLine2() {
		return businessAddressLine2;
	}

	public void setBusinessAddressLine2(String businessAddressLine2) {
		this.businessAddressLine2 = businessAddressLine2;
	}

	@XmlAttribute
	public String getBusinessAddressCity() {
		return businessAddressCity;
	}

	public void setBusinessAddressCity(String businessAddressCity) {
		this.businessAddressCity = businessAddressCity;
	}

	@XmlAttribute
	public String getBusinessAddressProvince() {
		return businessAddressProvince;
	}

	public void setBusinessAddressProvince(String businessAddressProvince) {
		this.businessAddressProvince = businessAddressProvince;
	}

	@XmlAttribute
	public String getBusinessAddressCountry() {
		return businessAddressCountry;
	}

	public void setBusinessAddressCountry(String businessAddressCountry) {
		this.businessAddressCountry = businessAddressCountry;
	}

	@XmlAttribute
	public String getBusinessAddressPostalCode() {
		return businessAddressPostalCode;
	}

	public void setBusinessAddressPostalCode(String businessAddressPostalCode) {
		this.businessAddressPostalCode = businessAddressPostalCode;
	}

	@XmlAttribute
	public String getBusinessAddressUnstructured() {
		return businessAddressUnstructured;
	}

	public void setBusinessAddressUnstructured(
			String businessAddressUnstructured) {
		this.businessAddressUnstructured = businessAddressUnstructured;
	}

	@XmlAttribute(name = "businessGUID")
	public String getBusinessGuid() {
		return businessGuid;
	}

	public void setBusinessGuid(String businessGuid) {
		this.businessGuid = businessGuid;
	}

	@XmlAttribute
	public String getBusinessLegalName() {
		return businessLegalName;
	}

	public void setBusinessLegalName(String businessLegalName) {
		this.businessLegalName = businessLegalName;
	}

	@XmlAttribute
	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	@XmlAttribute
	public Boolean getBusinessNumberVerified() {
		return businessNumberVerified;
	}

	public void setBusinessNumberVerified(Boolean businessNumberVerified) {
		this.businessNumberVerified = businessNumberVerified;
	}

	@XmlAttribute(name = "doingBusinessAs")
	public String getDoingBusinessAsName() {
		return doingBusinessAsName;
	}

	public void setDoingBusinessAsName(String doingBusinessAsName) {
		this.doingBusinessAsName = doingBusinessAsName;
	}

	@XmlAttribute
	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	@XmlAttribute
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@XmlAttribute
	public String getStatementOfRegistrationNumber() {
		return statementOfRegistrationNumber;
	}

	public void setStatementOfRegistrationNumber(
			String statementOfRegistrationNumber) {
		this.statementOfRegistrationNumber = statementOfRegistrationNumber;
	}

	@XmlAttribute
	public String getJurisdictionOfIncorporation() {
		return jurisdictionOfIncorporation;
	}

	public void setJurisdictionOfIncorporation(
			String jurisdictionOfIncorporation) {
		this.jurisdictionOfIncorporation = jurisdictionOfIncorporation;
	}

	@XmlAttribute
	public String getBusinessTypeOther() {
		return businessTypeOther;
	}

	public void setBusinessTypeOther(String businessTypeOther) {
		this.businessTypeOther = businessTypeOther;
	}

	@XmlAttribute
	public String getIncorporationNumber() {
		return incorporationNumber;
	}

	public void setIncorporationNumber(String incorporationNumber) {
		this.incorporationNumber = incorporationNumber;
	}

	@XmlAttribute
	public String getBnHubBusinessType() {
		return bnHubBusinessType;
	}

	public void setBnHubBusinessType(String bnHubBusinessType) {
		this.bnHubBusinessType = bnHubBusinessType;
	}

	@Override
	@XmlTransient
	public String getUserType() {
		return "BUP";
	}

	@Override
	@XmlTransient
	public String getAccountType() {
		return "Business";
	}
}
