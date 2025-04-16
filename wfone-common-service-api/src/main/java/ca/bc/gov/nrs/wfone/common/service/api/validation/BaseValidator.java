package ca.bc.gov.nrs.wfone.common.service.api.validation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.TraversableResolver;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.bc.gov.brmb.common.model.Message;
import ca.bc.gov.brmb.common.model.MessageImpl;

public abstract class BaseValidator
{
	private static final Logger logger = LoggerFactory.getLogger(BaseValidator.class);

	protected ResourceBundleMessageSource messageSource;

	private Validator validator;

	protected BaseValidator()
	{
		logger.debug("<BaseValidator"); //$NON-NLS-1$

		Configuration<?> config = Validation.byProvider(HibernateValidator.class).configure();

		config.traversableResolver(new TraversableResolver()
		{
			@Override
			public boolean isReachable(Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject,
					ElementType elementType)
			{
				return true;
			}

			@Override
			public boolean isCascadable(Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject,
					ElementType elementType)
			{
				return true;
			}
		});

		ValidatorFactory factory = config.buildValidatorFactory();
		this.validator = factory.getValidator();

		logger.debug(">BaseValidator"); //$NON-NLS-1$
	}
	
	private static Object getModelPorxy(Object modelObject, Class<?>[] groups) {
		Object proxy = Proxy.newProxyInstance(
				BaseValidator.class.getClassLoader(), groups,
				new ValidationInvocationHandler(modelObject));
		return proxy;
	}

	protected List<Message> validate(Object model, Class<?>... groups)
	{
		List<Message> results = new ArrayList<Message>();
		
		Object proxy = getModelPorxy(model, groups);

		Set<ConstraintViolation<Object>> constraintViolations = this.validator.validate(proxy, groups);

		for (ConstraintViolation<Object> constraintViolation: constraintViolations)
		{
			String path = constraintViolation.getPropertyPath().toString();
			logger.debug("path=" + path); //$NON-NLS-1$

			String message = constraintViolation.getMessage();
			logger.debug("message=" + message); //$NON-NLS-1$

			String messageTemplate = constraintViolation.getMessageTemplate();
			logger.debug("message template=" + constraintViolation.getMessageTemplate()); //$NON-NLS-1$

			Object invalidValue = constraintViolation.getInvalidValue();
			logger.debug("invalid value=" + invalidValue); //$NON-NLS-1$

			ConstraintDescriptor<?> constraintDescriptor = constraintViolation.getConstraintDescriptor();

			String[] args = getArgs(messageTemplate, invalidValue, constraintDescriptor);

			logger.debug("messageSource="+messageSource);
			String text = this.messageSource.getMessage(message, args, messageTemplate, Locale.getDefault());

			results.add(new MessageImpl(path, text, messageTemplate, args));
		}

		return results;
	}

	protected String[] getArgs(String messageTemplate, Object invalidValue, ConstraintDescriptor<?> constraintDescriptor)
	{
		logger.debug("<getArgs"); //$NON-NLS-1$
		Object[] args = null;

		Annotation annotation = constraintDescriptor.getAnnotation();

		Map<String, Object> attributes = constraintDescriptor.getAttributes();

		if (AssertFalse.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (AssertTrue.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (DecimalMax.class.equals(annotation.annotationType()))
		{
			Object value = attributes.get("value"); //$NON-NLS-1$
			args = new Object[] { invalidValue, value };
		}
		else if (DecimalMin.class.equals(annotation.annotationType()))
		{
			Object value = attributes.get("value"); //$NON-NLS-1$
			args = new Object[] { invalidValue, value };
		}
		else if (Digits.class.equals(annotation.annotationType()))
		{
			Object integer = attributes.get("integer"); //$NON-NLS-1$
			Object fraction = attributes.get("fraction"); //$NON-NLS-1$
			args = new Object[] { invalidValue, integer, fraction };
		}
		else if (Future.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Max.class.equals(annotation.annotationType()))
		{
			Object value = attributes.get("value"); //$NON-NLS-1$
			args = new Object[] { invalidValue, value };
		}
		else if (Min.class.equals(annotation.annotationType()))
		{
			Object value = attributes.get("value"); //$NON-NLS-1$
			args = new Object[] { invalidValue, value };
		}
		else if (NotNull.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Null.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Past.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Pattern.class.equals(annotation.annotationType()))
		{
			Object regexp = attributes.get("regexp"); //$NON-NLS-1$
			args = new Object[] { invalidValue, regexp };
		}
		else if (Size.class.equals(annotation.annotationType()))
		{
			Object min = attributes.get("min"); //$NON-NLS-1$
			Object max = attributes.get("max"); //$NON-NLS-1$
			args = new Object[] { invalidValue, min, max };
		}
		else if (Valid.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (CreditCardNumber.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Email.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Length.class.equals(annotation.annotationType()))
		{
			Object min = attributes.get("min"); //$NON-NLS-1$
			Object max = attributes.get("max"); //$NON-NLS-1$
			args = new Object[] { invalidValue, min, max };
		}
		else if (NotBlank.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (NotEmpty.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (Range.class.equals(annotation.annotationType()))
		{
			Object min = attributes.get("min"); //$NON-NLS-1$
			Object max = attributes.get("max"); //$NON-NLS-1$
			args = new Object[] { invalidValue, min, max };
		}
		else if (ScriptAssert.class.equals(annotation.annotationType()))
		{
			args = new Object[] { invalidValue };
		}
		else if (URL.class.equals(annotation.annotationType()))
		{
			Object protocol = attributes.get("protocol"); //$NON-NLS-1$
			Object host = attributes.get("host"); //$NON-NLS-1$
			Object port = attributes.get("port"); //$NON-NLS-1$
			args = new Object[] { invalidValue, protocol, host, port };
		}
		else
		{
			args = new Object[] { invalidValue };
			logger.warn("Annotation not supported: " + annotation.annotationType().getName()); //$NON-NLS-1$
		}

		String[] stringArgs = new String[args.length];
		for(int i=0;i<args.length;++i) {
			if(args[i]!=null) {
				stringArgs[i] = args[i].toString();
			}
		}
		
		logger.debug(">getArgs"); //$NON-NLS-1$
		return stringArgs;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource)
	{
		this.messageSource = messageSource;
	}
}
