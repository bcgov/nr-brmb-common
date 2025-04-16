package ca.bc.gov.nrs.wfone.common.service.api.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ValidationInvocationHandler implements InvocationHandler {
	
	private Object modelObject;

	public ValidationInvocationHandler(Object modelObject) {
		this.modelObject = modelObject;
	}

	@Override
	public Object invoke(Object proxy, Method method,
			Object[] args) throws Throwable {

		Object result = null;

		Method proxyMethod = null;
		try {
			proxyMethod = modelObject.getClass().getMethod(
					method.getName(),
					method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"The model object being validated must have the same method signatures as the contraints interface.",
					e);
		}

		result = proxyMethod.invoke(modelObject, args);


		return result;
	}

}
