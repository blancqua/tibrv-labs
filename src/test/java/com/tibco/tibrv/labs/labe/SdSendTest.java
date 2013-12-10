package com.tibco.tibrv.labs.labe;

import org.junit.Test;


public class SdSendTest {

	@Test
	public void sdSend() throws Exception {
		SdSend.main(new String[] { "-user", "user04", "-password", "user04", "USER04.test", "Test Message" });
	}

}
