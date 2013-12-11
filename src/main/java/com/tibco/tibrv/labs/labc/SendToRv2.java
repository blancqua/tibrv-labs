package com.tibco.tibrv.labs.labc;

// TIBCO Rendezvous API Java Development
// File: SendToRV.java
// TIBCO Education Services
// Copyright 2005 - TIBCO Software Inc.
// ALL RIGHTS RESERVED

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvRvdTransport;

public class SendToRv2 {
	static TibrvRvdTransport conn;
	static String service, network, daemon;
	static String subject;
	static TibrvMsg msg;

	public void init(String[] args) {
		if (args.length >= 2) {
			return;
		}
		System.out.println("Usage: SendToRv SUBJECT MESSAGE ...");
		System.exit(1);
	}

	static void sendMsgToRv(String[] inputArgs) throws TibrvException {
		msg = new TibrvMsg();
		subject = inputArgs[0];
		msg.setSendSubject(subject);

		for (int i = 1; i < inputArgs.length; ++i) {
			System.out.println("Subject: " + msg.getSendSubject());
			System.out.println("   { Message = " + inputArgs[i] + "}");

			msg.update("Message", inputArgs[i]);
			conn.send(msg);
		}
	}

	public void openTib() throws TibrvException {
		Tibrv.open(IMPL_NATIVE);
		if (Tibrv.isValid()) {
			System.out.println("Tibrv machinery is valid");
		}
	}

	public void closeTib() throws TibrvException {
		Tibrv.close();
		if (!Tibrv.isValid()) {
			System.out.println("Tibrv machinery is no longer valid");
		}
	}

	public static void main(String... args) {
		try {
			SendToRv2 orv = new SendToRv2();
			orv.init(args);
			orv.openTib();
			orv.connectRv();
			orv.checkRvd();
			sendMsgToRv(args);
			orv.destroyRv();
			orv.closeTib();
		} catch (TibrvException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public void connectRv() throws TibrvException {
		conn = new TibrvRvdTransport();
	}

	public void destroyRv() throws TibrvException {
		conn.destroy();
		System.out.println("Transport Destroyed");
		if (!conn.isValid()) {
			System.out.println("Rvd Transport is no longer valid");
		}
	}

	public void checkRvd() throws TibrvException {
		if (conn.isValid()) {
			System.out.println("Transport created\n");
		}
	}
}
