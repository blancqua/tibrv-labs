package com.tibco.tibrv.labs.labf;

import static com.tibco.tibrv.labs.labf.MsgCreatorTest.SUBJECT;

import org.junit.Test;


public class MsgParserTest {

	@Test
	public void msgParser() throws Exception {
		MsgParser.main(SUBJECT);
	}

}
