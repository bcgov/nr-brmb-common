package ca.bc.gov.brmb.common.persistence.code.dao.jdbc;

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

import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyConfig;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyDao;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.brmb.common.persistence.code.dto.HierarchyDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;

public class CodeHierarchyDaoImpl extends BaseDao implements CodeHierarchyDao {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory
			.getLogger(CodeHierarchyDaoImpl.class);
	
	private DataSource dataSource;

	@Override
	public CodeHierarchyDto fetch(CodeHierarchyConfig codeHierarchyConfig, LocalDate effectiveAsOfDate) throws DaoException {
		logger.debug("<fetch "+codeHierarchyConfig);

		CodeHierarchyDto result = null;

		try (Connection conn = this.dataSource.getConnection()) {

			List<HierarchyDto> dtos = select(conn, codeHierarchyConfig, effectiveAsOfDate);
			
			result = new CodeHierarchyDto();
			result.setCodeHierarchyName(codeHierarchyConfig.getCodeHierarchyTableName());
			result.setUpperCodeTableName(codeHierarchyConfig.getUpperCodeColumnName());
			result.setLowerCodeTableName(codeHierarchyConfig.getLowerCodeColumnName());
			result.setHierarchy(dtos);

		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">fetch " + result);
		return result;
	}

	@Override
	public void update(CodeHierarchyConfig codeHierarchyConfig, CodeHierarchyDto codeHierarchyDto,
			String optimisticLock, String currentUserId) throws DaoException {
		logger.debug("<update "+codeHierarchyConfig);

		try (Connection conn = this.dataSource.getConnection()) {
			
			List<HierarchyDto> dtos = codeHierarchyDto.getHierarchy();

			Set<HierarchyDto> results = new HashSet<HierarchyDto>();
			
			List<HierarchyDto> persistedDtos = select(conn, codeHierarchyConfig, null);
			
			for(Iterator<HierarchyDto> iter = dtos.iterator();iter.hasNext();) {
					HierarchyDto dto = iter.next();
					
				boolean found = false;
				for(HierarchyDto persistedDto:persistedDtos) {
					if(dto.equals(persistedDto)) {
						found = true;
						if(isDirty(dto, persistedDto)) {
							update(conn, codeHierarchyConfig, dto, currentUserId);
						}
						results.add(persistedDto);
						break;
					}
				}
				
				if(!found) {
					insert(conn, codeHierarchyConfig, dto, currentUserId);
					persistedDtos.add(dto);
					results.add(dto);
				}
			}
			
			for(HierarchyDto persistedDto:persistedDtos) {
					
				boolean found = false;
				for(HierarchyDto dto:dtos) {
					if(dto.equals(persistedDto)) {
						found = true; 
						break;
					}
				}
				
				if(!found && (persistedDto.getExpiryDate() == null || persistedDto.getExpiryDate().isAfter(LocalDate.now()))) {
					expire(conn, codeHierarchyConfig, persistedDto, currentUserId);
				}
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">update");
	}

	private static boolean isDirty(HierarchyDto dto, HierarchyDto persistedDto) {
		boolean result = false;
		
		if(!equals(dto.getUpperCode(), persistedDto.getUpperCode())) {
			result = true;
		}
		
		if(!equals(dto.getLowerCode(), persistedDto.getLowerCode())) {
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

	private List<HierarchyDto> select(Connection conn, CodeHierarchyConfig codeHierarchyConfig, LocalDate effectiveAsOfDate) throws DaoException {
		logger.debug("<select "+codeHierarchyConfig);

		List<HierarchyDto> result = null;

		String fetchSql = codeHierarchyConfig.getFetchSql();
		
		if(fetchSql==null||fetchSql.trim().length()==0) {
			fetchSql = "select "+codeHierarchyConfig.getSkeyColumnName()+" SKEY, "+codeHierarchyConfig.getUpperCodeColumnName()+" UPPER_CODE, "+codeHierarchyConfig.getLowerCodeColumnName()+" LOWER_CODE, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER, CREATE_DATE, UPDATE_USER, UPDATE_DATE from "+codeHierarchyConfig.getCodeHierarchyTableName();
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
					
					HierarchyDto dto = new HierarchyDto();
					
					
					dto.setSkey(rs.getString("SKEY"));
					dto.setUpperCode(rs.getString("UPPER_CODE"));
					dto.setLowerCode(rs.getString("LOWER_CODE"));
					dto.setEffectiveDate(getLocalDate(rs, "EFFECTIVE_DATE"));
					dto.setExpiryDate(getLocalDate(rs, "EXPIRY_DATE"));
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

		logger.debug(">select " + result);
		return result;
	}

	private void insert(Connection conn, CodeHierarchyConfig codeHierarchyConfig, HierarchyDto dto, String currentUserId) throws DaoException {
		logger.debug("<insert "+codeHierarchyConfig);

		String insertSql = codeHierarchyConfig.getInsertSql();
		
		if(insertSql==null||insertSql.trim().length()==0) {
			
			insertSql = "insert into "+codeHierarchyConfig.getCodeHierarchyTableName()+" ("
					+ codeHierarchyConfig.getSkeyColumnName()+", "
					+ codeHierarchyConfig.getUpperCodeColumnName()+", "
					+ codeHierarchyConfig.getLowerCodeColumnName()+", "
					+ "EFFECTIVE_DATE, "
					+ "EXPIRY_DATE, "
					+ (Boolean.TRUE.equals(codeHierarchyConfig.getUseRevisionCount())?"REVISION_COUNT, ":"")
					+ "CREATE_USER, "
					+ "CREATE_DATE, "
					+ "UPDATE_USER, "
					+ "UPDATE_DATE"
					+ ") values ("
					+ (codeHierarchyConfig.getSkeySequenceName()==null?"sys_guid(),":codeHierarchyConfig.getSkeySequenceName()+".nextval,")
					+ "?,"
					+ "?,"
					+ (dto.getEffectiveDate()==null?"trunc(SYSDATE),":"?,")
					+ (dto.getExpiryDate()==null?"to_date('9999-12-31','YYYY-MM-DD'),":"?,")
					+ (Boolean.TRUE.equals(codeHierarchyConfig.getUseRevisionCount())?"1,":"")
					+ "?,"
					+ "SYSDATE,"
					+ "?,"
					+ "SYSDATE"
					+ ")";
		}

		logger.debug("insertSql="+insertSql);
		try (PreparedStatement st = conn.prepareStatement(insertSql)) {
			
			int parameterIndex = 0;
			st.setString(++parameterIndex, dto.getUpperCode());
			st.setString(++parameterIndex, dto.getLowerCode());
			setLocalDate(++parameterIndex, st, dto.getEffectiveDate());
			if(dto.getExpiryDate()!=null) {
				setLocalDate(++parameterIndex, st, dto.getExpiryDate());
			}
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, currentUserId);
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to insert code: "+codeHierarchyConfig.getCodeHierarchyTableName()+"."+dto.getUpperCode()+"-"+dto.getLowerCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">insert");
	}

	private void update(Connection conn, CodeHierarchyConfig codeHierarchyConfig, HierarchyDto dto, String currentUserId) throws DaoException {
		logger.debug("<update "+codeHierarchyConfig);
		
		String updateSql = codeHierarchyConfig.getUpdateSql();
		
		if(updateSql==null||updateSql.trim().length()==0) {
			
			updateSql = "update "+codeHierarchyConfig.getCodeHierarchyTableName()+" set "
					+ "EFFECTIVE_DATE = ?,"
					+ "EXPIRY_DATE = "+(dto.getExpiryDate()==null?"to_date('9999-12-31','YYYY-MM-DD')":"?")+","
					+ "UPDATE_USER = ?,"
					+ (Boolean.TRUE.equals(codeHierarchyConfig.getUseRevisionCount())?"REVISION_COUNT = REVISION_COUNT + 1,":"")
					+ "UPDATE_DATE = SYSDATE "
					+ "WHERE "+codeHierarchyConfig.getUpperCodeColumnName()+" = ?"
					+ "  AND "+codeHierarchyConfig.getLowerCodeColumnName()+" = ?";
		}

		logger.debug("updateSql="+updateSql);
		try (PreparedStatement st = conn.prepareStatement(updateSql)) {
			
			int parameterIndex = 0;
			setLocalDate(++parameterIndex, st, dto.getEffectiveDate());
			if(dto.getExpiryDate()!=null) {
				setLocalDate(++parameterIndex, st, dto.getExpiryDate());
			}
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, dto.getUpperCode());
			st.setString(++parameterIndex, dto.getLowerCode());
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to update code: "+codeHierarchyConfig.getCodeHierarchyTableName()+"."+dto.getUpperCode()+"-"+dto.getLowerCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">update");
	}
	
	private static void expire(Connection conn, CodeHierarchyConfig codeHierarchyConfig, HierarchyDto dto, String currentUserId) throws DaoException {
		logger.debug("<update "+codeHierarchyConfig);
		
		String expireSql = codeHierarchyConfig.getExpireSql();
		
		if(expireSql==null||expireSql.trim().length()==0) {
			
			expireSql = "update "+codeHierarchyConfig.getCodeHierarchyTableName()+" set "
					+ "EXPIRY_DATE = trunc(SYSDATE),"
					+ "UPDATE_USER = ?,"
					+ (Boolean.TRUE.equals(codeHierarchyConfig.getUseRevisionCount())?"REVISION_COUNT = REVISION_COUNT + 1,":"")
					+ "UPDATE_DATE = SYSDATE "
					+ "WHERE "+codeHierarchyConfig.getUpperCodeColumnName()+" = ?"
					+ "  AND "+codeHierarchyConfig.getLowerCodeColumnName()+" = ?";
		}

		logger.debug("expireSql="+expireSql);
		try (PreparedStatement st = conn.prepareStatement(expireSql)) {
			
			int parameterIndex = 0;
			st.setString(++parameterIndex, currentUserId);
			st.setString(++parameterIndex, dto.getUpperCode());
			st.setString(++parameterIndex, dto.getLowerCode());
			int updateCount = st.executeUpdate();
			if(updateCount == 0) {
				throw new DaoException("Failed to update code: "+codeHierarchyConfig.getCodeHierarchyTableName()+"."+dto.getUpperCode()+"-"+dto.getLowerCode());
			}
			
		} catch (SQLException e) {
			throw new DaoException(e);
		}

		logger.debug(">update");
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
