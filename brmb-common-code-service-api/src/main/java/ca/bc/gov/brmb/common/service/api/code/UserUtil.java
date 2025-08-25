package ca.bc.gov.brmb.common.service.api.code;

public class UserUtil {

    public static String toUserId(String userEmail) {
        if (userEmail != null && userEmail.trim().length() > 0) {
            String result = userEmail.split("@")[0];
            result = result.length() > 30 ? result.substring(0, 30) : result;
            return result;
        }
        return null;
    }
}
