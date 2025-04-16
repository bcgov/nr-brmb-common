package ca.bc.gov.brmb.common.persistence.dao.mybatis;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.oracle.OraReader;
import com.vividsolutions.jts.io.oracle.OraWriter;

import oracle.jdbc.OracleConnection;
import oracle.sql.STRUCT;

@SuppressWarnings("deprecation")
public class GeometryTypeHandler implements TypeHandler<Geometry> {

	private static final Logger logger = LoggerFactory
			.getLogger(GeometryTypeHandler.class);

	@Override
	public void setParameter(PreparedStatement statement, int parameterIndex,
			Geometry geometry, JdbcType jdbcType) throws SQLException {
		logger.debug("<setParameter");

		Struct struct = null;
		
		OraWriter writer =  new OraWriter();
		
		Connection conn = statement.getConnection();
		
		Class<? extends Connection> clazz = conn.getClass();
		
		logger.debug("Connection.class="+clazz);
		
		logger.debug("super.class="+clazz.getSuperclass());
		
		Class<?>[] interfaces = clazz.getInterfaces();
		for(Class<?> interfaceClazz:interfaces) {
			logger.debug("interface.class="+interfaceClazz);
		}
		
		OracleConnection oracleConnection = null;
		
		if(conn instanceof OracleConnection) {
			logger.debug("Casting connection to 'oracle.jdbc.OracleConnection'.");
			oracleConnection = (OracleConnection) conn;
		} else if(conn.isWrapperFor(OracleConnection.class)) {
			logger.debug("Unwrapping 'oracle.jdbc.OracleConnection' connection.");
			oracleConnection = conn.unwrap(OracleConnection.class);
		} else {
			throw new RuntimeException("Failed to unwrap connection to 'oracle.jdbc.OracleConnection'.");
		}
		
		struct = writer.write(geometry, oracleConnection);

		statement.setObject(parameterIndex, struct, Types.STRUCT);

		logger.debug(">setParameter");
	}

	@Override
	public Geometry getResult(ResultSet rs, String columnName)
			throws SQLException {
		Geometry result = null;

		Struct struct = (Struct) rs.getObject(columnName);

		result = toGeometry(struct);

		return result;
	}

	@Override
	public Geometry getResult(ResultSet rs, int columnIndex)
			throws SQLException {
		Geometry result = null;

		Struct struct = (Struct) rs.getObject(columnIndex);

		result = toGeometry(struct);

		return result;
	}

	@Override
	public Geometry getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		Geometry result = null;

		Struct struct = (Struct) cs.getObject(columnIndex);

		result = toGeometry(struct);

		return result;
	}

	private static Geometry toGeometry(Struct struct) throws SQLException {
		Geometry result = null;
		
		OraReader reader = new OraReader();
		
		try {
			result = reader.read((STRUCT) struct);
		} catch(IllegalArgumentException e) {
			logger.warn("Cound not create geometry.",e);
		}

		return result;
	}
}