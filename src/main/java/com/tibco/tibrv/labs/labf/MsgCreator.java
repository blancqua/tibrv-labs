package com.tibco.tibrv.labs.labf;

//TIBCO Rendezvous API Java Development
//File: MsgCreator.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvRvdTransport;

public class MsgCreator {
	static TibrvRvdTransport conn;
	static String service, network, daemon;
	static String subject;

	TibrvMsg msg = new TibrvMsg();

	public void init(String[] args) {
		if (args.length >= 1) {
			return;
		}
		System.out.println("Usage: MsgCreator SUBJECT ...");
		System.exit(1);
	}

	void send(String[] inputArgs) throws TibrvException {
		for (int i = 0; i < inputArgs.length; ++i) {
			msg.setSendSubject(inputArgs[i]);
			msg.setReplySubject(inputArgs[i] + ".REPLY");
			System.out.println("Subject: " + msg.getSendSubject());
			System.out.println("      reply subject: " + msg.getReplySubject());
			createMsg(msg);
			System.out.println("MESSAGE:");
			System.out.println(msg);
			conn.send(msg);
		}
	}

	byte b = 1;
	short s = 2;
	int i = 3;
	long l = 4;

	public void createMsg(TibrvMsg msg) throws TibrvException {
		msg.add("STRING", "text");
		msg.update("STRING", "updatedtext");
		msg.add("BYTE", b);
		msg.add("SHORT", s);
		msg.add("INT", i);
		msg.add("INT", i * 2);
		msg.update("INT", i * 3);
		msg.add("LONG", l);
		msg.add("LONG", l * 2);

		TibrvMsg subMessage = new TibrvMsg();
		subMessage.add("SUB_MSG_STRING", "subtext");
		msg.add("SUB", subMessage);

		msg.add("INTARRAY", new int[] { 1, 2, 3 });
		msg.add("BYTEARRAY", new byte[] { 4, 5 });
	}

	public void openTib() throws TibrvException {
		Tibrv.open();
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

	public static void main(String... args) throws TibrvException {
		MsgCreator orv = new MsgCreator();
		orv.init(args);
		orv.openTib();
		orv.connectRv();
		orv.checkRvd();
		orv.send(args);
		orv.destroyRv();
		orv.closeTib();
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
			System.out.println("Transport created\n"
					+ "Rvd Transport Information:");
			System.out.println("RVD isValid: " + conn.isValid());
			service = conn.getService();
			System.out.println("Service: " + service);
			network = conn.getNetwork();
			System.out.println("Network: " + network);
			daemon = conn.getDaemon();
			System.out.println("Daemon: " + daemon);
		}
	}
}
