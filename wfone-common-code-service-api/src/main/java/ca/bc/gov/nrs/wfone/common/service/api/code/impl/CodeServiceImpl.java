package ca.bc.gov.nrs.wfone.common.service.api.code.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.brmb.common.model.Code;
import ca.bc.gov.brmb.common.model.CodeHierarchy;
import ca.bc.gov.brmb.common.model.CodeHierarchyList;
import ca.bc.gov.brmb.common.model.CodeTable;
import ca.bc.gov.brmb.common.model.CodeTableList;
import ca.bc.gov.brmb.common.model.Hierarchy;
import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeHierarchyConfig;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeHierarchyDao;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeTableConfig;
import ca.bc.gov.nrs.wfone.common.persistence.code.dao.CodeTableDao;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.nrs.wfone.common.persistence.code.dto.HierarchyDto;
import ca.bc.gov.nrs.wfone.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.wfone.common.persistence.dao.OptimisticLockingFailureDaoException;
import ca.bc.gov.nrs.wfone.common.service.api.ConflictException;
import ca.bc.gov.nrs.wfone.common.service.api.ForbiddenException;
import ca.bc.gov.nrs.wfone.common.service.api.NotFoundException;
import ca.bc.gov.nrs.wfone.common.service.api.ServiceException;
import ca.bc.gov.nrs.wfone.common.service.api.ValidationFailureException;
import ca.bc.gov.nrs.wfone.common.service.api.code.CodeService;
import ca.bc.gov.nrs.wfone.common.service.api.code.model.factory.CodeHierarchyFactory;
import ca.bc.gov.nrs.wfone.common.service.api.code.model.factory.CodeTableFactory;
import ca.bc.gov.nrs.wfone.common.service.api.code.validation.CodeValidator;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public class CodeServiceImpl implements CodeService {

	private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);
	
	private CodeTableDao codeTableDao;
	
	private CodeHierarchyDao codeHierarchyDao;
	
	private CodeTableFactory codeTableFactory;
	
	private CodeHierarchyFactory codeHierarchyFactory;
	
	private CodeValidator codeValidator;
	
	private List<CodeTableConfig> codeTableConfigs = new ArrayList<>();
	
	private List<CodeHierarchyConfig> codeHierarchyConfigs = new ArrayList<>();
	
	@Override
	public CodeTableList<? extends CodeTable<? extends Code>> getCodeTableList(
			LocalDate effectiveAsOfDate, 
			String codeTableName, 
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication) throws ServiceException {
		logger.debug("<getCodeTables");

		CodeTableList<? extends CodeTable<? extends Code>> results = null;
		
		if(codeTableConfigs==null||codeTableConfigs.isEmpty()) {
			logger.warn("No codeTables have been configured.");
		}

		try {
			
			List<CodeTableDto> dtos = new ArrayList<CodeTableDto>();
			
			if(codeTableName!=null&&codeTableName.trim().length()>0) {
				
				CodeTableConfig codeTableConfig = null;
				for(CodeTableConfig tmp:this.codeTableConfigs) {
					if(tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
						codeTableConfig = tmp;
						break;
					}
				}

				if(codeTableConfig!=null) {
					
					boolean canRead = true;
					String readScope = codeTableConfig.getReadScope();
					if(readScope!=null&&readScope.trim().length()>0) {
						canRead = webAdeAuthentication.hasAuthority(readScope);
					}
				
					if(canRead) {
					
						try {
							
							CodeTableDao dao = codeTableConfig.getCodeTableDao();
							
							if(dao==null) {
								dao = this.codeTableDao;
							}
							
							if(dao==null) {
								logger.error("No codeTableDao has been configured for "+codeTableConfig.getCodeTableName()+".");
							}
							
							dtos.add(dao.fetch(codeTableConfig, effectiveAsOfDate));
							
						} catch(IllegalArgumentException e) {
							// do nothing
						}
					}
				}
			} else {
				
				for(CodeTableConfig codeTableConfig:this.codeTableConfigs) {
					
					boolean canRead = true;
					String readScope = codeTableConfig.getReadScope();
					if(readScope!=null&&readScope.trim().length()>0) {
						canRead = webAdeAuthentication.hasAuthority(readScope);
					}
					
					CodeTableDao dao = codeTableConfig.getCodeTableDao();
					
					if(dao==null) {
						dao = this.codeTableDao;
					}
					
					if(codeTableConfigs==null||codeTableConfigs.isEmpty()) {
						logger.error("No codeTableDao has been configured for "+codeTableConfig.getCodeTableName()+".");
					}
					
					if(canRead) {
						dtos.add(dao.fetch(codeTableConfig, effectiveAsOfDate));
					}
				}
			}

			results = this.codeTableFactory.getCodeTables(effectiveAsOfDate, codeTableName, dtos, context);

		} catch (DaoException e) {
			
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">getCodeTables: " + results);
		return results;
	}


	@Override
	public CodeTable<? extends Code> getCodeTable(
			String codeTableName, 
			LocalDate effectiveAsOfDate, 
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication)
			throws ServiceException, ForbiddenException, NotFoundException {
		logger.debug("<getCodeTable");

		CodeTable<? extends Code> result = null;
		
		if(codeTableConfigs==null||codeTableConfigs.isEmpty()) {
			logger.warn("No codeTables have been configured.");
		}

		try {
			
			CodeTableConfig codeTableConfig = null;
			for(CodeTableConfig tmp:this.codeTableConfigs) {
				if(tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
					codeTableConfig = tmp;
					break;
				}
			}
			
			if(codeTableConfig==null) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			boolean canRead = true;
			String readScope = codeTableConfig.getReadScope();
			if(readScope!=null&&readScope.trim().length()>0) {
				canRead = webAdeAuthentication.hasAuthority(readScope);
			}
			
			if(!canRead) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			CodeTableDao dao = codeTableConfig.getCodeTableDao();
			
			if(dao==null) {
				dao = this.codeTableDao;
			}
			
			if(dao==null) {
				logger.error("No codeTableDao has been configured for "+codeTableConfig.getCodeTableName()+".");
			}
			
			CodeTableDto dto = dao.fetch(codeTableConfig, effectiveAsOfDate);

			if (dto == null) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			boolean canUpdate = false;
			String updateScope = codeTableConfig.getUpdateScope();
			if(updateScope!=null&&updateScope.trim().length()>0) {
				canUpdate = webAdeAuthentication.hasAuthority(updateScope);
			}

			result = this.codeTableFactory.getCodeTable(dto, effectiveAsOfDate, canUpdate, context);
			
		} catch (DaoException e) {
			
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">getCodeTable " + result.getCodeTableName());
		return result;
	}
	
	@Override
	public CodeTable<? extends Code> updateCodeTable(
			String codeTableName,
			String optimisticLock,
			CodeTable<? extends Code> codeTable,
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication)
			throws ServiceException, NotFoundException, ForbiddenException, ConflictException, ValidationFailureException {
		logger.debug("<updateCodeTable");

		CodeTable<? extends Code> result = null;
		
		if(codeTableConfigs==null||codeTableConfigs.isEmpty()) {
			logger.warn("No codeTables have been configured.");
		}

		try {
			
			CodeTableConfig codeTableConfig = null;
			for(CodeTableConfig tmp:this.codeTableConfigs) {
				if(tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
					codeTableConfig = tmp;
					break;
				}
			}
			
			if(codeTableConfig==null) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			boolean canRead = true;
			String readScope = codeTableConfig.getReadScope();
			if(readScope!=null&&readScope.trim().length()>0) {
				canRead = webAdeAuthentication.hasAuthority(readScope);
			}
			
			if(!canRead) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			CodeTableDao dao = codeTableConfig.getCodeTableDao();
			
			if(dao==null) {
				dao = this.codeTableDao;
			}
			
			if(dao==null) {
				logger.error("No codeTableDao has been configured for "+codeTableConfig.getCodeTableName()+".");
			}
			
			CodeTableDto dto = dao.fetch(codeTableConfig, null);

			if (dto == null) {
				throw new NotFoundException("Did not find the CodeTable: "+codeTableName);
			}
			
			List<Message> errors = new ArrayList<>();
			errors.addAll(this.codeValidator.validateUpdateCodeTable(codeTable));
			
			if(!errors.isEmpty()) {
				throw new ValidationFailureException(errors);
			}
			
			applyModel(codeTable, dto);
			
			dao.update(codeTableConfig, dto, optimisticLock, webAdeAuthentication.getUserId());

			dto = dao.fetch(codeTableConfig, null);
			
			boolean canUpdate = false;
			String updateScope = codeTableConfig.getUpdateScope();
			if(updateScope!=null&&updateScope.trim().length()>0) {
				canUpdate = webAdeAuthentication.hasAuthority(updateScope);
			}

			result = this.codeTableFactory.getCodeTable(dto, null, canUpdate, context);

		} catch (OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">updateCodeTable " + result.getCodeTableName());
		return result;
	}


	private static void applyModel(CodeTable<? extends Code> codeTable, CodeTableDto dto) {
		logger.debug("<applyModel");
		
		dto.setCodeTableName(codeTable.getCodeTableName());
		
		List<CodeDto> codeDtos = new ArrayList<>();
		
		for(Code code:codeTable.getCodes()) {
			CodeDto codeDto = new CodeDto();
			
			codeDto.setCode(code.getCode());
			codeDto.setDescription(code.getDescription());
			codeDto.setDisplayOrder(code.getDisplayOrder());
			codeDto.setEffectiveDate(code.getEffectiveDate());
			codeDto.setExpiryDate(code.getExpiryDate());
			
			codeDtos.add(codeDto);
		}
		
		dto.setCodes(codeDtos);
		
		logger.debug(">applyModel");
	}
	
	@Override
	public CodeHierarchyList<? extends CodeHierarchy> getCodeHierarchyList(
			LocalDate effectiveAsOfDate, 
			String codeHierarchyName, 
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication) throws ServiceException {
		logger.debug("<getCodeHierarchys");

		CodeHierarchyList<? extends CodeHierarchy> results = null;
		
		if(codeHierarchyConfigs==null||codeHierarchyConfigs.isEmpty()) {
			logger.warn("No codeHierarchys have been configured.");
		}

		try {
			
			List<CodeHierarchyDto> dtos = new ArrayList<CodeHierarchyDto>();
			
			if(codeHierarchyName!=null&&codeHierarchyName.trim().length()>0) {
				
				CodeHierarchyConfig codeHierarchyConfig = null;
				for(CodeHierarchyConfig tmp:this.codeHierarchyConfigs) {
					if(tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
						codeHierarchyConfig = tmp;
						break;
					}
				}

				if(codeHierarchyConfig!=null) {
					
					boolean canRead = true;
					String readScope = codeHierarchyConfig.getReadScope();
					if(readScope!=null&&readScope.trim().length()>0) {
						canRead = webAdeAuthentication.hasAuthority(readScope);
					}
				
					if(canRead) {
					
						try {
							
							CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();
							
							if(dao==null) {
								dao = this.codeHierarchyDao;
							}
							
							if(dao==null) {
								logger.error("No codeHierarchyDao has been configured for "+codeHierarchyConfig.getCodeHierarchyTableName()+".");
							}
							
							dtos.add(dao.fetch(codeHierarchyConfig, effectiveAsOfDate));
							
						} catch(IllegalArgumentException e) {
							// do nothing
						}
					}
				}
			} else {
				
				for(CodeHierarchyConfig codeHierarchyConfig:this.codeHierarchyConfigs) {
					
					boolean canRead = true;
					String readScope = codeHierarchyConfig.getReadScope();
					if(readScope!=null&&readScope.trim().length()>0) {
						canRead = webAdeAuthentication.hasAuthority(readScope);
					}
					
					if(canRead) {
						
						CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();
						
						if(dao==null) {
							dao = this.codeHierarchyDao;
						}
						
						if(dao==null) {
							logger.error("No codeHierarchyDao has been configured for "+codeHierarchyConfig.getCodeHierarchyTableName()+".");
						}
						
						dtos.add(dao.fetch(codeHierarchyConfig, effectiveAsOfDate));
					}
				}
			}

			results = this.codeHierarchyFactory.getCodeHierarchys(effectiveAsOfDate, codeHierarchyName, dtos, context);

		} catch (DaoException e) {
			
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">getCodeHierarchys: " + results);
		return results;
	}


	@Override
	public CodeHierarchy getCodeHierarchy(
			String codeHierarchyName, 
			LocalDate effectiveAsOfDate, 
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication)
			throws ServiceException, ForbiddenException, NotFoundException {
		logger.debug("<getCodeHierarchy");

		CodeHierarchy result = null;
		
		if(codeHierarchyConfigs==null||codeHierarchyConfigs.isEmpty()) {
			logger.warn("No codeHierarchys have been configured.");
		}

		try {
			
			CodeHierarchyConfig codeHierarchyConfig = null;
			for(CodeHierarchyConfig tmp:this.codeHierarchyConfigs) {
				if(tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
					codeHierarchyConfig = tmp;
					break;
				}
			}
			
			if(codeHierarchyConfig==null) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			boolean canRead = true;
			String readScope = codeHierarchyConfig.getReadScope();
			if(readScope!=null&&readScope.trim().length()>0) {
				canRead = webAdeAuthentication.hasAuthority(readScope);
			}
			
			if(!canRead) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();
			
			if(dao==null) {
				dao = this.codeHierarchyDao;
			}
			
			if(dao==null) {
				logger.error("No codeHierarchyDao has been configured for "+codeHierarchyConfig.getCodeHierarchyTableName()+".");
			}
			
			CodeHierarchyDto dto = dao.fetch(codeHierarchyConfig, effectiveAsOfDate);

			if (dto == null) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			boolean canUpdate = false;
			String updateScope = codeHierarchyConfig.getUpdateScope();
			if(updateScope!=null&&updateScope.trim().length()>0) {
				canUpdate = webAdeAuthentication.hasAuthority(updateScope);
			}

			result = this.codeHierarchyFactory.getCodeHierarchy(dto, effectiveAsOfDate, canUpdate, context);
			
		} catch (DaoException e) {
			
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">getCodeHierarchy " + result.getCodeHierarchyName());
		return result;
	}
	
	@Override
	public CodeHierarchy updateCodeHierarchy(
			String codeHierarchyName,
			String optimisticLock,
			CodeHierarchy codeHierarchy,
			FactoryContext context, 
			WebAdeAuthentication webAdeAuthentication)
			throws ServiceException, NotFoundException, ForbiddenException, ConflictException, ValidationFailureException {
		logger.debug("<updateCodeHierarchy");

		CodeHierarchy result = null;
		
		if(codeHierarchyConfigs==null||codeHierarchyConfigs.isEmpty()) {
			logger.warn("No codeHierarchys have been configured.");
		}

		try {
			
			CodeHierarchyConfig codeHierarchyConfig = null;
			for(CodeHierarchyConfig tmp:this.codeHierarchyConfigs) {
				if(tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
					codeHierarchyConfig = tmp;
					break;
				}
			}
			
			if(codeHierarchyConfig==null) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			boolean canRead = true;
			String readScope = codeHierarchyConfig.getReadScope();
			if(readScope!=null&&readScope.trim().length()>0) {
				canRead = webAdeAuthentication.hasAuthority(readScope);
			}
			
			if(!canRead) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();
			
			if(dao==null) {
				dao = this.codeHierarchyDao;
			}
			
			if(dao==null) {
				logger.error("No codeHierarchyDao has been configured for "+codeHierarchyConfig.getCodeHierarchyTableName()+".");
			}
			
			CodeHierarchyDto dto = dao.fetch(codeHierarchyConfig, null);

			if (dto == null) {
				throw new NotFoundException("Did not find the CodeHierarchy: "+codeHierarchyName);
			}
			
			List<Message> errors = new ArrayList<Message>();
			errors.addAll(this.codeValidator.validateUpdateCodeHierarchy(codeHierarchy));
			
			if(!errors.isEmpty()) {
				throw new ValidationFailureException(errors);
			}
			
			applyModel(codeHierarchy, dto);
			
			dao.update(codeHierarchyConfig, dto, optimisticLock, webAdeAuthentication.getUserId());

			dto = dao.fetch(codeHierarchyConfig, null);
			
			boolean canUpdate = false;
			String updateScope = codeHierarchyConfig.getUpdateScope();
			if(updateScope!=null&&updateScope.trim().length()>0) {
				canUpdate = webAdeAuthentication.hasAuthority(updateScope);
			}

			result = this.codeHierarchyFactory.getCodeHierarchy(dto, null, canUpdate, context);

		} catch (OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException("DAO threw an exception", e);
		}

		logger.debug(">updateCodeHierarchy " + result.getCodeHierarchyName());
		return result;
	}


	private static void applyModel(CodeHierarchy codeHierarchy, CodeHierarchyDto dto) {
		logger.debug("<applyModel");
		
		dto.setCodeHierarchyName(codeHierarchy.getCodeHierarchyName());
		
		List<HierarchyDto> hierarchyDtos = new ArrayList<>();
		
		for(Hierarchy hiearchy:codeHierarchy.getHierarchy()) {
			HierarchyDto hierarchyDto = new HierarchyDto();
			
			hierarchyDto.setUpperCode(hiearchy.getUpperCode());
			hierarchyDto.setLowerCode(hiearchy.getLowerCode());
			hierarchyDto.setEffectiveDate(hiearchy.getEffectiveDate());
			hierarchyDto.setExpiryDate(hiearchy.getExpiryDate());
			
			hierarchyDtos.add(hierarchyDto);
		}
		
		dto.setHierarchy(hierarchyDtos);
		
		logger.debug(">applyModel");
	}

	public void setCodeTableDao(CodeTableDao codeTableDao) {
		this.codeTableDao = codeTableDao;
	}

	public void setCodeHierarchyDao(CodeHierarchyDao codeHierarchyDao) {
		this.codeHierarchyDao = codeHierarchyDao;
	}

	public void setCodeTableFactory(CodeTableFactory codeTableFactory) {
		this.codeTableFactory = codeTableFactory;
	}

	public void setCodeHierarchyFactory(CodeHierarchyFactory codeHierarchyFactory) {
		this.codeHierarchyFactory = codeHierarchyFactory;
	}

	public void setCodeValidator(CodeValidator codeValidator) {
		this.codeValidator = codeValidator;
	}

	public void setCodeTableConfigs(List<CodeTableConfig> codeTableConfigs) {
		this.codeTableConfigs = codeTableConfigs;
	}

	public void setCodeHierarchyConfigs(
			List<CodeHierarchyConfig> codeHierarchyConfigs) {
		this.codeHierarchyConfigs = codeHierarchyConfigs;
	}
}
