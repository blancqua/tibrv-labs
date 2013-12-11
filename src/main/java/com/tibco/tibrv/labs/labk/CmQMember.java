package com.tibco.tibrv.labs.labk;

//TIBCO Rendezvous API Java Development
//File: CmQMember.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvCmListener;
import com.tibco.tibrv.TibrvCmMsg;
import com.tibco.tibrv.TibrvCmQueueTransport;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvQueue;
import com.tibco.tibrv.TibrvRvdTransport;

public class CmQMember implements TibrvMsgCallback {
	// RVD transport parameters
	String service = null;
	String network = null;
	String daemon = null;
	String subject = "subject";
	String queueName = "queue";

	public CmQMember(String args[]) throws TibrvException {
		// Parse parameters
		parseParams(args);
		Tibrv.open(IMPL_NATIVE);

		TibrvRvdTransport transport = new TibrvRvdTransport(service, network, daemon);

		try {
			// Create event queue
			TibrvQueue queue = new TibrvQueue();
			TibrvCmQueueTransport cmQueueTransport = new TibrvCmQueueTransport(transport, queueName);
			TibrvCmListener listener = new TibrvCmListener(queue, this, cmQueueTransport, subject, null);

			// Create dispatcher
			TibrvDispatcher disp = new TibrvDispatcher(queue);

			// Report we initialized Ok
			System.out.println("Queue name=" + queueName + ", listening on subject " + subject);

			// We'll never pass through this
			// call because this example never stops.
			disp.join();
		} catch (TibrvException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}


	@Override
	public void onMsg(TibrvListener listener, TibrvMsg msg) {
		try {
			long seqno = TibrvCmMsg.getSequence(msg);
			System.out.println("Received message with seqno=" + seqno + ": " + msg);
			System.out.flush();
		} catch (TibrvException e) {
			System.out.println("Failed to obtain seqno from CM message:");
			e.printStackTrace();
		}
	}

	void usage() {
		System.out
				.println("Usage: java cmqmember [-service service] [-network network]");
		System.out.println("            [-daemon daemon] [-queue queueName]");
		System.out.println("            [-subject subject]");
		System.out.println("    default values are:");
		System.out.println("       service = " + service);
		System.out.println("       network = " + network);
		System.out.println("       daemon  = " + daemon);
		System.out.println("       queue   = " + queueName);
		System.out.println("       subject = " + subject);
		System.exit(-1);
	}

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
			} else if (args[i].equals("-queue")) {
				queueName = args[i + 1];
				i += 2;
			} else {
				usage();
			}
		}
	}

	// ---------------------------------------------------------------
	// main()
	// ---------------------------------------------------------------

	public static void main(String... args) throws TibrvException {
		new CmQMember(args);
	}

}
