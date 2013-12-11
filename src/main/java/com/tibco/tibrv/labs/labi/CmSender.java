package com.tibco.tibrv.labs.labi;

//TIBCO Rendezvous API Java Development
//File: CmSender.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmMsg;
import com.tibco.tibrv.TibrvCmTransport;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;

public class CmSender implements TibrvMsgCallback {
	// RVD transport parameters
	String service = null;
	String network = null;
	String daemon = null;

	String subject = "subject";
	String cmname = "cmname";

	// Count of messages to be sent
	int count = 100;

	String confirmAdvisorySubject = "confirmAdvisorySubject";

	// seqno of the last message, 0 is invalid value
	long lastSeqno = 0;

	// Used to synchronize the last seqno
	Object lockSeqno = new Object();

	TibrvListener confirmListener = null;

	// ---------------------------------------------------------------
	// CmSender
	//
	// Creates CM trasnport and publishes count messages
	// on a given subject
	// ---------------------------------------------------------------

	public CmSender(String args[]) {
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

		TibrvRvdTransport rvdTransport = null;
		TibrvCmTransport cmTransport = null;

		try {
			rvdTransport = new TibrvRvdTransport(service, network, daemon);
			cmTransport = new TibrvCmTransport(rvdTransport, cmname, true);
			confirmListener = new TibrvListener(Tibrv.defaultQueue(), this, rvdTransport, confirmAdvisorySubject, null);

		} catch (TibrvException e) {
			System.out.println("Failed to create transport or listener:");
			e.printStackTrace();
			System.exit(0);
		}

		// Dispatch default queue
		TibrvDispatcher disp = new TibrvDispatcher(Tibrv.defaultQueue());

		System.out.println("Publishing " + count
				+ " certified messages on subject " + subject);

		// Create the message
		TibrvMsg msg = new TibrvMsg();

		try {
			msg.setSendSubject(subject);
			TibrvCmMsg.setTimeLimit(msg, 10d);

			// Publish count messages.
			for (int i = 1; i <= count; i++) {
				// Delay for 1 second
				Thread.sleep(2000);

				// Distinguish sent messages
				msg.update("index", i);

				System.out.println("Publishing message " + msg);
				System.out.flush();

				// we must block access to lastSeqno because
				// the confirmation may arrive before we can
				// retrieve seqno from the message we just sent

				synchronized (lockSeqno) {
					cmTransport.send(msg);

					// If it was the last message, get it's seqno
					if (i == count) {
						lastSeqno = TibrvCmMsg.getSequence(msg);
						System.out
								.println("Last sequence number to be confirmed = "
										+ lastSeqno);
						System.out.flush();
					}
				}
			}

			// wait until the last message has been confirmed
			disp.join();

		} catch (TibrvException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InterruptedException e) {
			System.exit(0);
		}

	}

	// ---------------------------------------------------------------
	// onMsg
	// ---------------------------------------------------------------

	@Override
	public void onMsg(TibrvListener list, TibrvMsg msg) {
		long seqno = 0;

		// because we check if the last message was confirmed we
		// should synchronize access to it

		synchronized (lockSeqno) {
			try {
				seqno = msg.getAsLong("seqno", 0);
				System.out.println("Confirmed message with seqno=" + seqno);
				System.out.flush();
			} catch (TibrvException e) {
				System.out
						.println("Exception occurred while getting \"seqno\" field from DELIVERY.CONFIRM advisory message:");
				e.printStackTrace();
				System.exit(0);
			}

			try {
				// check if the last message has been confirmed
				// and if it was the last message, close Tibrv
				if (lastSeqno > 0 && lastSeqno == seqno) {
					Tibrv.close();
				}
			} catch (TibrvException e) {
				System.out.println("Exception occurred in Tibrv.close():");
				e.printStackTrace();
				System.exit(0);
			}

		}
	}

	// ---------------------------------------------------------------
	// usage
	// ---------------------------------------------------------------

	void usage() {
		System.out
				.println("Usage: java cmsender [-service service] [-network network]");
		System.out.println("            [-daemon daemon] [-cmname cmname]");
		System.out.println("            [-subject subject] [-count NNN]");
		System.out.println("    default values are:");
		System.out.println("       service = " + service);
		System.out.println("       network = " + network);
		System.out.println("       daemon  = " + daemon);
		System.out.println("       cmname  = " + cmname);
		System.out.println("       subject = " + subject);
		System.out.println("       count   = " + count);
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
			}
			if (args[i].equals("-service")) {
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
			} else if (args[i].equals("-count")) {
				try {
					count = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					usage();
				}
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
		new CmSender(args);
	}

}
