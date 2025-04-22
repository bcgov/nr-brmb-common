package ca.bc.gov.brmb.common.api.rest.code.endpoints.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.brmb.common.api.rest.code.endpoints.CodeEndpoints;
import ca.bc.gov.brmb.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.brmb.common.rest.resource.CodeRsrc;
import ca.bc.gov.brmb.common.service.api.NotFoundException;
import ca.bc.gov.brmb.common.service.api.code.CodeService;

public class CodeEndpointsImpl extends BaseEndpointsImpl implements CodeEndpoints {

    private static final Logger logger = LoggerFactory.getLogger(CodeEndpointsImpl.class);

    @Autowired
    private CodeService service;

    @Override
    public Response getCode(String codeTableName, String codeName) {
        logger.debug("<getCode");
        Response response = null;

        logRequest();

        try {

            CodeRsrc result = (CodeRsrc) service.getCode(codeTableName, codeName, getFactoryContext());

            response = Response.ok(result).tag(result.getUnquotedETag()).build();
        } catch (NotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();

        } catch (Throwable t) {
            response = getInternalServerErrorResponse(t);
        }

        logResponse(response);

        logger.debug(">getCode " + response);
        return response;
    }

    @Override
    public Response createCode(String codeTableName, CodeRsrc codeRsrc) {
        logger.debug("<createCode");

        Response response = null;

        logRequest();

        try {

            String optimisticLock = getIfMatchHeader();

            CodeRsrc result = (CodeRsrc) service.createCode(codeTableName, optimisticLock, codeRsrc,
                    getFactoryContext());

            response = Response.status(Status.CREATED).entity(result).tag(result.getUnquotedETag()).build();
        } catch (NotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Throwable t) {
            response = getInternalServerErrorResponse(t);
        }

        logResponse(response);

        logger.debug(">createCode " + response.getStatus());
        return response;
    }

    @Override
    public Response deleteCode(String codeTableName, String codeName) {
        logger.debug("<deleteCode");

        Response response = null;

        logRequest();

        try {

            String optimisticLock = getIfMatchHeader();

            service.deleteCode(codeTableName, optimisticLock, codeName, getFactoryContext());

            response = Response.status(Status.NO_CONTENT).build();
        } catch (NotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Throwable t) {
            response = getInternalServerErrorResponse(t);
        }

        logResponse(response);

        logger.debug(">deleteCode " + response.getStatus());
        return response;
    }

    @Override
    public Response updateCode(String codeTableName, CodeRsrc codeRsrc) {
        logger.debug("<updateCode");

        Response response = null;

        logRequest();

        try {

            String optimisticLock = getIfMatchHeader();

            CodeRsrc result = (CodeRsrc) service.updateCode(codeTableName, optimisticLock, codeRsrc,
                    getFactoryContext());

            response = Response.ok(result).tag(result.getUnquotedETag()).build();
        } catch (NotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Throwable t) {
            response = getInternalServerErrorResponse(t);
        }

        logResponse(response);

        logger.debug(">updateCode " + response.getStatus());
        return response;
    }
}
