package ca.bc.gov.nrs.wfone.common.persistence.code.dao;

public class CodeTableConfig {

	private String codeTableName;
	private String readScope;
	private String updateScope;
	private Boolean useRevisionCount = Boolean.TRUE;
	private Boolean useDisplayOrder = Boolean.TRUE;
	private CodeTableDao codeTableDao;
	private String fetchSql;
	private String insertSql;
	private String updateSql;
	private String expireSql;

	public String getCodeTableName() {
		return codeTableName;
	}

	public void setCodeTableName(String codeTableName) {
		this.codeTableName = codeTableName;
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

	public Boolean getUseDisplayOrder() {
		return useDisplayOrder;
	}

	public void setUseDisplayOrder(Boolean useDisplayOrder) {
		this.useDisplayOrder = useDisplayOrder;
	}

	public CodeTableDao getCodeTableDao() {
		return codeTableDao;
	}

	public void setCodeTableDao(CodeTableDao codeTableDao) {
		this.codeTableDao = codeTableDao;
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