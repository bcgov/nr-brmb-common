package ca.bc.gov.brmb.common.service.api.code.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.METHOD })
@Retention(RUNTIME)
@Constraint(validatedBy = CodeValueValidator.class)
@Documented
public @interface CodeValue {
    
	String codeTableName();
	
	String message();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
}
