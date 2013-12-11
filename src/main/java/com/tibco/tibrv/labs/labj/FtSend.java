package com.tibco.tibrv.labs.labj;

//TIBCO Rendezvous API Java Development
//File: FtSend.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvFtMember;
import com.tibco.tibrv.TibrvFtMemberCallback;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvTransport;

public class FtSend implements TibrvFtMemberCallback {
	static TibrvTransport transport;
	static int wt = 50;
	static String group;
	static volatile boolean active;
	static String id;

	public static void main(String... args) throws TibrvException, InterruptedException, UnknownHostException {

		String subject = args.length > 2 ? args[2] : "TIME";

		try {
			if (args.length > 0) {
				wt = Integer.parseInt(args[0]);
			}
		} catch (NumberFormatException nfx) {
		}

		group = args.length > 1 ? args[1] : ("FtSend." + InetAddress
				.getLocalHost());
		id = group + "@" + wt;

		FtSend orv = new FtSend();

		orv.open();
		transport = orv.connectRv();

		new TibrvFtMember(Tibrv.defaultQueue(), orv, transport, group, wt, 1, 1, 1.5, 2, null);
		new TibrvDispatcher(Tibrv.defaultQueue());

		boolean always = true;
		while (always) {

			if (active)
			{
				TibrvMsg msg = new TibrvMsg();
				msg.setSendSubject(subject);
				msg.update(id, new java.util.Date());
				transport.send(msg);
			}
			Thread.sleep(5000);
		}

		orv.destroyRv(transport);
		orv.close();
	}

	public TibrvRvdTransport connectRv() throws TibrvException {

		TibrvRvdTransport connection = new TibrvRvdTransport();
		return connection;
	}

	public void destroyRv(TibrvTransport connection) throws TibrvException {
		connection.destroy();
	}

	public void open() throws TibrvException {
		Tibrv.open();
	}

	public void close() throws TibrvException {
		Tibrv.close();
	}

	@Override
	public void onFtAction(TibrvFtMember m, String group, int action) {
		switch (action) {

		case TibrvFtMember.PREPARE_TO_ACTIVATE:
			System.out.println(id + " preparing...");
			break;

		case TibrvFtMember.ACTIVATE:
			System.out.println(id + " activating...");

			active = true;

			break;

		case TibrvFtMember.DEACTIVATE:
			System.out.println(id + " deactivating...");

			active = false;

			break;
		}
	}
}
