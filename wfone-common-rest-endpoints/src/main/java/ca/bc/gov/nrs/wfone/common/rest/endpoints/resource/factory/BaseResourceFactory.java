package ca.bc.gov.nrs.wfone.common.rest.endpoints.resource.factory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import ca.bc.gov.brmb.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.brmb.common.service.api.model.factory.FactoryException;
import ca.bc.gov.nrs.wfone.common.utils.ByteUtils;
import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public abstract class BaseResourceFactory {

	protected static int getResourceDepth(FactoryContext context) {
		int result = Integer.MAX_VALUE;

		if (context instanceof ResourceFactoryContext) {
			result = ((ResourceFactoryContext) context).getResourceDepth();
		} else {
			result = Integer.MAX_VALUE;
		}

		return result;
	}

	protected static URI getBaseURI(FactoryContext context) {
		URI result = null;
		try {

			if (context instanceof ResourceFactoryContext) {
				result = ((ResourceFactoryContext) context).getBaseURI();
			} else {
				result = new URI("http://default.uri");
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	protected static String toString(String[] values) {
		String result = null;

		if (values != null) {
			result = "";
			for(int i=0;i<values.length;++i) {
				result += values[i];
				if(i<values.length-1) {
					result += ",";
				}
			}
		}

		return result;
	}
	
	protected static String toString(Long[] values) {
		String result = null;

		if (values != null) {
			result = "";
			for(int i=0;i<values.length;++i) {
				result += String.valueOf(values[i]);
				if(i<values.length-1) {
					result += ",";
				}
			}
		}

		return result;
	}

	protected static String toString(Integer[] values) {
		String result = null;

		if (values != null) {
			result = "";
			for(int i=0;i<values.length;++i) {
				result += String.valueOf(values[i]);
				if(i<values.length-1) {
					result += ",";
				}
			}
		}

		return result;
	}
	

	protected static String toString(Instant value) {
		String result = null;

		if (value != null) {
			result = DateTimeFormatter.ISO_INSTANT.format(value);
		}

		return result;
	}
	
	
	protected static String toString(Instant [] values) {
		String result = null;
		
		if ( values!=null) {
			result="";
			for (int i=0; i<values.length; i++) {
				result+=toString(values[i]);
				if(i<values.length-1) 
					result += ",";
			}
		}
		return result;
	}

	protected static String toString(LocalDate value) {
		String result = null;

		if (value != null) {
			result = DateTimeFormatter.ISO_LOCAL_DATE.format(value);
		}

		return result;
	}

	protected static String toString(UUID uuid) {
		String result = null;

		if (uuid != null) {
			result = uuid.toString();
		}

		return result;
	}

	protected static String toString(Number value) {
		String result = null;

		if (value != null) {
			result = value.toString();
		}

		return result;
	}
	
	protected static String nvl(String value, String defaultValue) {
		return (value==null)?defaultValue:value;
	}

	protected static String getEtag(Object object) throws FactoryException {

		String result = null;

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] mdBytes = md.digest(getEntityToBytes(object));

			UUID uuid = ByteUtils.getUUID(mdBytes);

			result = uuid.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new FactoryException("Failed to create MD5 hash.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FactoryException("Failed to serialize object.", e);
		}

		return result;
	}

	private static byte[] getEntityToBytes(Object object) throws IOException {

		byte[] result = null;

		ObjectOutput out = null;
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			result = bos.toByteArray();

			out.close();
			out = null;

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		return result;
	}
	
	protected static final boolean isAuthenticatedUser(WebAdeAuthentication authentication, String userTypeCode, String userGuid) {
		boolean result = false;

		if(authentication.getUserTypeCode().equals(userTypeCode)&&authentication.getUserGuid().equals(userGuid)) {
			
			result = true;
		}

		return result;
	}
	
	protected static String toString(Boolean value) {
		String result = null;
		
		if(value!=null) {
			result = Boolean.TRUE.equals(value)?"Y":"N";
		}
		
		return result;
	}
}
