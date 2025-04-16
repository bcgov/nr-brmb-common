package ca.bc.gov.nrs.wfone.common.api.rest.code.endpoints.jersey;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * Enable GZip compression
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Compress {
}