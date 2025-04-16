package ca.bc.gov.nrs.wfone.common.persistence.code.spring;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeHierarchyDao;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeTableDao;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.jdbc.CodeHierarchyDaoImpl;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.jdbc.CodeTableDaoImpl;

@Configuration
public class CodePersistenceSpringConfig {

	@Autowired 
	@Qualifier("codeTableDataSource") 
	DataSource dataSource;
	
	@Bean
	public CodeTableDao codeTableDao() {
		CodeTableDaoImpl result;
		
		result = new CodeTableDaoImpl();
		result.setDataSource(dataSource);
		
		return result;
	}
	
	@Bean
	public CodeHierarchyDao codeHierarchyDao() {
		CodeHierarchyDaoImpl result;
		
		result = new CodeHierarchyDaoImpl();
		result.setDataSource(dataSource);
		
		return result;
	}
}
