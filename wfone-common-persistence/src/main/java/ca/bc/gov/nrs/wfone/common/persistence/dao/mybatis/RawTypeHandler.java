package ca.bc.gov.nrs.wfone.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.RAW;

public class RawTypeHandler implements TypeHandler<String> {

	@Override
	public String getResult(ResultSet resultSet, String name)
			throws SQLException {

		return resultSet.getString(name);
	}

	@Override
	public String getResult(ResultSet resultSet, int position)
			throws SQLException {
		
		return resultSet.getString(position);
	}

	@Override
	public String getResult(CallableStatement statement, int position)
			throws SQLException {

		return statement.getString(position);
	}

	@Override
	public void setParameter(
			PreparedStatement statement, 
			int position,
			String value, 
			JdbcType jdbcType) throws SQLException {

		if(!JdbcType.BINARY.equals(jdbcType)) {
			
			throw new java.lang.IllegalArgumentException("Not expecting jdbcType: "+jdbcType);
		}
		
		if(value==null) {
			
			statement.setNull(position, Types.VARCHAR);
			
		} else if(statement.isWrapperFor(OraclePreparedStatement.class)) {
				
			byte[] bytes = hexStringToByteArray(value);
			RAW raw = new RAW(bytes);

			statement.unwrap(OraclePreparedStatement.class).setRAW(position, raw);
		} else {
			
			statement.setString(position, value);
		}
	}
	
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}