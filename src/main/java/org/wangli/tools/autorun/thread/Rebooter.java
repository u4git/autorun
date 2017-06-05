package org.wangli.tools.autorun.thread;

import java.util.concurrent.Callable;

import org.wangli.tools.autorun.entity.Host;

public class Rebooter implements Callable<Boolean> {

	// Host
	private Host host;

	public Rebooter(Host host) {
		this.host = host;
	}

	public Boolean call() throws Exception {
		return this.host.reboot();
	}

}
