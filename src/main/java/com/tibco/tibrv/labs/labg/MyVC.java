package com.tibco.tibrv.labs.labg;

//TIBCO Rendezvous API Java Development
//File: MyVC.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvVcTransport;

public class MyVC implements TibrvMsgCallback {

	private static final String SUBJECT = "USER04.TEST";

	TibrvRvdTransport conn;

	TibrvVcTransport vconn;
	String kk;
	int i = 0;

	public MyVC() throws TibrvException {
		Tibrv.open();
		conn = new TibrvRvdTransport();

		System.out.println("Creating an Accept VC Object");
		vconn = TibrvVcTransport.createAcceptVc(conn);

		kk = vconn.getConnectSubject();
		System.out.println("Connect Subject is :" + kk);

		new TibrvListener(Tibrv.defaultQueue(), this, vconn, SUBJECT, null);
		new TibrvListener(Tibrv.defaultQueue(), this, conn, "_RV.INFO.SYSTEM.VC.*", null);

		TibrvMsg msg = new TibrvMsg();
		msg.setSendSubject(SUBJECT);
		msg.setReplySubject(vconn.getConnectSubject());

		msg.add("Data", "Let's setup a virtual circuit !");
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
			i = i + 1;
			String a = (String) msg.get("Data");
			System.out.println("Data : " + a);
			if (i == 1) {
				System.out.println("Send Subject: " + msg.getSendSubject());
				System.out.println("Reply Subject: " + msg.getReplySubject());
				new TibrvListener(Tibrv.defaultQueue(), this, vconn, "TEST2", null);
				System.out.println("Listening on TEST2");
			}
		} catch (TibrvException ee) {
		}
	}

	public static void main(String... args) throws TibrvException {
		MyVC vc = new MyVC();
	}
}