package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.webade.org/webade-xml-user-info", name = "users")
public class UserInfoData {


	private List<UserInfo> userInfos;

	@XmlElementRef
	public List<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(List<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}
}
