package com.tibco.tibrv.labs.labf;

// TIBCO Rendezvous API Java Development
//File: MsgParser.java
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
import com.tibco.tibrv.TibrvTransport;

class MsgParser implements TibrvMsgCallback {
	MsgParser(String[] args) throws TibrvException {
		if (args.length < 1) {
			System.out.println("Usage: MsgParser SUBJECT ...");
			System.exit(1);
		}

		Tibrv.open();
		TibrvTransport transport = new TibrvRvdTransport();
		for (int i = 0; i < args.length; ++i) {
			new TibrvListener(Tibrv.defaultQueue(), this, transport, args[i], null);
		}

		for (;;) {
			try {
				Tibrv.defaultQueue().dispatch();
			} catch (InterruptedException ix) {
			}
		}
	}

	public static void main(String... args) throws TibrvException {
		new MsgParser(args);
	}

	@Override
	public void onMsg(TibrvListener listener, TibrvMsg msg) {
		try {
			parseMsg(msg);
		} catch (TibrvException tx) {
			System.out.println(tx);
		}
	}

	void parseMsg(TibrvMsg msg) throws TibrvException {
		parse(msg, "");
	}

	void parse(TibrvMsg msg, String indent) throws TibrvException {
		for (int i = 0; i < msg.getNumFields(); ++i) {
			TibrvMsgField f = msg.getFieldByIndex(i);
			if (f.type == TibrvMsg.MSG) {
				parse((TibrvMsg) f.data, indent + "   ");
			} else {
				System.out.println(indent + f);
			}
		}
	}
}