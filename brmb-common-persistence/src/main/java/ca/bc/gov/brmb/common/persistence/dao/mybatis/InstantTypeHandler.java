package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class InstantTypeHandler implements TypeHandler<Instant> {

	@Override
	public Instant getResult(ResultSet resultSet, String name)
			throws SQLException {

		return valueOf(resultSet.getTimestamp(name));
	}

	@Override
	public Instant getResult(ResultSet resultSet, int position)
			throws SQLException {
		return valueOf(resultSet.getTimestamp(position));
	}

	@Override
	public Instant getResult(CallableStatement statement, int position)
			throws SQLException {

		return valueOf(statement.getTimestamp(position));
	}

	@Override
	public void setParameter(PreparedStatement statement, int position,
			Instant value, JdbcType jdbcType) throws SQLException {

		if(value==null) {
			
			statement.setNull(position, Types.TIMESTAMP);
		} else {

			statement.setTimestamp(position, Timestamp.from(value));
		}
	}

	private static Instant valueOf(Timestamp value) {
		Instant result = null;
		
		if(value!=null) {
			
			result = value.toInstant();
		}
		
		return result;
	}
}