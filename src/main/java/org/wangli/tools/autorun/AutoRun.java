package org.wangli.tools.autorun;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.wangli.tools.autorun.config.ConfigManager;
import org.wangli.tools.autorun.entity.ExecOper;
import org.wangli.tools.autorun.entity.Execution;
import org.wangli.tools.autorun.entity.Host;
import org.wangli.tools.autorun.log.Logger;
import org.wangli.tools.autorun.thread.Operator;
import org.wangli.tools.autorun.thread.Rebooter;
import org.wangli.tools.autorun.thread.ThreadManager;
import org.wangli.tools.autorun.util.DateUtil;

public class AutoRun {

	/**
	 * 重启所有主机
	 * 
	 * @return
	 */
	private static boolean rebootHosts() throws Exception {
		boolean rslt = false;

		Map<String, Host> hosts = ConfigManager.getHosts();
		Set<String> keys = hosts.keySet();
		Iterator<String> hostIds = keys.iterator();

		List<Future<Boolean>> futures = new LinkedList<Future<Boolean>>();
		List<String> hostIds4Info = new LinkedList<String>();

		while (hostIds.hasNext()) {
			String hostId = hostIds.next();
			Host host = hosts.get(hostId);

			// 起一线程重启
			Rebooter rebooter = new Rebooter(host);
			Future<Boolean> future = ThreadManager.getThreadPool().submit(rebooter);
			futures.add(future);
			hostIds4Info.add(hostId);
		}

		// 等待所有主机重启结束
		for (int i = 0; i < futures.size(); ++i) {
			Future<Boolean> future = futures.get(i);
			if (!future.get()) {
				Logger.logErrInfo(AutoRun.class, "Failed to reboot host " + hostIds4Info.get(i) + ".");
			}
		}

		return rslt;
	}

	/**
	 * 断开所有主机连接
	 * 
	 * @return
	 */
	private static boolean disconnectHosts() {
		boolean rslt = true;

		Map<String, Host> hosts = ConfigManager.getHosts();
		Set<String> hostKeys = hosts.keySet();
		Iterator<String> hostIds = hostKeys.iterator();
		while (hostIds.hasNext()) {
			String hostId = hostIds.next();
			Host host = hosts.get(hostId);
			host.disconnect();
		}

		return rslt;
	}

	public static void main(String[] args) {

		try {
			Logger.logSysInfo(AutoRun.class, "Start to run...");

			/*
			 * 加载配置
			 */
			try {
				Logger.logSysInfo(AutoRun.class, "Load configuration...");
				ConfigManager.loadXmlConfig();
				Logger.logSysInfo(AutoRun.class, "Load configuration...Done.");
			} catch (Exception e) {
				Logger.logErrInfo(AutoRun.class, "Error while loading configuration.", e);
				throw e;
			}

			/*
			 * 创建结果文件夹
			 */
			String baseRsltPath = "result" + File.separator + DateUtil.transferMs2String(System.currentTimeMillis());
			File rsltDirFile = new File(baseRsltPath);
			rsltDirFile.mkdirs();

			/*
			 * 执行所有Execution
			 */
			List<Execution> executions = ConfigManager.getExecutions();

			for (int i = 0; i < executions.size(); i++) {
				Execution execution = executions.get(i);

				// 创建当前Execution的结果文件夹
				String execRsltPath = baseRsltPath + File.separator + execution.getId();
				File execRsltFile = new File(execRsltPath);
				execRsltFile.mkdirs();

				// 执行次数
				int execNum = 0;
				// 次结果目录编号
				int timeRsltDir = execution.getTimeDirStart();
				while (execNum < execution.getExecNum()) {
					execNum++;
					String execInfo = "(" + (i + 1) + "/" + executions.size() + ", " + execNum + "/"
							+ execution.getExecNum() + ", " + execution.getId() + ")";
					Logger.logSysInfo(AutoRun.class, execInfo + " will be executed...");

					// 创建本次执行的结果文件夹
					String thisRsltPath = execRsltPath + File.separator + (timeRsltDir++);
					File thisRsltFile = new File(thisRsltPath);
					thisRsltFile.mkdirs();

					if (execution.isRebootBefore()) {
						try {
							Logger.logSysInfo(AutoRun.class,
									"Reboot hosts before executing " + execution.getId() + "...");
							// 重启所有主机
							rebootHosts();
							Logger.logSysInfo(AutoRun.class,
									"Reboot hosts before executing " + execution.getId() + "...Done.");
						} catch (Exception e) {
							Logger.logErrInfo(AutoRun.class,
									"Error while rebooting hosts before executing " + execution.getId() + ".", e);
							throw e;
						}
					}

					// 该Execution的所有Operation
					List<ExecOper> execOpers = execution.getExecOpers();

					/*
					 * 执行该Execution的所有Operation
					 */
					// 所有并行操作的返回结果
					List<Future<Boolean>> parOpFutrues = new LinkedList<Future<Boolean>>();
					for (int j = 0; j < execOpers.size(); ++j) {
						ExecOper execOper = execOpers.get(j);

						// 创建执行该操作的线程
						Operator operator = new Operator(execOper, thisRsltPath);

						if (execOper.isParallel()) {
							// 并行
							Future<Boolean> opFuture = ThreadManager.getThreadPool().submit(operator);
							parOpFutrues.add(opFuture);
						} else {
							// 串行
							operator.doOperation();
						}
					}

					// 等待所有并行操作执行完
					for (int j = 0; j < parOpFutrues.size(); ++j) {
						Future<Boolean> parOpFutrue = parOpFutrues.get(j);
						parOpFutrue.get();
					}

					if (execution.isRebootAfter()) {
						try {
							Logger.logSysInfo(AutoRun.class,
									"Reboot hosts after executing " + execution.getId() + "...");
							// 重启所有主机
							rebootHosts();
							Logger.logSysInfo(AutoRun.class,
									"Reboot hosts after executing " + execution.getId() + "...Done.");
						} catch (Exception e) {
							Logger.logErrInfo(AutoRun.class,
									"Error while rebooting hosts after executing " + execution.getId() + ".", e);
							throw e;
						}
					}

					Logger.logSysInfo(AutoRun.class, execInfo + " has been executed.");
				}
			}

			Logger.logSysInfo(AutoRun.class, "All executions have been finished.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 断开所有主机连接
			disconnectHosts();
		}

	}

}
