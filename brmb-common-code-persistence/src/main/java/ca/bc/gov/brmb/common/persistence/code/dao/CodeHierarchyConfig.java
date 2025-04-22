package ca.bc.gov.brmb.common.persistence.code.dao;

public class CodeHierarchyConfig {

	private String codeHierarchyTableName;
	private String skeyColumnName;
	private String skeySequenceName;
	private String lowerCodeColumnName;
	private String upperCodeColumnName;
	private String readScope;
	private String updateScope;
	private Boolean useRevisionCount = Boolean.TRUE;
	private CodeHierarchyDao codeHierarchyDao;
	private String fetchSql;
	private String insertSql;
	private String updateSql;
	private String expireSql;

	public String getCodeHierarchyTableName() {
		return codeHierarchyTableName;
	}

	public void setCodeHierarchyTableName(String codeHierarchyTableName) {
		this.codeHierarchyTableName = codeHierarchyTableName;
	}

	public String getSkeyColumnName() {
		return skeyColumnName;
	}

	public void setSkeyColumnName(String skeyColumnName) {
		this.skeyColumnName = skeyColumnName;
	}

	public String getSkeySequenceName() {
		return skeySequenceName;
	}

	public void setSkeySequenceName(String skeySequenceName) {
		this.skeySequenceName = skeySequenceName;
	}

	public String getLowerCodeColumnName() {
		return lowerCodeColumnName;
	}

	public void setLowerCodeColumnName(String lowerCodeColumnName) {
		this.lowerCodeColumnName = lowerCodeColumnName;
	}

	public String getUpperCodeColumnName() {
		return upperCodeColumnName;
	}

	public void setUpperCodeColumnName(String upperCodeColumnName) {
		this.upperCodeColumnName = upperCodeColumnName;
	}

	public String getReadScope() {
		return readScope;
	}

	public void setReadScope(String readScope) {
		this.readScope = readScope;
	}

	public String getUpdateScope() {
		return updateScope;
	}

	public void setUpdateScope(String updateScope) {
		this.updateScope = updateScope;
	}

	public Boolean getUseRevisionCount() {
		return useRevisionCount;
	}

	public void setUseRevisionCount(Boolean useRevisionCount) {
		this.useRevisionCount = useRevisionCount;
	}

	public CodeHierarchyDao getCodeHierarchyDao() {
		return codeHierarchyDao;
	}

	public void setCodeHierarchyDao(CodeHierarchyDao codeHierarchyDao) {
		this.codeHierarchyDao = codeHierarchyDao;
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	public String getExpireSql() {
		return expireSql;
	}

	public void setExpireSql(String expireSql) {
		this.expireSql = expireSql;
	}
}
