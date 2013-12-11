package com.tibco.tibrv.labs.labd;

//TIBCO Rendezvous API Java Development
//File: ListenToRV.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;

public class ListenToRv implements TibrvMsgCallback {

	public ListenToRv(String[] args) throws TibrvException {
		if (args.length < 1) {
			System.out.println("Usage: ListenToRv SUBJECT ...");
			System.exit(1);
		}

		Tibrv.open(IMPL_NATIVE);
		TibrvRvdTransport conn = new TibrvRvdTransport();

		for (int i = 0; i < args.length; ++i) {
			new TibrvListener(Tibrv.defaultQueue(), this, conn, args[i], null);
		}

		for (;;) {
			try {
				Tibrv.defaultQueue().dispatch();
			} catch (InterruptedException ix) {
			}
		}
	}

	public static void main(String... args) throws TibrvException {
		new ListenToRv(args);
	}

	@Override
	public void onMsg(TibrvListener listener, TibrvMsg message) {
		System.out.println("Send Subject: " + message.getSendSubject());
		System.out.println("Reply Subject: " + message.getReplySubject());
		System.out.println("Message: " + message);
	}


}