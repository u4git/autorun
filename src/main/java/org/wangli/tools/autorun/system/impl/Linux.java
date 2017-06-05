package org.wangli.tools.autorun.system.impl;

import org.wangli.tools.autorun.system.ISystem;

public class Linux implements ISystem {

	public String getPingCmd(String hostname, int num) {
		return "ping -c " + num + " " + hostname;
	}

	public String getPingRsltPattern() {
		return ".*icmp_seq=\\d+\\s+ttl=\\d+\\s+time=[0-9.]+\\s*ms";
	}

	public String getRebootCmd() {
		return "reboot";
	}

}
