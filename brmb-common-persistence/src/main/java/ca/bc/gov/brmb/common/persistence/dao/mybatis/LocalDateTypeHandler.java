package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class LocalDateTypeHandler implements TypeHandler<LocalDate> {

	@Override
	public LocalDate getResult(ResultSet resultSet, String name)
			throws SQLException {

		return valueOf(resultSet.getDate(name));
	}

	@Override
	public LocalDate getResult(ResultSet resultSet, int position)
			throws SQLException {
		return valueOf(resultSet.getDate(position));
	}

	@Override
	public LocalDate getResult(CallableStatement statement, int position)
			throws SQLException {

		return valueOf(statement.getDate(position));
	}

	@Override
	public void setParameter(PreparedStatement statement, int position,
			LocalDate value, JdbcType jdbcType) throws SQLException {

		if(value==null) {
			
			statement.setNull(position, Types.DATE);
		} else {
			
			statement.setDate(position, Date.valueOf(value));
		}
	}

	private static LocalDate valueOf(Date value) {
		LocalDate result = null;
		
		if(value!=null) {
			
			result = value.toLocalDate();
		}
		
		return result;
	}
}