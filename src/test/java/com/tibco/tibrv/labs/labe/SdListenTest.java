package com.tibco.tibrv.labs.labe;

import org.junit.Test;


public class SdListenTest {

	@Test
	public void sdListen() throws Exception {
		SdListen.main(new String[] { "-user", "user04", "-password", "user04", "USER04.>" });
	}

}
