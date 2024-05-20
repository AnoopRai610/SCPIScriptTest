# SCPIScriptTest
For SAP CPI Groovy Script testing

# How to use?
===================

1. Add SCPIScriptTest.jar in project path.
2. An example code is as below:
```java

import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import com.sap.cpi.test.RunScript;
import com.sap.gateway.ip.core.customdev.util.Message;

public class TestClass {

	public static void main(String[] args) throws CompilationFailedException, IOException {
		
		//Path to message to set as body
		String messagePath = "..ProjectName/mainpayload.txt";
		
		// Script paths
		String sriptPath = "..ProjectName/src/com/FormatMapping.groovy"; 
		
		// Initiate RunScript
		RunScript runScript = new RunScript(messagePath, sriptPath);

		//Can get message instance before executing any function to add exchange property, message header and attachments
		Message message = runScript.getMessage();
		
		//Add message header
		message.setHeader("partnerName", "PARTNER1");
		message.setHeader("IDOCType", "MBGMCR");

		//Add exchange property
		message.setProperty("PreviousDate", "2021-05-27T10:05:00");
		message.setProperty("CurrentDate", "2021-05-27T10:05:00");
		message.setProperty("BankAccounts", "61386875,77591963,77591700,2423534");
		message.setProperty("LastRunDetail",
				"61386875|2021-04-01T12:00:00,77591963|2021-04-01T12:00:00,77591700|2021-04-01T12:00:00");

		//Invoke required method
		message = runScript.invokeMethod("processData");
		
		//Get Output from message and print to console..
		System.out.println("Transformed message");
		System.out.println(message.getBody(String.class));
	}

}
```
