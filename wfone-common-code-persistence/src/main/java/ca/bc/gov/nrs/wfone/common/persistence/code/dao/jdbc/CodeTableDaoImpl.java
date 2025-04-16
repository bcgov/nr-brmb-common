package ca.bc.gov.nrs.wfone.common.persistence.code.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeTableConfig;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeTableDao;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.nrs.wfone.common.persistence.dao.DaoException;

public class CodeTableDaoImpl extends BaseDao implements CodeTableDao {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(CodeTableDaoImpl.class);
	
	private DataSource dataSource;
	
	public CodeTableDto fetch(CodeTableConfig codeTableConfig, LocalDate effectiveAsOfDate) throws DaoException {
		logger.debug("<fetch");
		CodeTableDto result = null;
		
		try (Connection conn = this.dataSource.getConnection()) {
			
			List<CodeDto> dtos = select(conn, codeTableConfig, effectiveAsOfDate);
			
			result = new CodeTableDto();
			result.setCodeTableName(codeTableConfig.getCodeTableName());
			result.setCodes(dtos);
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		
		logger.debug(">fetch "+result);
		return result;
	}

	@Override
	public void update(CodeTableConfig codeTableConfig, CodeTableDto codeTableDto,
			String optimisticLock, String currentUserId) throws DaoException {
		logger.debug("<update "+codeTableConfig);
			
		List<CodeDto> dtos = codeTableDto.getCodes();

		Set<CodeDto> results = new HashSet<CodeDto>();
		
		try (Connection conn = this.dataSource.getConnection()) {
		
			List<CodeDto> persistedDtos = select(conn, codeTableConfig, null);
			
			for(Iterator<CodeDto> iter = dtos.iterator();iter.hasNext();) {
					CodeDto dto = iter.next();
					
				boolean found = false;
				for(CodeDto persistedDto:persistedDtos) {
					if(dto.equals(persistedDto)) {
						found = true;
						if(isDirty(dto, persistedDto)) {
							update(conn, codeTableConfig, dto, currentUserId);
						}
						results.add(persistedDto);
						break;
					}
				}
				
				if(!found) {
					insert(conn, codeTableConfig, dto, currentUserId);
					persistedDtos.add(dto);
					results.add(dto);
				}
			}
			
			for(CodeDto persistedDto:persistedDtos) {
					
				boolean found = false;
				for(CodeDto dto:dtos) {
					if(dto.equals(persistedDto)) {
						found = true; 
						break;
					}
				}
				
				if(!found && (persistedDto.getExpiryDate() == null || persistedDto.getExpiryDate().isAfter(LocalDate.now()))) {
					expire(conn, codeTableConfig, persistedDto, currentUserId);
				}
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">update");
	}
	
	protected List<CodeDto> select(Connection conn, CodeTableConfig codeTableConfig, LocalDate effectiveAsOfDate) throws DaoException {
		logger.debug("<select");
		List<CodeDto> result = null;
		
		String fetchSql = codeTableConfig.getFetchSql();
		
		if(fetchSql==null||fetchSql.trim().length()==0) {
			fetchSql = "SELECT "+codeTableConfig.getCodeTableName()+" CODE, DESCRIPTION, "+(Boolean.TRUE.equals(codeTableConfig.getUseDisplayOrder())?"DISPLAY_ORDER, ":"row_number() OVER (ORDER BY DESCRIPTION) DISPLAY_ORDER, ")+"EFFECTIVE_DATE, EXPIRY_DATE, "+(Boolean.TRUE.equals(codeTableConfig.getUseRevisionCount())?"REVISION_COUNT, ":"")+"CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE from "+codeTableConfig.getCodeTableName();
		}

		fetchSql = "select * from ("+fetchSql+") t "+(effectiveAsOfDate==null?"":"WHERE ? BETWEEN EFFECTIVE_DATE AND EXPIRY_DATE");
			
		logger.debug("fetchSql="+fetchSql);
		try (PreparedStatement st = conn.prepareStatement(fetchSql)) {

			if(effectiveAsOfDate!=null) {
				int parameterIndex = 0;
				setLocalDate(++parameterIndex, st, effectiveAsOfDate);
			}
			
			try (ResultSet rs = st.executeQuery()) {
				
				result = new ArrayList<>();
				while(rs.next()) {
					
					CodeDto dto = new CodeDto();
					
					dto.setCode(rs.getString("CODE"));
					dto.setDescription(rs.getString("DESCRIPTION"));
				    dto.setDisplayOrder(getInteger(rs, "DISPLAY_ORDER"));
					dto.setEffectiveDate(getLocalDate(rs, "EFFECTIVE_DATE"));
					dto.setExpiryDate(getLocalDate(rs, "EXPIRY_DATE"));
					if(!Boolean.FALSE.equals(codeTableConfig.getUseRevisionCount())) {
						dto.setRevisionCount(getLong(rs, "REVISION_COUNT"));
					}
					dto.setCreateTimestamp(getInstant(rs, "CREATE_DATE"));
					dto.setCreateUser(rs.getString("CREATE_USER"));
					dto.setUpdateTimestamp(getInstant(rs, "UPDATE_DATE"));
					dto.setUpdateUser(rs.getString("UPDATE_USER"));
					
					result.add(dto);
				}
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		
		logger.debug(">select "+result);
		return result;
	}

	protected void insert(Connection conn, CodeTableConfig codeTableConfig, CodeDto dto, String currentUserId) throws DaoException {
		logger.debug("<insert "+codeTableConfig);

		String insertSql = codeTableConfig.getInsertSql();
		
		if(insertSql==null||insertSql.trim().length()==0) {
			
			insertSql = "insert into "+codeTableConfig.getCodeTableName()+" ("
					+ codeTableConfig.getCodeTableName()+", "
					+ "DESCRIPTION, "
					+ (Boolean.TRUE.equals(codeTableConfig.getUseDisplayOrder())?"DISPLAY_ORDER, ":"")
					+ "EFFECTIVE_DATE, "
					+ "EXPIRY_DATE, "
					+ (Boolean.TRUE.equals(codeTableConfig.getUseRevisionCount())?"REVISION_COUNT, ":"")
					+ "CREATE_USER, "
					+ "CREATE_DATE, "
					+ "UPDATE_USER, "
					+ "UPDATE_DATE"
					+ ") values ("
					+ "?,"
					+ "?,"
					+ "?,"
					+ (Boolean.TRUE.equals(codeTableConfig.getUseDisplayOrder())?"?,":"")
					+ (dto.getEffectiveDate()==null?"trunc(SYSDATE),":"?,")
					+ (dto.getExpiryDate()==null?"to_date('9999-12-31','YYYY-MM-DD'),":"?,")
					+ (Boolean.TRUE.equals(codeTableConfig.getUseDisplayOrder())?"1,":"")
					+ "?,"
					+ "SYSDATE,"
					+ "?,"
					+ "SYSDATE"
					+ ")";
		}

		logger.debug("insertSql="+insertSql);
		try (PreparedStatement st = conn.prepareStatement(insertSql)) {
			
			int parameterIndex = 0;
			st.setString(++parameterIndex, dto.getCode());
			st.setString(++parameterIndex, dto.getDescription());
			if(!Boolean.FALSE.equals(codeTableConfig.getUseDisplayOrder())) {
				setInteger(++parameterIndex, st, dto.getDisplayOrder());
			}
			setLocalDate(++parameterIndex, st, dto.getEffectiveDate());
			if(dto.getExpiryDate()!=null) {
				setLocalDate(++parameterIndex, st, dto.getExpiryDate());
			}
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, currentUserId);
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to isnert code: "+codeTableConfig.getCodeTableName()+"."+dto.getCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">insert");
	}
	
	protected void update(Connection conn, CodeTableConfig codeTableConfig, CodeDto dto, String currentUserId) throws DaoException {
		logger.debug("<update");
		
		String updateSql = codeTableConfig.getUpdateSql();
		
		if(updateSql==null||updateSql.trim().length()==0) {
			updateSql = "UPDATE "+codeTableConfig.getCodeTableName()+" SET "
					+ "DESCRIPTION = ?, "
					+ (Boolean.TRUE.equals(codeTableConfig.getUseDisplayOrder())?"DISPLAY_ORDER = ?, ":"")
					+ "EFFECTIVE_DATE = ?, "
					+ "EXPIRY_DATE = "+(dto.getExpiryDate()==null?"to_date('9999-12-31','YYYY-MM-DD')":"?")+", "
					+(Boolean.TRUE.equals(codeTableConfig.getUseRevisionCount())?"REVISION_COUNT = REVISION_COUNT + 1, ":"")
					+"UPDATE_USER = ?, "
					+ "UPDATE_TIMESTAMP = SYSDATE "
					+ "WHERE "+codeTableConfig.getCodeTableName()+" = ?";
		
		}

		logger.debug("updateSql="+updateSql);
		try (PreparedStatement st = conn.prepareStatement(updateSql)) {
			
			int parameterIndex = 0;
			st.setString(++parameterIndex, dto.getDescription());
			if(!Boolean.FALSE.equals(codeTableConfig.getUseDisplayOrder())) {
				setInteger(++parameterIndex, st, dto.getDisplayOrder());
			}
			setLocalDate(++parameterIndex, st, dto.getEffectiveDate());
			if(dto.getExpiryDate()!=null) {
				setLocalDate(++parameterIndex, st, dto.getExpiryDate());
			}
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, dto.getCode());
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to update code: "+codeTableConfig.getCodeTableName()+"."+dto.getCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		
		logger.debug(">update");
	}
	
	protected void expire(Connection conn, CodeTableConfig codeTableConfig, CodeDto dto, String currentUserId) throws DaoException {
		logger.debug("<expire");
		
		String expireSql = codeTableConfig.getExpireSql();
		
		if(expireSql==null||expireSql.trim().length()==0) {
			expireSql = "UPDATE "+codeTableConfig.getCodeTableName()+" SET EXPIRY_DATE = trunc(SYSDATE), "+(Boolean.TRUE.equals(codeTableConfig.getUseRevisionCount())?"REVISION_COUNT = REVISION_COUNT + 1, ":"")+"UPDATE_USER = ?, UPDATE_TIMESTAMP = SYSDATE WHERE "+codeTableConfig.getCodeTableName()+" = ?";
		
		}

		logger.debug("expireSql="+expireSql);
		try (PreparedStatement st = conn.prepareStatement(expireSql)) {
			
			int parameterIndex = 0;
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, dto.getCode());
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to expire code: "+codeTableConfig.getCodeTableName()+"."+dto.getCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		
		
		logger.debug(">expire");
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

	private static boolean isDirty(CodeDto dto, CodeDto persistedDto) {
		boolean result = false;
		
		if(!equals(dto.getCode(), persistedDto.getCode())) {
			result = true;
		}
		
		if(!equals(dto.getDescription(), persistedDto.getDescription())) {
			result = true;
		}
		
		if(!equals(dto.getDisplayOrder(), persistedDto.getDisplayOrder())) {
			result = true;
		}
		
		if(!equals(dto.getEffectiveDate(), persistedDto.getEffectiveDate())) {
			result = true;
		}
		
		if(!equals(dto.getExpiryDate(), persistedDto.getExpiryDate())) {
			result = true;
		}
		
		return result;
	}
	
	private static boolean equals(Object object0, Object object1) {
		boolean result = true;
		
		if(object0==null) {
			if(object1!=null) {
				result = false;
			}
		} else {
			if(object1==null) {
				result = false;
			} else if(!object0.equals(object1)) {
				result = false;
			}
		}
		
		return result;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
