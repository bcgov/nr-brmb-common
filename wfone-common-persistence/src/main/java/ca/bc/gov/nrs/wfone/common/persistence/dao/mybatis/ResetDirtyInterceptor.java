package ca.bc.gov.nrs.wfone.common.persistence.dao.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import ca.bc.gov.nrs.wfone.common.persistence.dto.BaseDto;


@Intercepts(
		{
			@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
            @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
		})
public class ResetDirtyInterceptor implements Interceptor {
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		Object result = invocation.proceed();
		
		if(result!=null) {
			
			if(result instanceof Collection<?>) {
				
				resetCollection((Collection<?>) result);
				
			} else if(result instanceof BaseDto) {
				
				resetDirty((BaseDto<?>) result);
			}
		}
		
		return result;
	}
	
	private static void resetCollection(Collection<?> values) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		for(Object object:values) {
			
			if(object instanceof BaseDto) {
				
				resetDirty((BaseDto<?>) object);
			}
		}
		
	}
	
	private static void resetDirty(BaseDto<?> baseDto) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		baseDto.resetDirty();
		
		Method[] methods = baseDto.getClass().getDeclaredMethods();
		
		for(Method method:methods) {
			
			String methodName = method.getName();
			int modifiers = method.getModifiers();
			int parameterCount = method.getParameterCount();

			if(Modifier.isPublic(modifiers)&&!Modifier.isStatic(modifiers)&&methodName.startsWith("get")&&parameterCount==0) {
			
				Object value = method.invoke(baseDto, new Object[]{});
				
				if(value instanceof BaseDto) {
					
					resetDirty((BaseDto<?>) value);
				} else if(value instanceof Collection) {
					
					resetCollection((Collection<?>) value);
				}
			}
		}
	}

	@Override
	public Object plugin(Object target) {
		
		Object result = Plugin.wrap(target, this);
		
		return result;
	}

	@Override
	public void setProperties(Properties properties) {
		
		// do nothing
	}

}
