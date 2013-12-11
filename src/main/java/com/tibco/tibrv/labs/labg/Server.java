package com.tibco.tibrv.labs.labg;

//TIBCO Rendezvous API Java Development
//File: Server.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvTransport;

class Server implements TibrvMsgCallback {
	Server(String[] args) throws TibrvException {
		if (args.length < 1) {
			System.out.println("Usage: Server SUBJECT ...");
			System.exit(1);
		}

		Tibrv.open();
		TibrvTransport transport = new TibrvRvdTransport();
		for (int i = 0; i < args.length; ++i) {
			new TibrvListener(Tibrv.defaultQueue(), this, transport, args[i],
					null);
		}

		for (;;) {
			try {
				Tibrv.defaultQueue().dispatch();
			} catch (InterruptedException ix) {
			}
		}
	}

	public static void main(String... args) throws TibrvException {
		new Server(args);
	}

	@Override
	public void onMsg(TibrvListener l, TibrvMsg m) {
		System.out.println("Message received with subject: "
				+ m.getSendSubject());
		System.out.println("Msg: " + m);

		TibrvMsg reply = new TibrvMsg();

		try {
			reply.add("Reply", "Aloha!!!");
			l.getTransport().sendReply(reply, m);
		} catch (TibrvException e) {
			e.printStackTrace();
		}

	}
}