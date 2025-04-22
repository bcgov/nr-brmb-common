package ca.bc.gov.brmb.common.rest.resource.types;

public class BaseResourceTypes
{
	public static final String COMMON_NAMESPACE = "http://common.brmb.nrs.gov.bc.ca/v1/";
	
	public static final String SELF = "self";

	public static final String PREV = "prev";
	public static final String NEXT = "next";

	public static final String MESSAGE_LIST_NAME = "messages";

	public static final String MESSAGE_LIST = COMMON_NAMESPACE + MESSAGE_LIST_NAME;

	public static final String MESSAGE_NAME = "message";

	public static final String MESSAGE = COMMON_NAMESPACE + MESSAGE_NAME;

	public static final String REDIRECT_NAME = "redirect";

	public static final String REDIRECT = COMMON_NAMESPACE + REDIRECT_NAME;

	public static final String RELLINK_NAME = "rellink";

	public static final String RELLINK = COMMON_NAMESPACE + RELLINK_NAME;
	
	public static final String BYTES = "bytes";
	
	public static final String CODE_TABLE_LIST_NAME = "codeTables";
	public static final String CODE_TABLE_LIST = COMMON_NAMESPACE + CODE_TABLE_LIST_NAME;
	
	public static final String CODE_TABLE_NAME = "codeTable";
	public static final String CODE_TABLE = COMMON_NAMESPACE + CODE_TABLE_NAME;
	public static final String UPDATE_CODE_TABLE = COMMON_NAMESPACE + "updateCodeTable";

	public static final String CODE_NAME = "code";

	public static final String CODE = COMMON_NAMESPACE + CODE_NAME;
	
	public static final String CODE_HIERARCHY_LIST_NAME = "codeHierarchies";
	public static final String CODE_HIERARCHY_LIST = COMMON_NAMESPACE + CODE_HIERARCHY_LIST_NAME;
	
	public static final String CODE_HIERARCHY_NAME = "codeHierarchy";
	public static final String CODE_HIERARCHY = COMMON_NAMESPACE + CODE_HIERARCHY_NAME;
	public static final String UPDATE_CODE_HIERARCHY = COMMON_NAMESPACE + "updateCodeHierarchy";

	public static final String HEALTH_CHECK_RESPONSE_NAME = "HealthCheckResponse";
	public static final String HEALTH_CHECK_RESPONSE = COMMON_NAMESPACE+HEALTH_CHECK_RESPONSE_NAME;
	
}
