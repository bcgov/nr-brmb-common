package ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.stub;

public abstract class User {

	private String accountName;
	private String sourceDirectory;
	private String guid;


	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getUserId() {
		return (this.sourceDirectory+"\\"+this.accountName).toUpperCase();
	}

	public abstract String getAccountType();

	public abstract String getUserType();

}
