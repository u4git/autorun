package org.wangli.tools.autorun.system.impl;

import org.wangli.tools.autorun.system.ISystem;

public class Windows implements ISystem {

	public String getPingCmd(String hostname, int num) {
		return "ping " + hostname + " -n " + num;
	}

	public String getPingRsltPattern() {
		return ".*=\\d+\\s+.*[=|<]\\d+ms\\s+TTL=\\d+";
	}

	public String getRebootCmd() {
		return "shutdown -r";
	}

}
