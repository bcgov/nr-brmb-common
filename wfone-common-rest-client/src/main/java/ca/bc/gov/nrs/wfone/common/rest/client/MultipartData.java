package ca.bc.gov.nrs.wfone.common.rest.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

public class MultipartData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String contentType;
	private byte[] bytes;
	// We do not want to serialize a stream
	private transient InputStream stream;
	private boolean streamable;

	public MultipartData() {
	}

	public MultipartData(String fileName, String contentType, byte[] bytes) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.bytes = bytes;
		this.streamable = false;
	}

	public MultipartData(String fileName, String contentType, InputStream stream) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.stream = stream;
		this.streamable = true;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
		this.stream = null;
		this.streamable = false;
	}

	public InputStream getInputStream() {
		// If we have a stream, return the stream. Otherwise, return the byte array as a stream.
		return stream != null ? stream : this.bytes != null ? new ByteArrayInputStream(this.bytes) : null;
	}

	public void setInputStream(InputStream stream) {
		this.stream = stream;
		this.bytes = null;
		this.streamable = true;
	}

	public boolean isStreamable() {
		return this.streamable;
	}
}
