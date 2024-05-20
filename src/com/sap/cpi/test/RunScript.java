package com.sap.cpi.test;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import com.sap.gateway.ip.core.customdev.util.Message;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class RunScript {
	Message message;
	Script script;

	public RunScript(String messagePath, String scriptPath) throws CompilationFailedException, IOException {
		this(new File(messagePath), new File(scriptPath));
	}

	public RunScript(File messageFile, File scriptFile) throws CompilationFailedException, IOException {
		message = new MessageImpl(messageFile);
		setScriptPath(scriptFile);
	}

	public Message invokeMethod(){
		return invokeMethod(null);
	}

	public Message invokeMethod(String methodName) {
		if(methodName==null)
			methodName = "processData";
		return message = (Message) script.invokeMethod(methodName, message);
	}

	public Message getMessage() {
		return message;
	}

	public Message createMessage(File messageFile) {
		return message = new MessageImpl(messageFile);
	}

	public Message createMessage(String messagePath) {
		return createMessage(new File(messagePath));
	}

	public void setScriptPath(File scriptFile) throws CompilationFailedException, IOException {
		GroovyShell shell = new GroovyShell();
		script = shell.parse(scriptFile);
		MessageLogFactory messageLogFactory = new MessageLogFactory();
		script.setProperty("messageLogFactory", messageLogFactory);
	}

}
