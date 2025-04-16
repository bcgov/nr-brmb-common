package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class LocalDateTimeTypeHandler implements TypeHandler<LocalDateTime> {

	@Override
	public LocalDateTime getResult(ResultSet resultSet, String name)
			throws SQLException {

		return valueOf(resultSet.getTimestamp(name));
	}

	@Override
	public LocalDateTime getResult(ResultSet resultSet, int position)
			throws SQLException {
		return valueOf(resultSet.getTimestamp(position));
	}

	@Override
	public LocalDateTime getResult(CallableStatement statement, int position)
			throws SQLException {

		return valueOf(statement.getTimestamp(position));
	}

	@Override
	public void setParameter(PreparedStatement statement, int position,
			LocalDateTime value, JdbcType jdbcType) throws SQLException {

		if(value==null) {
			
			statement.setNull(position, Types.TIMESTAMP);
		} else {

			statement.setTimestamp(position, Timestamp.valueOf(value));
		}
	}

	private static LocalDateTime valueOf(Timestamp value) {
		LocalDateTime result = null;
		
		if(value!=null) {
			
			result = value.toLocalDateTime();
		}
		
		return result;
	}
}