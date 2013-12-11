package com.tibco.tibrv.labs.labb;

// TIBCO Rendezvous API Java Development
// File: OpenRV.java
// TIBCO Education Services
// Copyright 2005 - TIBCO Software Inc.
// ALL RIGHTS RESERVED

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import com.tibco.tibrv.*;

public class OpenRV {
	static TibrvRvdTransport conn;
	static String service;
	static String network;
	static String daemon;

	public void openTib() throws TibrvException {
		Tibrv.open(IMPL_NATIVE);
		System.out.println("Valid: " + Tibrv.isValid());
		System.out.println("Version: " + Tibrv.getVersion());
		System.out.println("Native: " + Tibrv.isNativeImpl());
	}

	public void connectRv() throws TibrvException {
		try {
			conn = new TibrvRvdTransport();
			if (conn.isValid()) {
				System.out.println(" Opened Connection to RVD ");
				conn.setDescription("MyTest");
				Thread.sleep(60000);
			} else {
				System.out.println("Not connected to RVD");
			}
		} catch (InterruptedException tib1x) {
			tib1x.printStackTrace();
		}
	}

	public void destroyRv() throws TibrvException {
		conn.destroy();
		System.out.println("Transport Destroyed");
		if (!conn.isValid())
			System.out.println("Rvd Transport is no longer valid");
	}

	public void closeTib() throws TibrvException {
		Tibrv.close();
		if (!Tibrv.isValid())
			System.out.println("Tibrv machinery is no longer valid");
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

	public static void main(String... args) throws TibrvException {
		OpenRV orv = new OpenRV();
		orv.openTib();
		orv.connectRv();
		orv.checkRvd();
		orv.destroyRv();
		orv.closeTib();
	}

}
