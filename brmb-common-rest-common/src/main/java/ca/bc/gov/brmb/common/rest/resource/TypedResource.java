package ca.bc.gov.brmb.common.rest.resource;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.Hidden;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TypedResource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String eTag;

	private Long cacheExpiresMillis;

	@Hidden
	@JsonIgnore
	@XmlTransient
	public String get_type() {
		return null;
	}

	@Hidden
	@JsonIgnore
	@XmlTransient
	public String getQuotedETag() {
		return getETag(true);
	}

	@Hidden
	@JsonIgnore
	@XmlTransient
	public String getUnquotedETag() {
		return getETag(false);
	}

	private String getETag(boolean quoted) {
		String result = null;
		if (this.eTag != null) {
			if (quoted) {
				if (eTag.charAt(0) == '"') {
					result = this.eTag;
				} else {
					result = '"' + this.eTag + '"';
				}
			} else {
				if (eTag.charAt(0) == '"') {
					result = this.eTag.substring(1, this.eTag.length() - 1);
				} else {
					result = this.eTag;
				}
			}
		}
		return result;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	@Hidden
	@JsonIgnore
	@XmlTransient
	public Long getCacheExpiresMillis() {
		return cacheExpiresMillis;
	}

	public void setCacheExpiresMillis(Long cacheExpiresMillis) {
		this.cacheExpiresMillis = cacheExpiresMillis;
	}
}
