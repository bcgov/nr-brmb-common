package ca.bc.gov.brmb.common.persistence.utils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;

import com.vividsolutions.jts.geom.Geometry;

public class DtoUtils {
	
	private Logger logger;
	
	public DtoUtils() {
		
	}
	
	public DtoUtils(Logger logger) {
		
		this.logger = logger;
	}
	
	public boolean equals(String propertyName, String v1, String v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Boolean v1, Boolean v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Integer v1, Integer v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Short v1, Short v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Long v1, Long v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Geometry v1, Geometry v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Double v1, Double v2, int precision) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			double multiplier = (int) Math.pow(10, precision);

			long l1 = Math.round(v1.doubleValue()*multiplier);
			long l2 = Math.round(v2.doubleValue()*multiplier);
			
			long diff = Math.abs(l1 - l2);
			
			result = (diff <= 0);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, LocalDateTime v1, LocalDateTime v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.isEqual(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, LocalDateTime v1, LocalDateTime v2, ChronoUnit unit) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.truncatedTo(unit).isEqual(v2.truncatedTo(unit));
		} else {
			
			result = false;
		}
		
		if(!result) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}


	public boolean equals(String propertyName, Instant v1, Instant v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	
	public boolean equals(String propertyName, LocalTime v1, LocalTime v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {

			result = v1.equals(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Instant v1, Instant v2, ChronoUnit unit) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.truncatedTo(unit).toEpochMilli()==v2.truncatedTo(unit).toEpochMilli();
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, LocalDate v1, LocalDate v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null) {
			
			result = v1.isEqual(v2);
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}
	
	public boolean equals(String propertyName, Object v1, Object v2) {
		boolean result = false;
		
		if(v1==null&&v2==null) {
			
			result = true;
		} else if(v1!=null&&v2!=null&&v1.getClass().equals(v1.getClass())) {
			
			try {
				String methodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				logger.debug("methodName="+methodName);
				
				Method method = v1.getClass().getMethod(methodName);
	
				Object value1 = method.invoke(v1);
				Object value2 = method.invoke(v2);
				
				result = value1.equals(value2);
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			} 
		} else {
			
			result = false;
		}
		
		if(!result&&logger!=null) {
			logger.info(propertyName+" is dirty. old:"+v2+" new:"+v1);
		}
		
		return result;
	}

}
