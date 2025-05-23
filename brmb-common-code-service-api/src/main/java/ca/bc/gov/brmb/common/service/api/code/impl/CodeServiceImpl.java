package ca.bc.gov.brmb.common.service.api.code.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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
import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyConfig;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeHierarchyDao;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeTableConfig;
import ca.bc.gov.brmb.common.persistence.code.dao.CodeTableDao;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeDto;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeHierarchyDto;
import ca.bc.gov.brmb.common.persistence.code.dto.CodeTableDto;
import ca.bc.gov.brmb.common.persistence.code.dto.HierarchyDto;
import ca.bc.gov.brmb.common.persistence.dao.DaoException;
import ca.bc.gov.brmb.common.persistence.dao.OptimisticLockingFailureDaoException;
import ca.bc.gov.brmb.common.service.api.ConflictException;
import ca.bc.gov.brmb.common.service.api.ForbiddenException;
import ca.bc.gov.brmb.common.service.api.NotFoundException;
import ca.bc.gov.brmb.common.service.api.ServiceException;
import ca.bc.gov.brmb.common.service.api.ValidationFailureException;
import ca.bc.gov.brmb.common.service.api.code.CodeService;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeFactory;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeHierarchyFactory;
import ca.bc.gov.brmb.common.service.api.code.model.factory.CodeTableFactory;
import ca.bc.gov.brmb.common.service.api.code.validation.CodeValidator;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;

public class CodeServiceImpl implements CodeService {

    private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);

    private CodeTableDao codeTableDao;

    private CodeHierarchyDao codeHierarchyDao;

    private CodeFactory codeFactory;

    private CodeTableFactory codeTableFactory;

    private CodeHierarchyFactory codeHierarchyFactory;

    private CodeValidator codeValidator;

    private List<CodeTableConfig> codeTableConfigs = new ArrayList<>();

    private List<CodeHierarchyConfig> codeHierarchyConfigs = new ArrayList<>();

    @Override
    public Code getCode(String codeTableName, String codeName, FactoryContext factoryContext)
            throws ServiceException, NotFoundException {
        logger.debug("<getCode");

        Code result = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto codeTableDto = dao.fetch(codeTableConfig, null);

            if (codeTableDto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeDto codeDto = null;
            for (CodeDto tmp : codeTableDto.getCodes()) {
                if (tmp.getCode().equalsIgnoreCase(codeName)) {
                    codeDto = tmp;
                    break;
                }
            }

            if (codeDto == null) {
                throw new NotFoundException("Did not find the Code: " + codeName);
            }

            result = this.codeFactory.getCode(codeTableDto, codeDto, factoryContext);

        } catch (DaoException e) {

            throw new ServiceException("DAO threw an exception", e);
        }

        logger.debug(">getCode " + result.getCode());
        return result;
    }

    @Override
    public Code createCode(String codeTableName, String optimisticLock, Code code, FactoryContext factoryContext)
            throws ServiceException, NotFoundException, ConflictException {
        logger.debug("<createCode");

        Code result = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto codeTableDto = dao.fetch(codeTableConfig, null);

            if (codeTableDto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            Iterator<CodeDto> codeIterator = codeTableDto.getCodes().iterator();
            while (codeIterator.hasNext()) {
                CodeDto tmp = codeIterator.next();
                if (tmp.getCode().equalsIgnoreCase(code.getCode())) {
                    throw new ServiceException("Code already exists: " + code.getCode());
                }
            }

            CodeDto codeDto = new CodeDto();
            codeDto.setCode(code.getCode());
            codeDto.setDescription(code.getDescription());
            codeDto.setDisplayOrder(code.getDisplayOrder());
            codeDto.setEffectiveDate(code.getEffectiveDate());
            codeDto.setExpiryDate(code.getExpiryDate());
            codeTableDto.getCodes().add(codeDto);
            dao.update(codeTableConfig, codeTableDto, optimisticLock, "UserId");

            result = this.codeFactory.getCode(codeTableDto, codeDto, factoryContext);

        } catch (OptimisticLockingFailureDaoException e) {
            throw new ConflictException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("DAO threw an exception", e);
        }

        logger.debug(">createCode " + result.getCode());
        return result;
    }

    @Override
    public void deleteCode(String codeTableName, String optimisticLock, String codeName, FactoryContext factoryContext)
            throws ServiceException, NotFoundException, ConflictException {
        logger.debug("<deleteCode");

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto codeTableDto = dao.fetch(codeTableConfig, null);

            if (codeTableDto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            boolean found = false;
            Iterator<CodeDto> codeIterator = codeTableDto.getCodes().iterator();
            while (codeIterator.hasNext()) {
                CodeDto tmp = codeIterator.next();
                if (tmp.getCode().equalsIgnoreCase(codeName)) {
                    found = true;
                    codeIterator.remove();
                }
            }

            if (!found) {
                throw new NotFoundException("Did not find the Code: " + codeName);
            }

            dao.update(codeTableConfig, codeTableDto, optimisticLock, "UserId");

        } catch (OptimisticLockingFailureDaoException e) {
            throw new ConflictException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("DAO threw an exception", e);
        }

        logger.debug(">deleteCode");
    }

    @Override
    public Code updateCode(String codeTableName, String optimisticLock, Code code, FactoryContext factoryContext)
            throws ServiceException, NotFoundException, ConflictException {
        logger.debug("<updateCode");

        Code result = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto codeTableDto = dao.fetch(codeTableConfig, null);

            if (codeTableDto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeDto codeDto = null;
            for (CodeDto tmp : codeTableDto.getCodes()) {
                if (tmp.getCode().equalsIgnoreCase(code.getCode())) {
                    codeDto = tmp;
                    break;
                }
            }

            if (codeDto == null) {
                throw new NotFoundException("Did not find the Code: " + code.getCode());
            }

            codeDto.setDescription(code.getDescription());
            codeDto.setDisplayOrder(code.getDisplayOrder());
            codeDto.setEffectiveDate(code.getEffectiveDate());
            codeDto.setExpiryDate(code.getExpiryDate());
            dao.update(codeTableConfig, codeTableDto, optimisticLock, "UserId");

            result = this.codeFactory.getCode(codeTableDto, codeDto, factoryContext);

        } catch (OptimisticLockingFailureDaoException e) {
            throw new ConflictException(e.getMessage());
        } catch (DaoException e) {
            throw new ServiceException("DAO threw an exception", e);
        }

        logger.debug(">updateCode " + result.getCode());
        return result;
    }

    @Override
    public CodeTableList<? extends CodeTable<? extends Code>> getCodeTableList(
            LocalDate effectiveAsOfDate,
            String codeTableName,
            FactoryContext context) throws ServiceException {
        logger.debug("<getCodeTables");

        CodeTableList<? extends CodeTable<? extends Code>> results = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            List<CodeTableDto> dtos = new ArrayList<CodeTableDto>();

            if (codeTableName != null && codeTableName.trim().length() > 0) {

                CodeTableConfig codeTableConfig = null;
                for (CodeTableConfig tmp : this.codeTableConfigs) {
                    if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                        codeTableConfig = tmp;
                        break;
                    }
                }

                if (codeTableConfig != null) {

                    try {

                        CodeTableDao dao = codeTableConfig.getCodeTableDao();

                        if (dao == null) {
                            dao = this.codeTableDao;
                        }

                        if (dao == null) {
                            logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName()
                                    + ".");
                        }

                        dtos.add(dao.fetch(codeTableConfig, effectiveAsOfDate));

                    } catch (IllegalArgumentException e) {
                        // do nothing
                    }
                }
            } else {

                for (CodeTableConfig codeTableConfig : this.codeTableConfigs) {

                    CodeTableDao dao = codeTableConfig.getCodeTableDao();

                    if (dao == null) {
                        dao = this.codeTableDao;
                    }

                    if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
                        logger.error(
                                "No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
                    }

                    dtos.add(dao.fetch(codeTableConfig, effectiveAsOfDate));
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
            FactoryContext context)
            throws ServiceException, ForbiddenException, NotFoundException {
        logger.debug("<getCodeTable");

        CodeTable<? extends Code> result = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto dto = dao.fetch(codeTableConfig, effectiveAsOfDate);

            if (dto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            result = this.codeTableFactory.getCodeTable(dto, effectiveAsOfDate, true, context);

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
            FactoryContext context)
            throws ServiceException, NotFoundException, ForbiddenException, ConflictException,
            ValidationFailureException {
        logger.debug("<updateCodeTable");

        CodeTable<? extends Code> result = null;

        if (codeTableConfigs == null || codeTableConfigs.isEmpty()) {
            logger.warn("No codeTables have been configured.");
        }

        try {

            CodeTableConfig codeTableConfig = null;
            for (CodeTableConfig tmp : this.codeTableConfigs) {
                if (tmp.getCodeTableName().equalsIgnoreCase(codeTableName)) {
                    codeTableConfig = tmp;
                    break;
                }
            }

            if (codeTableConfig == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            CodeTableDao dao = codeTableConfig.getCodeTableDao();

            if (dao == null) {
                dao = this.codeTableDao;
            }

            if (dao == null) {
                logger.error("No codeTableDao has been configured for " + codeTableConfig.getCodeTableName() + ".");
            }

            CodeTableDto dto = dao.fetch(codeTableConfig, null);

            if (dto == null) {
                throw new NotFoundException("Did not find the CodeTable: " + codeTableName);
            }

            List<Message> errors = new ArrayList<>();
            errors.addAll(this.codeValidator.validateUpdateCodeTable(codeTable));

            if (!errors.isEmpty()) {
                throw new ValidationFailureException(errors);
            }

            applyModel(codeTable, dto);

            dao.update(codeTableConfig, dto, optimisticLock, "UserId");

            dto = dao.fetch(codeTableConfig, null);

            result = this.codeTableFactory.getCodeTable(dto, null, true, context);

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

        for (Code code : codeTable.getCodes()) {
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
            FactoryContext context) throws ServiceException {
        logger.debug("<getCodeHierarchys");

        CodeHierarchyList<? extends CodeHierarchy> results = null;

        if (codeHierarchyConfigs == null || codeHierarchyConfigs.isEmpty()) {
            logger.warn("No codeHierarchys have been configured.");
        }

        try {

            List<CodeHierarchyDto> dtos = new ArrayList<CodeHierarchyDto>();

            if (codeHierarchyName != null && codeHierarchyName.trim().length() > 0) {

                CodeHierarchyConfig codeHierarchyConfig = null;
                for (CodeHierarchyConfig tmp : this.codeHierarchyConfigs) {
                    if (tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
                        codeHierarchyConfig = tmp;
                        break;
                    }
                }

                if (codeHierarchyConfig != null) {

                    try {

                        CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();

                        if (dao == null) {
                            dao = this.codeHierarchyDao;
                        }

                        if (dao == null) {
                            logger.error("No codeHierarchyDao has been configured for "
                                    + codeHierarchyConfig.getCodeHierarchyTableName() + ".");
                        }

                        dtos.add(dao.fetch(codeHierarchyConfig, effectiveAsOfDate));

                    } catch (IllegalArgumentException e) {
                        // do nothing
                    }
                }
            } else {

                for (CodeHierarchyConfig codeHierarchyConfig : this.codeHierarchyConfigs) {

                    CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();

                    if (dao == null) {
                        dao = this.codeHierarchyDao;
                    }

                    if (dao == null) {
                        logger.error("No codeHierarchyDao has been configured for "
                                + codeHierarchyConfig.getCodeHierarchyTableName() + ".");
                    }

                    dtos.add(dao.fetch(codeHierarchyConfig, effectiveAsOfDate));
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
            FactoryContext context)
            throws ServiceException, ForbiddenException, NotFoundException {
        logger.debug("<getCodeHierarchy");

        CodeHierarchy result = null;

        if (codeHierarchyConfigs == null || codeHierarchyConfigs.isEmpty()) {
            logger.warn("No codeHierarchys have been configured.");
        }

        try {

            CodeHierarchyConfig codeHierarchyConfig = null;
            for (CodeHierarchyConfig tmp : this.codeHierarchyConfigs) {
                if (tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
                    codeHierarchyConfig = tmp;
                    break;
                }
            }

            if (codeHierarchyConfig == null) {
                throw new NotFoundException("Did not find the CodeHierarchy: " + codeHierarchyName);
            }

            CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();

            if (dao == null) {
                dao = this.codeHierarchyDao;
            }

            if (dao == null) {
                logger.error("No codeHierarchyDao has been configured for "
                        + codeHierarchyConfig.getCodeHierarchyTableName() + ".");
            }

            CodeHierarchyDto dto = dao.fetch(codeHierarchyConfig, effectiveAsOfDate);

            if (dto == null) {
                throw new NotFoundException("Did not find the CodeHierarchy: " + codeHierarchyName);
            }

            result = this.codeHierarchyFactory.getCodeHierarchy(dto, effectiveAsOfDate, true, context);

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
            FactoryContext context)
            throws ServiceException, NotFoundException, ForbiddenException, ConflictException,
            ValidationFailureException {
        logger.debug("<updateCodeHierarchy");

        CodeHierarchy result = null;

        if (codeHierarchyConfigs == null || codeHierarchyConfigs.isEmpty()) {
            logger.warn("No codeHierarchys have been configured.");
        }

        try {

            CodeHierarchyConfig codeHierarchyConfig = null;
            for (CodeHierarchyConfig tmp : this.codeHierarchyConfigs) {
                if (tmp.getCodeHierarchyTableName().equalsIgnoreCase(codeHierarchyName)) {
                    codeHierarchyConfig = tmp;
                    break;
                }
            }

            if (codeHierarchyConfig == null) {
                throw new NotFoundException("Did not find the CodeHierarchy: " + codeHierarchyName);
            }

            CodeHierarchyDao dao = codeHierarchyConfig.getCodeHierarchyDao();

            if (dao == null) {
                dao = this.codeHierarchyDao;
            }

            if (dao == null) {
                logger.error("No codeHierarchyDao has been configured for "
                        + codeHierarchyConfig.getCodeHierarchyTableName() + ".");
            }

            CodeHierarchyDto dto = dao.fetch(codeHierarchyConfig, null);

            if (dto == null) {
                throw new NotFoundException("Did not find the CodeHierarchy: " + codeHierarchyName);
            }

            List<Message> errors = new ArrayList<Message>();
            errors.addAll(this.codeValidator.validateUpdateCodeHierarchy(codeHierarchy));

            if (!errors.isEmpty()) {
                throw new ValidationFailureException(errors);
            }

            applyModel(codeHierarchy, dto);

            dao.update(codeHierarchyConfig, dto, optimisticLock, "UserId");

            dto = dao.fetch(codeHierarchyConfig, null);

            result = this.codeHierarchyFactory.getCodeHierarchy(dto, null, true, context);

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

        for (Hierarchy hiearchy : codeHierarchy.getHierarchy()) {
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

    public void setCodeFactory(CodeFactory codeFactory) {
        this.codeFactory = codeFactory;
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
