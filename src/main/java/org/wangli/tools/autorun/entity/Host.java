package org.wangli.tools.autorun.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wangli.tools.autorun.config.ConfigManager;
import org.wangli.tools.autorun.log.Logger;
import org.wangli.tools.autorun.system.ISystem;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Host {

	private String id;

	private String hostname;

	private int port;

	private String username;

	private String password;

	// 重连次数
	private int reconNum;

	// 重连前等待的时间
	private long reconWait;

	private ISystem system;

	private int waitTimeAfterReboot;

	// 连接
	private Connection connection;

	/**
	 * 创建Connection
	 * 
	 * @return
	 * @throws Exception
	 */
	private Connection mkConnection() throws Exception {
		Logger.logDebugInfo(this.getClass(), "Create connection to host " + this.id + "...");
		/*
		 * 创建连接，默认重连3次
		 */
		Connection connection = null;
		for (int i = 0; i <= this.reconNum; i++) {
			try {
				connection = new Connection(this.hostname, this.port);
				connection.connect();
				// 验证身份
				boolean passed = connection.authenticateWithPassword(this.username, this.password);
				if (!passed) {
					connection = null;
					throw new Exception("Can not pass authentication on host " + this.id + " with username "
							+ this.username + " password " + this.password + ".");
				}
				Logger.logDebugInfo(this.getClass(),
						"Create connection to host " + this.id + "...Done. (" + i + "/" + this.reconNum + ")");
				break;
			} catch (Exception e) {
				Logger.logErrInfo(this.getClass(),
						"Error while creating connection to host " + this.id + ".(" + i + "/" + this.reconNum + ")");
				if (i >= this.reconNum) {
					throw e;
				}

				Thread.sleep(this.reconWait);
			}
		}
		return connection;
	}

	/**
	 * 判断读取的执行结果中是否有异常信息
	 * 
	 * @param line
	 * @return
	 */
	private boolean isError(String line) {
		boolean isError = false;
		List<Pattern> errPtns = ConfigManager.getErrPtns();
		for (int i = 0; i < errPtns.size(); i++) {
			Pattern errPtn = errPtns.get(i);
			Matcher errMch = errPtn.matcher(line);
			if (errMch.matches()) {
				isError = true;
				break;
			}
		}
		return isError;
	}

	/**
	 * 在当前主机上执行一条命令
	 * 
	 * @param cmd
	 * @param useNewConn
	 * @return
	 * @throws Exception
	 */
	public List<String> execCmd(String cmd, boolean useNewConn) throws Exception {
		List<String> ret = new LinkedList<String>();

		// 所用连接
		Connection connection = null;
		try {
			if (useNewConn) {
				// 使用新连接
				connection = this.mkConnection();
			} else {
				// 复用连接
				if (this.connection == null) {
					// 创建连接
					this.connection = this.mkConnection();
				}
				connection = this.connection;
			}

			// 开启会话
			Logger.logDebugInfo(this.getClass(), "Open session to host " + this.id + "...");
			Session session = connection.openSession();
			Logger.logDebugInfo(this.getClass(), "Open session to host " + this.id + "...Done.");

			// 执行命令
			Logger.logDebugInfo(this.getClass(), "Send command \"" + cmd + "\" to host " + this.id + "...");
			session.execCommand(cmd);
			Logger.logDebugInfo(this.getClass(), "Send command \"" + cmd + "\" to host " + this.id + "...Done.");

			// 读取结果
			BufferedReader br = null;
			try {
				Logger.logDebugInfo(this.getClass(),
						"Read result of command \"" + cmd + "\" from host " + this.id + "...");
				br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout())));
				String line = br.readLine();
				while (line != null) {
					ret.add(line);
					if (ConfigManager.isErrRegexsEnabled() && isError(line)) {
						ret.add("=====End with error.=====");
						Logger.logDebugInfo(this.getClass(), "Read result of command \"" + cmd + "\" from host "
								+ this.id + "...Ending with Error.");
						break;
					}
					line = br.readLine();
				}
				Logger.logDebugInfo(this.getClass(),
						"Read result of command \"" + cmd + "\" from host " + this.id + "...Done.");
			} catch (Exception e) {
				Logger.logErrInfo(this.getClass(),
						"Error while reading return of command \"" + cmd + "\" from host " + this.id + ".", e);
				throw e;
			} finally {
				if (br != null) {
					br.close();
				}
				if (session != null) {
					session.close();
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// 使用新连接需要用完即关，除非复用的全局连接没有初始化
			if (useNewConn) {
				if (connection != null) {
					if (this.connection == null) {
						// 留着以后复用
						this.connection = connection;
					} else {
						// 用完即关
						connection.close();
					}
				}
			}
		}

		return ret;
	}

	/**
	 * PING主机
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean ping() throws Exception {
		boolean rslt = false;

		Process process = null;
		BufferedReader br = null;
		try {
			process = Runtime.getRuntime().exec(ConfigManager.getSystem().getPingCmd(this.hostname, 1));
			process.waitFor();

			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Pattern pattern = Pattern.compile(ConfigManager.getSystem().getPingRsltPattern());
			String line = br.readLine();
			while (line != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.matches()) {
					rslt = true;
					break;
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			// 抛出异常
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (process != null) {
				process.destroy();
			}
		}

		return rslt;
	}

	/**
	 * 重启当前主机
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean reboot() throws Exception {
		boolean rslt = false;

		/*
		 * 发送重启命令
		 */
		Logger.logDebugInfo(this.getClass(),
				"Execute command \"" + this.system.getRebootCmd() + "\" on host " + this.id + " for reboot...");
		this.execCmd(this.system.getRebootCmd(), false);
		Logger.logDebugInfo(this.getClass(), "Execute command \"" + this.system.getRebootCmd() + "\" on host " + this.id
				+ " for reboot...Finished.");

		/*
		 * 期待PING不通
		 */
		Logger.logDebugInfo(this.getClass(), "Expect host " + this.id + " unreachable...");
		while (this.ping()) {
			// Nothing
		}
		Logger.logDebugInfo(this.getClass(), "Host " + this.id + " is unreachable.");

		/*
		 * 期待PING得通
		 */
		Logger.logDebugInfo(this.getClass(), "Expect host " + this.id + " reachable...");
		while (!this.ping()) {
			// Nothing
		}
		Logger.logDebugInfo(this.getClass(), "Host " + this.id + " is reachable.");

		// 连接置空，以便下次使用时重建连接
		this.connection = null;

		rslt = true;

		/*
		 * 重启之后等一段时间，让系统的各项服务有时间启动
		 */
		Thread.sleep(this.waitTimeAfterReboot * 1000);

		Logger.logDebugInfo(this.getClass(), "Reboot host " + this.id + " successfully.");

		return rslt;
	}

	public void disconnect() {
		if (this.connection != null) {
			this.connection.close();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setReconNum(int reconNum) {
		this.reconNum = reconNum;
	}

	public void setReconWait(long reconWait) {
		this.reconWait = reconWait;
	}

	public ISystem getSystem() {
		return system;
	}

	public void setSystem(ISystem system) {
		this.system = system;
	}

	public int getWaitTimeAfterReboot() {
		return waitTimeAfterReboot;
	}

	public void setWaitTimeAfterReboot(int waitTimeAfterReboot) {
		this.waitTimeAfterReboot = waitTimeAfterReboot;
	}

}
