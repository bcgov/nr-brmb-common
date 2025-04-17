package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlSeeAlso({BusinessPartnerUserInfo.class, GovernmentUserInfo.class, IndividualUserInfo.class, VerifiedIndividualUserInfo.class})
public abstract class UserInfo extends User {

	private String displayName;
	private String firstName;
	private String surname;
	private String middleName;
	private String otherMiddleName;
	private String initials;
	private Date dateOfBirth;
	private String mailingAddressLine1;
	private String mailingAddressLine2;
	private String mailingAddressCity;
	private String mailingAddressProvince;
	private String mailingAddressCountry;
	private String mailingAddressPostalCode;
	private String mailingAddressUnstructured;
	private String residentialAddressLine1;
	private String residentialAddressLine2;
	private String residentialAddressCity;
	private String residentialAddressProvince;
	private String residentialAddressCountry;
	private String residentialAddressPostalCode;
	private String residentialAddressUnstructured;
	private String contactAddressLine1;
	private String contactAddressLine2;
	private String contactAddressCity;
	private String contactAddressProvince;
	private String contactAddressCountry;
	private String contactAddressPostalCode;
	private String contactAddressUnstructured;
	private String contactDepartment;
	private String contactEmail;
	private String contactPreferredName;
	private String contactTelephone;
	private String contactPreferenceType;

	@Override
	@XmlAttribute
	public String getAccountName() {
		return super.getAccountName();
	}

	@Override
	public void setAccountName(String accountName) {
		super.setAccountName(accountName);
	}

	@Override
	@XmlAttribute
	public String getSourceDirectory() {
		return super.getSourceDirectory();
	}

	@Override
	public void setSourceDirectory(String sourceDirectory) {
		super.setSourceDirectory(sourceDirectory);
	}

	@XmlAttribute
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	@XmlAttribute
	public String getGuid() {
		return super.getGuid();
	}

	@Override
	public void setGuid(String guid) {
		super.setGuid(guid);
	}

	@XmlAttribute
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlAttribute(name = "lastName")
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@XmlAttribute
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@XmlAttribute
	public String getOtherMiddleName() {
		return otherMiddleName;
	}

	public void setOtherMiddleName(String otherMiddleName) {
		this.otherMiddleName = otherMiddleName;
	}

	@XmlAttribute
	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	@XmlAttribute(name = "dateOfBirth")
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@XmlAttribute
	public String getMailingAddressLine1() {
		return mailingAddressLine1;
	}

	public void setMailingAddressLine1(String mailingAddressLine1) {
		this.mailingAddressLine1 = mailingAddressLine1;
	}

	@XmlAttribute
	public String getMailingAddressLine2() {
		return mailingAddressLine2;
	}

	public void setMailingAddressLine2(String mailingAddressLine2) {
		this.mailingAddressLine2 = mailingAddressLine2;
	}

	@XmlAttribute
	public String getMailingAddressCity() {
		return mailingAddressCity;
	}

	public void setMailingAddressCity(String mailingAddressCity) {
		this.mailingAddressCity = mailingAddressCity;
	}

	@XmlAttribute
	public String getMailingAddressProvince() {
		return mailingAddressProvince;
	}

	public void setMailingAddressProvince(String mailingAddressProvince) {
		this.mailingAddressProvince = mailingAddressProvince;
	}

	@XmlAttribute
	public String getMailingAddressCountry() {
		return mailingAddressCountry;
	}

	public void setMailingAddressCountry(String mailingAddressCountry) {
		this.mailingAddressCountry = mailingAddressCountry;
	}

	@XmlAttribute
	public String getMailingAddressPostalCode() {
		return mailingAddressPostalCode;
	}

	public void setMailingAddressPostalCode(String mailingAddressPostalCode) {
		this.mailingAddressPostalCode = mailingAddressPostalCode;
	}

	@XmlAttribute
	public String getMailingAddressUnstructured() {
		return mailingAddressUnstructured;
	}

	public void setMailingAddressUnstructured(String mailingAddressUnstructured) {
		this.mailingAddressUnstructured = mailingAddressUnstructured;
	}

	@XmlAttribute
	public String getResidentialAddressLine1() {
		return residentialAddressLine1;
	}

	public void setResidentialAddressLine1(String residentialAddressLine1) {
		this.residentialAddressLine1 = residentialAddressLine1;
	}

	@XmlAttribute
	public String getResidentialAddressLine2() {
		return residentialAddressLine2;
	}

	public void setResidentialAddressLine2(String residentialAddressLine2) {
		this.residentialAddressLine2 = residentialAddressLine2;
	}

	@XmlAttribute
	public String getResidentialAddressCity() {
		return residentialAddressCity;
	}

	public void setResidentialAddressCity(String residentialAddressCity) {
		this.residentialAddressCity = residentialAddressCity;
	}

	@XmlAttribute
	public String getResidentialAddressProvince() {
		return residentialAddressProvince;
	}

	public void setResidentialAddressProvince(String residentialAddressProvince) {
		this.residentialAddressProvince = residentialAddressProvince;
	}

	@XmlAttribute
	public String getResidentialAddressCountry() {
		return residentialAddressCountry;
	}

	public void setResidentialAddressCountry(String residentialAddressCountry) {
		this.residentialAddressCountry = residentialAddressCountry;
	}

	@XmlAttribute
	public String getResidentialAddressPostalCode() {
		return residentialAddressPostalCode;
	}

	public void setResidentialAddressPostalCode(
			String residentialAddressPostalCode) {
		this.residentialAddressPostalCode = residentialAddressPostalCode;
	}

	@XmlAttribute
	public String getResidentialAddressUnstructured() {
		return residentialAddressUnstructured;
	}

	public void setResidentialAddressUnstructured(
			String residentialAddressUnstructured) {
		this.residentialAddressUnstructured = residentialAddressUnstructured;
	}

	@XmlAttribute
	public String getContactAddressLine1() {
		return contactAddressLine1;
	}

	public void setContactAddressLine1(String contactAddressLine1) {
		this.contactAddressLine1 = contactAddressLine1;
	}

	@XmlAttribute
	public String getContactAddressLine2() {
		return contactAddressLine2;
	}

	public void setContactAddressLine2(String contactAddressLine2) {
		this.contactAddressLine2 = contactAddressLine2;
	}

	@XmlAttribute
	public String getContactAddressCity() {
		return contactAddressCity;
	}

	public void setContactAddressCity(String contactAddressCity) {
		this.contactAddressCity = contactAddressCity;
	}

	@XmlAttribute
	public String getContactAddressProvince() {
		return contactAddressProvince;
	}

	public void setContactAddressProvince(String contactAddressProvince) {
		this.contactAddressProvince = contactAddressProvince;
	}

	@XmlAttribute
	public String getContactAddressCountry() {
		return contactAddressCountry;
	}

	public void setContactAddressCountry(String contactAddressCountry) {
		this.contactAddressCountry = contactAddressCountry;
	}

	@XmlAttribute
	public String getContactAddressPostalCode() {
		return contactAddressPostalCode;
	}

	public void setContactAddressPostalCode(String contactAddressPostalCode) {
		this.contactAddressPostalCode = contactAddressPostalCode;
	}

	@XmlAttribute
	public String getContactAddressUnstructured() {
		return contactAddressUnstructured;
	}

	public void setContactAddressUnstructured(String contactAddressUnstructured) {
		this.contactAddressUnstructured = contactAddressUnstructured;
	}

	@XmlAttribute(name = "department")
	public String getContactDepartment() {
		return contactDepartment;
	}

	public void setContactDepartment(String contactDepartment) {
		this.contactDepartment = contactDepartment;
	}

	@XmlAttribute(name = "emailAddress")
	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	@XmlAttribute(name = "preferredName")
	public String getContactPreferredName() {
		return contactPreferredName;
	}

	public void setContactPreferredName(String contactPreferredName) {
		this.contactPreferredName = contactPreferredName;
	}

	@XmlAttribute(name = "phoneNumber")
	public String getContactTelephone() {
		return contactTelephone;
	}

	public void setContactTelephone(String contactTelephone) {
		this.contactTelephone = contactTelephone;
	}

	@XmlAttribute
	public String getContactPreferenceType() {
		return contactPreferenceType;
	}

	public void setContactPreferenceType(String contactPreferenceType) {
		this.contactPreferenceType = contactPreferenceType;
	}

	@Override
	public String getUserId() {
		return super.getUserId();
	}

	@Override
	@XmlTransient
	public abstract String getAccountType();

	@Override
	@XmlTransient
	public abstract String getUserType();

}
