package com.tibco.tibrv.labs.labe;

/*
 * TIBCO Rendezvous API Java Development
 * File : SdListen.java
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
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvSdContext;

public class SdListen implements TibrvMsgCallback {
	String daemon = "ssl:localhost:7500";
	String username = null;
	String password = null;

	public SdListen(String args[]) {
		// parse arguments
		int i = get_InitParams(args);

		// we must have at least one subject
		if ((i >= args.length) || (username == null) || (password == null)) {
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

		TibrvRvdTransport transport = null;
		try {
			TibrvSdContext.setDaemonCert(TIBRV_SECURE_DAEMON_ANY_NAME, TIBRV_SECURE_DAEMON_ANY_CERT);
			TibrvSdContext.setUserNameWithPassword(username, password);
			transport = new TibrvRvdTransport(null, null, daemon);
		} catch (TibrvException e) {
			System.err.println("Failed to create TibrvRvdTransport:");
			e.printStackTrace();
			System.exit(0);
		}

		// Create listeners for specified subjects
		while (i < args.length) {

			try {
				new TibrvListener(Tibrv.defaultQueue(), this, transport, args[i], null);
				System.err.println("Listening on: " + args[i]);
			} catch (TibrvException e) {
				System.err.println("Failed to create listener:");
				e.printStackTrace();
				System.exit(0);
			}
			i++;
		}

		while (true) {
			try {
				Tibrv.defaultQueue().dispatch();
			} catch (TibrvException e) {
				System.err.println("Exception dispatching default queue:");
				e.printStackTrace();
				System.exit(0);
			} catch (InterruptedException ie) {
				System.exit(0);
			}
		}
	}

	@Override
	public void onMsg(TibrvListener listener, TibrvMsg msg) {
		System.out.println("Subject: " + msg.getSendSubject());
		System.out.println("Message: " + msg);
		System.out.flush();
	}

	// print usage information and quit
	void usage() {
		System.err.println("Usage: java SdListen ");
		System.err
				.println("       -user <user> -password <password> <subject-list>");
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
		new SdListen(args);
	}
}
