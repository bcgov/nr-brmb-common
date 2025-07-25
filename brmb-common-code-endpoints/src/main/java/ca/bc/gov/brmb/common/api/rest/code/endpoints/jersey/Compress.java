package ca.bc.gov.brmb.common.api.rest.code.endpoints.jersey;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.ws.rs.NameBinding;

/**
 * Enable GZip compression
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Compress {
}