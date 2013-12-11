package com.tibco.tibrv.labs.labe;

/*
 * TIBCO Rendezvous API Java Development
 * File : SdSend.java
 * TIBCO Education Services
 * Copyright 2005 - TIBCO Software Inc.
 * ALL RIGHTS RESERVED
 *
 */

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;
import static com.tibco.tibrv.TibrvSdContext.TIBRV_SECURE_DAEMON_ANY_CERT;
import static com.tibco.tibrv.TibrvSdContext.TIBRV_SECURE_DAEMON_ANY_NAME;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvSdContext;
import com.tibco.tibrv.TibrvTransport;

public class SdSend {

	String daemon = "ssl:localhost:7500";
	String username = null;
	String password = null;
	String FIELD_NAME = "DATA";

	public SdSend(String args[]) {
		// parse arguments
		int i = get_InitParams(args);

		// we must have at least one subject and one message
		if (i > args.length - 2) {
			usage();
		}

		if ((username == null) || (password == null)) {
			usage();
		}

		try {
			Tibrv.open(IMPL_NATIVE);
		} catch (TibrvException e) {
			System.err
					.println("Failed to open Tibrv in native implementation:");
			e.printStackTrace();
			System.exit(0);
		}

		// Create RVD transport
		TibrvTransport transport = null;
		try {
			TibrvSdContext.setDaemonCert(TIBRV_SECURE_DAEMON_ANY_NAME, TIBRV_SECURE_DAEMON_ANY_CERT);
			TibrvSdContext.setUserNameWithPassword(username, password);
			transport = new TibrvRvdTransport(null, null, daemon);
		} catch (TibrvException e) {
			System.err.println("Failed to create TibrvRvdTransport:");
			e.printStackTrace();
			System.exit(0);
		}

		TibrvMsg msg = new TibrvMsg();
		try {
			msg.setSendSubject(args[i++]);
		} catch (TibrvException e) {
			System.err.println("Failed to set send subject:");
			e.printStackTrace();
			System.exit(0);
		}

		try {
			// one message for each parameter
			while (i < args.length) {
				System.out.println("Publishing: subject="
						+ msg.getSendSubject() + " \"" + args[i] + "\"");
				msg.update(FIELD_NAME, args[i]);
				transport.send(msg);
				i++;
			}
		} catch (TibrvException e) {
			System.err.println("Error sending a message:");
			e.printStackTrace();
			System.exit(0);
		}

		try {
			Tibrv.close();
		} catch (TibrvException e) {
			System.err.println("Exception dispatching default queue:");
			e.printStackTrace();
			System.exit(0);
		}

	}

	// print usage information and quit
	void usage() {
		System.err.println("Usage: java SdSend ");
		System.err
				.println("       -user <user> -password <password> <subject> <messages>");
		System.exit(-1);
	}

	int get_InitParams(String[] args) {
		int i = 0;
		while (i < args.length - 1 && args[i].startsWith("-")) {
			if (args[i].equals("-user")) {
				username = args[i + 1];
				i += 2;
			} else if (args[i].equals("-password")) {
				password = args[i + 1];
				i += 2;
			} else {
				usage();
			}
		}
		return i;
	}

	public static void main(String... args) {
		new SdSend(args);
	}

}