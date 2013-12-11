package com.tibco.tibrv.labs.labi;

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmListener;
import com.tibco.tibrv.TibrvCmMsg;
import com.tibco.tibrv.TibrvCmTransport;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvQueue;
import com.tibco.tibrv.TibrvRvdTransport;

//TIBCO Rendezvous API Java Development
//File: CmListener.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

public class CmListener implements TibrvMsgCallback {

	// RVD transport parameters
	String service = null;
	String network = null;
	String daemon = null;
	String subject = "subject";
	String cmname = "cmname";

	TibrvQueue queue = null;
	TibrvRvdTransport rvdTransport = null;
	TibrvCmTransport cmTransport = null;
	TibrvCmListener cmListener = null;

	// ---------------------------------------------------------------
	// cmlistener
	// ---------------------------------------------------------------

	public CmListener(String args[]) {
		// Parse parameters
		parseParams(args);

		// open Tibrv in native implementation
		try {
			Tibrv.open(IMPL_NATIVE);
		} catch (TibrvException e) {
			System.out
					.println("Failed to open Tibrv in native implementation:");
			e.printStackTrace();
			System.exit(0);
		}

		// Create event queue, transports and listener

		try {
			// Our event queue
			queue = new TibrvQueue();
			rvdTransport = new TibrvRvdTransport(service, network, daemon);
			cmTransport = new TibrvCmTransport(rvdTransport, cmname, true);
			cmListener = new TibrvCmListener(queue, this, cmTransport, subject, null);
			cmListener.setExplicitConfirm();

		} catch (TibrvException e) {
			System.out
					.println("Failed to create queue, transport or listener:");
			e.printStackTrace();
			System.exit(0);
		}

		// Report we are running Ok
		System.out.println("Listening on subject: " + subject);

		// Dispatch queue
		TibrvDispatcher disp = new TibrvDispatcher(queue);

		// This example never quits...
		// If we would close Tibrv this join() would go through,
		// but because we never close Tibrv we'll get stuck
		// inside the join() call forever.
		try {
			disp.join();
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}

	// ---------------------------------------------------------------
	// onMsg
	// ---------------------------------------------------------------

	@Override
	public void onMsg(TibrvListener l, TibrvMsg msg) {
		System.out.println("Received message: " + msg);

		try {
			long seqno = TibrvCmMsg.getSequence(msg);

			// If it was not CM message or very first message
			// we'll get seqno=0. Only confirm if seqno > 0.
			if (seqno > 0) {
				System.out.println("Confirming message with seqno=" + seqno);
				System.out.flush();

				cmListener.confirmMsg(msg);
			}
		} catch (TibrvException e) {
			System.out.println("Failed to confirm CM message: " + e.toString());
		}

		// if message had the reply subject, send the reply
		try {
			if (msg.getReplySubject() != null) {
				TibrvMsg reply = new TibrvMsg(msg.getAsBytes());
				cmTransport.sendReply(reply, msg);
			}
		} catch (TibrvException e) {
			System.out.println("Failed to send reply:");
			e.printStackTrace();
		}
	}

	// ---------------------------------------------------------------
	// usage
	// ---------------------------------------------------------------

	void usage() {
		System.out
				.println("Usage: java cmlistener [-service service] [-network network]");
		System.out.println("            [-daemon daemon] [-cmname cmname]");
		System.out.println("            [-subject subject]");
		System.out.println("    default values are:");
		System.out.println("       service = " + service);
		System.out.println("       network = " + network);
		System.out.println("       daemon  = " + daemon);
		System.out.println("       cmname  = " + cmname);
		System.out.println("       subject = " + subject);
		System.exit(-1);
	}

	// ---------------------------------------------------------------
	// parseParams
	// ---------------------------------------------------------------

	void parseParams(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].equalsIgnoreCase("-h")
					|| args[i].equalsIgnoreCase("-help")
					|| args[i].equalsIgnoreCase("?")) {
				usage();
			} else if (i == args.length - 1) // all parameters require value
			{
				usage();
			} else if (args[i].equals("-service")) {
				service = args[i + 1];
				i += 2;
			} else if (args[i].equals("-network")) {
				network = args[i + 1];
				i += 2;
			} else if (args[i].equals("-daemon")) {
				daemon = args[i + 1];
				i += 2;
			} else if (args[i].equals("-subject")) {
				subject = args[i + 1];
				i += 2;
			} else if (args[i].equals("-cmname")) {
				cmname = args[i + 1];
				i += 2;
			} else {
				usage();
			}
		}
	}

	// ---------------------------------------------------------------
	// main()
	// ---------------------------------------------------------------

	public static void main(String args[]) {
		new CmListener(args);
	}

}
