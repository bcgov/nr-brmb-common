package ca.bc.gov.brmb.common.webade.oauth2.token.client.stub;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class UserInfoDataFactory {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoDataFactory.class);

	public static UserInfoData getUserInfoData(String userInfoFileLocation) {

		UserInfoData results = null;
		
		try {

			if (userInfoFileLocation != null
					&& userInfoFileLocation.trim().length() > 0) {
				
				JAXBContext jc = JAXBContext.newInstance(UserInfoData.class,
						BusinessPartnerUserInfo.class,
						GovernmentUserInfo.class, IndividualUserInfo.class,
						VerifiedIndividualUserInfo.class, BcServicesCardUserInfo.class);
				
				Unmarshaller u = jc.createUnmarshaller();
				
				try(Reader fileReader = new FileReader(userInfoFileLocation)) {

			        XMLReader reader = new NamespaceFilterXMLReader(true);
			        InputSource is = new InputSource(fileReader);
			        SAXSource ss = new SAXSource(reader, is);
			        
			        results = (UserInfoData) u.unmarshal(ss);
					
					List<UserInfo> userInfos = results.getUserInfos();
					logger.debug("user count: "+userInfos.size());
					
					for(UserInfo userInfo:userInfos) {
						logger.debug("Found: "+userInfo.getUserType()+" "+userInfo.getGuid());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return results;
	}
}
