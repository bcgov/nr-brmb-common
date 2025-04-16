package ca.bc.gov.nrs.wfone.common.persistence.code.dao.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;

public abstract class BaseDao {

	protected LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
		LocalDate result = null;
		
		Date value = rs.getDate(columnName);
		if(!rs.wasNull()) {
			
			result = value.toLocalDate();
		}
		
		return result;
	}

	protected Instant getInstant(ResultSet rs, String columnName) throws SQLException {
		Instant result = null;
		
		Timestamp value = rs.getTimestamp(columnName);
		if(!rs.wasNull()) {
			
			result = value.toInstant();
		}
		
		return result;
	}

	protected Long getLong(ResultSet rs, String columnName) throws SQLException {
		Long result = null;
		
		long value = rs.getLong(columnName);
		if(!rs.wasNull()) {
			result = Long.valueOf(value);
		}
		
		return result;
	}

	protected Integer getInteger(ResultSet rs, String columnName) throws SQLException {
		Integer result = null;
		
		int value = rs.getInt(columnName);
		if(!rs.wasNull()) {
			result = Integer.valueOf(value);
		}
		
		return result;
	}
	
	protected void setInteger(int parameterIndex, PreparedStatement st, Integer value) throws SQLException {
		
		if(value == null) {
			st.setNull(parameterIndex, Types.NUMERIC);
		} else {
			st.setInt(parameterIndex, value.intValue());
		}
	}
	
	protected void setLocalDate(int parameterIndex, PreparedStatement st, LocalDate value) throws SQLException {
		
		if(value == null) {
			st.setNull(parameterIndex, Types.DATE);
		} else {
			st.setDate(parameterIndex, Date.valueOf(value));
		}
	}
}
