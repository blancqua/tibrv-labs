package com.tibco.tibrv.labs.labf;

import org.junit.Test;


public class MsgCreatorTest {

	static final String SUBJECT = "USER04.complexmsg";

	@Test
	public void msgCreator() throws Exception {
		MsgCreator.main(SUBJECT);
	}

}
