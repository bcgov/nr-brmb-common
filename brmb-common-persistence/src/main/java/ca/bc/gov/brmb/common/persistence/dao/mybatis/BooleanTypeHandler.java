package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class BooleanTypeHandler implements TypeHandler<Boolean> {

	private static final String YES = "Y";
	private static final String NO = "N";

	@Override
	public Boolean getResult(ResultSet resultSet, String name)
			throws SQLException {

		return valueOf(resultSet.getString(name));
	}

	@Override
	public Boolean getResult(ResultSet resultSet, int position)
			throws SQLException {
		return valueOf(resultSet.getString(position));
	}

	@Override
	public Boolean getResult(CallableStatement statement, int position)
			throws SQLException {

		return valueOf(statement.getString(position));
	}

	@Override
	public void setParameter(PreparedStatement statement, int position,
			Boolean value, JdbcType jdbcType) throws SQLException {

		if(value==null) {
			statement.setNull(position, Types.VARCHAR);
		} else {
			statement.setString(position, value.booleanValue() ? YES : NO);
		}
	}

	private static Boolean valueOf(String value) throws SQLException {
		Boolean result = null;
		
		if(value!=null) {
			
			if (YES.equalsIgnoreCase(value)) {
				result = Boolean.valueOf(true);
			} else if (NO.equalsIgnoreCase(value)) {
				result = Boolean.valueOf(false);
			} else {
				throw new SQLException("Unexpected value " + value
						+ " found where " + YES + " or " + NO + " was expected.");
			}
		}
		
		return result;
	}
}