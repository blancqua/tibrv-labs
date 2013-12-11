package com.tibco.tibrv.labs.labg;

//TIBCO Rendezvous API Java Development
//File: Client.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvRvdTransport;

public class Client {
	static TibrvRvdTransport conn;
	static String service, network, daemon;
	static String subject;

	public void init(String[] args) {
		if (args.length >= 1) {
			return;
		}
		System.exit(1);
	}

	static void sendRequest(String[] inputArgs) throws TibrvException {
		TibrvMsg msg = new TibrvMsg();
		for (int i = 0; i < inputArgs.length; ++i) {
			subject = inputArgs[0];
			msg.setSendSubject(subject);
			msg.setReplySubject(subject + ".REPLY");

			System.out.println("Subject: " + msg.getSendSubject());
			System.out.println("   { Message = " + inputArgs[i] + "}");
			System.out.println("      reply subject: " + msg.getReplySubject());
			msg.update("Message", inputArgs[i]);

			TibrvMsg reply = conn.sendRequest(msg, 5d);
			System.out.println(reply);
		}
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

	public static void main(String... args) {
		try {
			Client orv = new Client();
			orv.init(args);
			orv.openTib();
			orv.connectRv();
			sendRequest(args);
			orv.checkRvd();
			orv.destroyRv();
			orv.closeTib();
		} catch (TibrvException e) {
			System.err.println(e);
			System.exit(-1);
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
