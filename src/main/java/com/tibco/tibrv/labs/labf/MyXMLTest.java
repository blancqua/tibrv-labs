package com.tibco.tibrv.labs.labf;

//TIBCO Rendezvous API Java Development
//File: MyXMLTest.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvMsgField;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvXml;

public class MyXMLTest implements TibrvMsgCallback {

	private static final String SUBJECT = "USER04.TEST";

	TibrvRvdTransport conn;

	/** Creates new MyXMLTest */
	public MyXMLTest() throws TibrvException {
		Tibrv.open();
		conn = new TibrvRvdTransport();
		new TibrvListener(Tibrv.defaultQueue(), this, conn, SUBJECT, null);

		TibrvMsg msg = new TibrvMsg();
		msg.setSendSubject(SUBJECT);

		msg.add("Name", "John");

		String s = "<XML> This is a test </XML>";
		TibrvXml myxml = new TibrvXml(s.getBytes());

		msg.add("XMLValue", myxml);
		conn.send(msg);
		System.out.println("Message sent");

		for (;;) {
			try {
				Tibrv.defaultQueue().dispatch();
			} catch (InterruptedException ix) {
				conn.destroy();
				Tibrv.close();
			}
		}

	}

	@Override
	public void onMsg(TibrvListener l, TibrvMsg msg) {
		try {
			TibrvMsgField field = msg.getField("XMLValue");

			System.out.println("Recieved subject: " + msg.getSendSubject());
			String a = (String) msg.get("Name");
			System.out.println("Name : " + a);
			if (field != null) {
				String s = new String(((TibrvXml) field.data).getBytes());
				System.out.println("XML Message recieved:" + s);
			}

		} catch (TibrvException ee) {
		}
	}

	public static void main(String... args) throws TibrvException {
		MyXMLTest test = new MyXMLTest();
	}
}
