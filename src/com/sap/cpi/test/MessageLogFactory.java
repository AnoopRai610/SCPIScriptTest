package com.sap.cpi.test;

import com.sap.it.api.msglog.MessageLog;

public class MessageLogFactory implements com.sap.it.api.msglog.MessageLogFactory {
	
	@Override
	public MessageLog getMessageLog(Object obj) {
		return new MessageLogLocal(obj);
	}

}
