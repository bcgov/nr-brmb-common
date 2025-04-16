package ca.bc.gov.nrs.wfone.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.bc.gov.nrs.wfone.common.model.Message;
import ca.bc.gov.nrs.wfone.common.model.MessageImpl;

public class MessageBuilder {
	
	private static ObjectMapper mapper = new ObjectMapper();

	private List<String> argList = new ArrayList<>();
	private Map<String,String> argMap = new HashMap<>();
	private String path;
	private String message;
	private String messageTemplate;
	
	public MessageBuilder(String path, String message, String messageTemplate) {

		this.path = path;
		this.message = message;
		this.messageTemplate = messageTemplate;
	}
	
	public void addArg(String key, String value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = value.toString();
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, Long value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = value.toString();
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, Integer value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = value.toString();
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, Double value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = value.toString();
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, LocalDate value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = DateUtils.toLocalDateString(value);
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, LocalDateTime value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = DateUtils.toLocalDateTimeString(value);
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, Instant value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = DateUtils.toInstantString(value);
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public void addArg(String key, LocalTime value) {
		
		String stringValue = null;
		if(value!=null) {
			
			stringValue = DateUtils.toLocalTimeString(value);
		}
		
		argMap.put(key, stringValue);
		argList.add(stringValue);
	}
	
	public Message getMessage() {
		
		try {
			argList.add(mapper.writeValueAsString(argMap));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
		String[] args = argList.toArray(new String[] {}); 
		
		return new MessageImpl(path, message, messageTemplate, args);
	}
}
