package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class LocalTimeTypeHandler implements TypeHandler<LocalTime> {

	@Override
	public LocalTime getResult(ResultSet resultSet, String name)
			throws SQLException {

		return valueOf(resultSet.getString(name));
	}

	@Override
	public LocalTime getResult(ResultSet resultSet, int position)
			throws SQLException {
		return valueOf(resultSet.getString(position));
	}

	@Override
	public LocalTime getResult(CallableStatement statement, int position)
			throws SQLException {

		return valueOf(statement.getString(position));
	}

	@Override
	public void setParameter(PreparedStatement statement, int position,
			LocalTime value, JdbcType jdbcType) throws SQLException {

		if(value==null) {
			statement.setNull(position, Types.VARCHAR);
		} else {
			String timeString = value.format(DateTimeFormatter.ISO_LOCAL_TIME);
			statement.setString(position, timeString);
		}
	}

	private static LocalTime valueOf(String value) throws SQLException {
		LocalTime result = null;
		
		if(value!=null) {
			
			try {
			result = LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME);
			} catch(DateTimeParseException e) {
				throw new SQLException("Unexpected value " + value
						+ " found.", e);
			}
		}
		
		return result;
	}
}