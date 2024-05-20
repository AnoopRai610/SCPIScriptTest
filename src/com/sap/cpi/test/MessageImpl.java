package com.sap.cpi.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.camel.Attachment;
import org.apache.camel.converter.stream.InputStreamCache;

import com.sap.gateway.ip.core.customdev.util.AttachmentWrapper;

public class MessageImpl implements com.sap.gateway.ip.core.customdev.util.Message {
	private Object body;
	private Map<String, Object> headers;
	private Map<String, Object> properties;
	private Map<String, DataHandler> attachments;
	private Map<String, AttachmentWrapper> attachmentWrapperObjects;
	private Map<String, Attachment> attachmentObjects;

	protected MessageImpl(File file) {
		this();
		try {
			if (file.isFile()) {
				setBody(readFilesToString(file));
				setProperty("attachmentPath", file.getParent());
			} else {
				setProperty("attachmentPath", file.getPath());
				for (File f : file.listFiles()) {
					if (f.isDirectory())
						continue;

					if (f.getName().matches("(?i).*body.*")) {
						setBody(readFilesToString(f));
						continue;
					}

					if (f.getName().matches("(?i).*header.*")) {
						for (String header : readFilesToString(f).split(System.lineSeparator()))
							setHeader(header.split("/t")[0], header.split("/t")[1]);
						continue;
					}

					if (f.getName().matches("(?i).*property.*")) {
						for (String property : readFilesToString(f).split(System.lineSeparator()))
							setProperty(property.split("/t")[0], property.split("/t")[1]);
						continue;
					}

					if (f.getName().matches("(?).*attachment.*")) {
						DataSource ds = new FileDataSource(f);
						this.addAttachmentObject(f.getName(), new AttachmentWrapper(ds));
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private MessageImpl() {
		body = new InputStreamCache(new String().getBytes());
		attachments = new HashMap<>();
		headers = new HashMap<>();
		properties = new HashMap<>();
		attachmentWrapperObjects = new HashMap<>();
	}

	private String readFilesToString(File file) {
		InputStream io = null;
		try {
			io = new FileInputStream(file);
			byte[] byt = new byte[io.available()];
			io.read(byt);
			return new String(byt);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (io != null)
				try {
					io.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
		}
	}

	@Override
	public <T> T getBody(Class<T> type) {
		if (type.isInstance(this.body))
			return type.cast(this.body);
		return getObject(this.body, type);
	}

	@Override
	public Object getBody() {
		return this.body;
	}

	@Override
	public void setBody(Object body) {
		this.body = getObjectByte(body);
	}

	@Override
	public long getBodySize() {
		return ((InputStreamCache) this.body).length();
	}

	@Override
	public Map<String, Object> getHeaders() {
		return this.headers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getHeader(String var1, Class<T> var2) {
		return (T) this.headers.get(var1);
	}

	@Override
	public void setHeaders(Map<String, Object> var1) {
		this.headers = var1;
	}

	@Override
	public void setHeader(String var1, Object var2) {
		this.headers.put(var1, var2);
	}

	@Override
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	@Override
	public void setProperties(Map<String, Object> var1) {
		this.properties = var1;
	}

	@Override
	public void setProperty(String var1, Object var2) {
		this.properties.put(var1, var2);
	}

	@Override
	public Object getProperty(String var1) {
		return this.properties.get(var1);
	}

	@Override
	public long getAttachmentsSize() {
		return getAttachmentWrapperObjects().size();
	}

	@Override
	public void addAttachmentHeader(String headerName, String headerValue, AttachmentWrapper attachment) {
		attachment.addHeader(headerName, headerValue);
	}

	@Override
	public void setAttachmentHeader(String headerName, String headerValue, AttachmentWrapper attachment) {
		attachment.setHeader(headerName, headerValue);
	}

	@Override
	public String getAttachmentHeader(String headerName, AttachmentWrapper attachment) {
		return attachment.getHeader(headerName);
	}

	@Override
	public void removeAttachmentHeader(String headerName, AttachmentWrapper attachment) {
		attachment.removeHeader(headerName);
	}

	@Override
	public Map<String, AttachmentWrapper> getAttachmentWrapperObjects() {
		return this.attachmentWrapperObjects;
	}

	@Override
	public void setAttachmentWrapperObjects(Map<String, AttachmentWrapper> attachmentObjects) {
		this.attachmentWrapperObjects = attachmentObjects;
	}

	@Override
	public void addAttachmentObject(String id, AttachmentWrapper content) {
		this.attachmentWrapperObjects.put(id, content);
	}

	@Deprecated
	@Override
	public void addAttachmentHeader(String headerName, String headerValue, Attachment attachment) {
		attachment.addHeader(headerName, headerValue);
	}

	@Deprecated
	@Override
	public void setAttachmentHeader(String headerName, String headerValue, Attachment attachment) {
		attachment.setHeader(headerName, headerValue);
	}

	@Deprecated
	@Override
	public String getAttachmentHeader(String headerName, Attachment attachment) {
		return attachment.getHeader(headerName);
	}

	@Deprecated
	@Override
	public void removeAttachmentHeader(String headerName, Attachment attachment) {
		attachment.removeHeader(headerName);
	}

	@Deprecated
	@Override
	public Map<String, Attachment> getAttachmentObjects() {
		return this.attachmentObjects;
	}

	@Deprecated
	@Override
	public void setAttachmentObjects(Map<String, Attachment> attachmentObjects) {
		this.attachmentObjects = attachmentObjects;
	}

	@Deprecated
	@Override
	public void addAttachmentObject(String id, Attachment content) {
		this.attachmentObjects.put(id, content);
	}

	@Override
	public Map<String, DataHandler> getAttachments() {
		return this.attachments;
	}

	@Override
	public void setAttachments(Map<String, DataHandler> attachments) {
		this.attachments = attachments;
	}

	@SuppressWarnings("unchecked")
	private <T> T getObject(Object obj, Class<T> type) {
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStreamCache isc = (InputStreamCache) obj;
			isc.writeTo(baos);
			ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			return (T) ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	private InputStreamCache getObjectByte(Object body) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(body);
			oos.flush();
			return new InputStreamCache(baos.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				if (baos != null)
					baos.close();
				if (oos != null)
					oos.close();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

}
