package ca.bc.gov.nrs.wfone.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {

	public static LocalDate toLocalDate(long epochMilli, ZoneId zoneId) {
		LocalDate result = null;
			
		result = Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDate();
		
		return result;
	}

	public static LocalDate toLocalDate(long epochMilli) {
		
		return toLocalDate(epochMilli, ZoneOffset.systemDefault());
	}

	public static LocalDate toLocalDate(String value) {
		LocalDate result = null;
		
		if(value!=null&&value.trim().length()>0) {
			
			result = LocalDate.parse(value);
		}
		
		return result;
	}

	public static LocalTime toLocalTime(String value) {
		LocalTime result = null;
		
		if(value!=null&&value.trim().length()>0) {
			
			result = LocalTime.parse(value);
		}
		
		return result;
	}

	public static String toInstantString(ZonedDateTime value, ZoneId zoneId) {
		String result = null;
		
		if(value!=null) {
			
			result = value.withZoneSameInstant(zoneId).format(DateTimeFormatter.ISO_INSTANT);
		}
		
		return result;
	}

	public static String toInstantString(ZonedDateTime value) {
		
		return toInstantString(value, ZoneOffset.systemDefault());
	}

	public static String toInstantString(Instant value) {
		String result = null;
		
		if(value!=null) {
			
			result = DateTimeFormatter.ISO_INSTANT.format(value);
		}
		
		return result;
	}

	public static String toLocalDateString(LocalDate value) {
		String result = null;
		
		if(value!=null) {
			
			result = value.format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		
		return result;
	}

	public static String toLocalTimeString(LocalTime value) {
		String result = null;
		
		if(value!=null) {
			
			result = value.format(DateTimeFormatter.ISO_LOCAL_TIME);
		}
		
		return result;
	}
	
	public static Instant toInstant(String value) {
		Instant result = null;
		
		if(value!=null&&value.trim().length()>0) {
			
			try {
				
				result =  Instant.parse(value);
			} catch (DateTimeParseException e1) {
					
				try {
				
					long epochMillis = Long.parseLong(value);
					
					result =  Instant.ofEpochMilli(epochMillis);
				} catch(NumberFormatException e3) {
					
					throw e1;
				}
			}
		}
		
		return result;
	}
	
	public static Instant[] toInstantArray(String[] values) {
		
		List<Instant> instantList = new ArrayList<Instant>();
		
		if (values!=null) {
			for (String s:values) {
				instantList.add( toInstant(s) );
			}
		}
			
		return (Instant[]) instantList.toArray();	
	}

    public static LocalDate toLocalDate(Date value, ZoneId zoneId) {
        LocalDate result = null;
        
        if(value!=null) {
            
            result = value.toInstant().atZone(zoneId).toLocalDate();
        }
        
        return result;
    }    
    
    public static LocalDate toLocalDate(Date value) {

        return toLocalDate(value, ZoneOffset.systemDefault());
    }

	public static Date toDate(LocalDate value, ZoneId zoneId) {
		Date result = null;
		
		if(value!=null) {
			
			result = Date.from(value.atStartOfDay(zoneId).toInstant());
		}
		
		return result;
	}

	public static Date toDate(LocalDate value) {
		
		return toDate(value, ZoneOffset.systemDefault());
	}

	public static Date toDate(ZonedDateTime value) {
		Date result = null;
		
		if(value!=null) {
			
			result = Date.from(value.toInstant());
		}
		
		return result;
	}

	public static ZonedDateTime toZonedDateTime(Date value, ZoneId zoneId) {
		ZonedDateTime result = null;
		
		if(value!=null) {
			
			result = value.toInstant().atZone(zoneId);
		}
		
		return result;
	}

	public static ZonedDateTime toZonedDateTime(Date value) {
		
		return toZonedDateTime(value, ZoneOffset.systemDefault());
	}

	public static String toInstantString(Date value, ZoneId zoneId) {
		String result = null;
		
		if(value!=null) {
			
			result = toZonedDateTime(value, zoneId).format(DateTimeFormatter.ISO_INSTANT);
		}
		
		return result;
	}

	public static String toInstantString(Date value) {
		
		return toInstantString(value, ZoneOffset.systemDefault());
	}

	public static String toLocalDateString(Date value, ZoneId zoneId) {
		String result = null;
		
		if(value!=null) {
			
			result = toLocalDate(value, zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		
		return result;
	}

	public static String toLocalDateString(Date value) {
		
		return toLocalDateString(value, ZoneOffset.systemDefault());
	}

	public static String toLocalDateTimeString(LocalDateTime value) {
		String result = null;
		
		if(value!=null) {
			
			result = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		}
		
		return result;
	}

}

