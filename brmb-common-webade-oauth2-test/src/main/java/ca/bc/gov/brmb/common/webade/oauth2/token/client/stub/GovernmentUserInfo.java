package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "http://www.webade.org/webade-xml-user-info", name = "government-user-info")
public class GovernmentUserInfo extends UserInfo {

	private String company;
	private String description;
	private String employeeId;
	private String governmentDepartment;
	private String office;
	private String organizationCode;
	private String title;

	@XmlAttribute
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlAttribute
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	@XmlAttribute
	public String getGovernmentDepartment() {
		return governmentDepartment;
	}

	public void setGovernmentDepartment(String governmentDepartment) {
		this.governmentDepartment = governmentDepartment;
	}

	@XmlAttribute
	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	@XmlAttribute
	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	@XmlTransient
	public String getUserType() {
		return "GOV";
	}

	@Override
	@XmlTransient
	public String getAccountType() {
		return "Internal";
	}
}
