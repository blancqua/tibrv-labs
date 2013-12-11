package com.tibco.tibrv.labs.labh;

//TIBCO Rendezvous API Java Development
//File: TimerRv.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvQueue;
import com.tibco.tibrv.TibrvTimer;
import com.tibco.tibrv.TibrvTimerCallback;

class TimerRv {
	static TibrvTimer timer1;
	static TibrvQueue queue;
	double seconds = 2.0;

	public static void main(String[] args) {
		new TimerRv();
	}

	TimerRv() {
		try {
			Tibrv.open();
			setTimer(seconds);
			Tibrv.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	void setTimer(double seconds) throws TibrvException, InterruptedException {
		queue = Tibrv.defaultQueue();
		timer1 = new TibrvTimer(queue, new TimerCallback(this), seconds, null);
		new TibrvDispatcher(queue).join();
	}
}

class TimerCallback implements TibrvTimerCallback {
	TimerRv t;

	TimerCallback(TimerRv trv) {
		t = trv;
	}

	int count;

	@Override
	public void onTimer(TibrvTimer timerEvent) {
		System.out.println(++count + " Timer Expired.");
	}
}
