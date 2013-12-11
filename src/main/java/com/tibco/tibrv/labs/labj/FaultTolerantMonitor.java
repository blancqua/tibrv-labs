package com.tibco.tibrv.labs.labj;

import static com.tibco.tibrv.Tibrv.IMPL_NATIVE;

import java.net.InetAddress;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvFtMonitor;
import com.tibco.tibrv.TibrvFtMonitorCallback;
import com.tibco.tibrv.TibrvRvdTransport;

public class FaultTolerantMonitor implements TibrvFtMonitorCallback {

	public FaultTolerantMonitor(TibrvRvdTransport connection) throws Exception {
		new TibrvFtMonitor(Tibrv.defaultQueue(), this, connection, "FtSend." + InetAddress.getLocalHost(), 3.0, null);
	}

	private void start() throws TibrvException, InterruptedException {
		while(true) {
			Tibrv.defaultQueue().dispatch();
		}
	}

	@Override
	public void onFtMonitor(TibrvFtMonitor monitor, String groupName, int numActiveMembers) {
		System.out.println(groupName + "[" + numActiveMembers + "]");
	}

	public static void main(String[] args) throws Exception {
		Tibrv.open(IMPL_NATIVE);
		TibrvRvdTransport connection = new TibrvRvdTransport();

		new FaultTolerantMonitor(connection).start();
	}

}
