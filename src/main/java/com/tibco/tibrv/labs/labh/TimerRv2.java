package com.tibco.tibrv.labs.labh;

//TIBCO Rendezvous API Java Development
//File: TimerRv.java
//TIBCO Education Services
//Copyright 2005 - TIBCO Software Inc.
//ALL RIGHTS RESERVED

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Random;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvQueue;
import com.tibco.tibrv.TibrvQueueGroup;
import com.tibco.tibrv.TibrvTimer;
import com.tibco.tibrv.TibrvTimerCallback;

class TimerRv2 {
	static List<TibrvQueue> queues;
	private Random random = new Random();

	public static void main(String[] args) {
		new TimerRv2();
	}

	TimerRv2() {
		try {
			Tibrv.open();
			setTimer();
			Tibrv.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}
	}

	void setTimer() throws TibrvException, InterruptedException {
		queues = newArrayList(new TibrvQueue(), new TibrvQueue(), new TibrvQueue());
		TibrvQueueGroup queueGroup = new TibrvQueueGroup();
		for (TibrvQueue queue : queues) {
			queueGroup.add(queue);
			int seconds = random.nextInt(5) + 1;
			System.out.println("Creating Timer with period of " + seconds + "s");
			new TibrvTimer(queue, new TimerCallback2(this), seconds, null);
		}
		new TibrvDispatcher(queueGroup).join();
	}
}

class TimerCallback2 implements TibrvTimerCallback {
	TimerRv2 t;

	TimerCallback2(TimerRv2 trv) {
		t = trv;
	}

	int count;

	@Override
	public void onTimer(TibrvTimer timerEvent) {
		System.out.println(++count + " Timer Expired.");
	}
}
