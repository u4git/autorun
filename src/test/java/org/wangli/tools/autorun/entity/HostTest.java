package org.wangli.tools.autorun.entity;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wangli.tools.autorun.config.ConfigManager;
import org.wangli.tools.autorun.system.impl.Linux;
import org.wangli.tools.autorun.system.impl.Windows;

public class HostTest {

	@Before
	public void loadConfig() throws Exception {
		ConfigManager.loadXmlConfig();
	}

	@Test
	public void TestExecCmd() throws Exception {
		Host host = new Host();
		host.setId("h1");
		host.setHostname("172.16.80.253");
		host.setPort(22);
		host.setUsername("root");
		host.setPassword("123456");

		int times = 100;

		for (int i = 0; i < times; ++i) {
			String cmd = "uname -r";
			List<String> ret1 = host.execCmd(cmd, false);
			String info = i + ": ";
			for (int j = 0; j < ret1.size(); ++j) {
				info = info + ret1.get(j);
			}
			System.out.println(info);
		}

		for (int i = 0; i < times; ++i) {
			String cmd = "uname -a";
			List<String> ret1 = host.execCmd(cmd, true);
			String info = i + ": ";
			for (int j = 0; j < ret1.size(); ++j) {
				info = info + ret1.get(j);
			}
			System.out.println(info);
		}
	}

	@Test
	public void TestPing() throws Exception {
		Host host = new Host();
		host.setId("h1");
		host.setHostname("172.16.80.253");
		host.setPort(22);
		host.setUsername("root");
		host.setPassword("123456");
		host.setSystem(new Windows());

		int num = 1;
		while (true) {
			System.out.println((num++) + ": " + host.ping());
		}
	}

	@Test
	public void TestReboot() throws Exception {
		Host host = new Host();
		host.setId("h1");
		host.setHostname("172.16.80.253");
		host.setPort(22);
		host.setUsername("root");
		host.setPassword("123456");
		host.setSystem(new Linux());

		System.out.println(host.reboot());
	}

}
