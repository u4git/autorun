package org.wangli.tools.autorun.thread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.wangli.tools.autorun.AutoRun;
import org.wangli.tools.autorun.config.ConfigManager;
import org.wangli.tools.autorun.entity.ExecOper;
import org.wangli.tools.autorun.entity.Host;
import org.wangli.tools.autorun.entity.Operation;
import org.wangli.tools.autorun.log.Logger;

public class Operator implements Callable<Boolean> {

	// 要执行的操作
	private ExecOper execOper;

	// 结果存放路径
	private String thisRsltPath;

	public Operator(ExecOper execOper, String thisRsltPath) {
		this.execOper = execOper;
		this.thisRsltPath = thisRsltPath;
	}

	private boolean doOperation(boolean isParallel) throws Exception {
		boolean rslt = false;

		// id
		String execOperId = execOper.getId();
		// parameters
		String execOperParam = execOper.getParameters();
		// operation
		Operation operation = ConfigManager.getOperations().get(execOperId);
		// 该Operation上的所有Host的id
		List<String> hostIds = operation.getHostIds();
		// 所有执行线程的返回结果
		List<Future<Boolean>> futures = new LinkedList<Future<Boolean>>();
		for (int k = 0; k < hostIds.size(); ++k) {
			String hostId = hostIds.get(k);
			// 拿到主机对象
			Host host = ConfigManager.getHosts().get(hostId);
			// 创建线程执行
			Runner runner = new Runner(host, operation, execOperParam, thisRsltPath, isParallel);
			Future<Boolean> future = ThreadManager.getThreadPool().submit(runner);
			futures.add(future);
		}
		// 检查所有线程是否正确结束
		for (int k = 0; k < futures.size(); ++k) {
			Future<Boolean> future = futures.get(k);
			try {
				future.get();
			} catch (Exception e) {
				Logger.logErrInfo(AutoRun.class, "Error while waiting for result of operation " + execOperId
						+ " on host " + hostIds.get(k) + ".", e);
				throw e;
			}
		}

		rslt = true;

		return rslt;
	}

	public Boolean call() throws Exception {
		return this.doOperation(true);
	}

	/**
	 * 在该Operation的所有Host上执行该Operation
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean doOperation() throws Exception {
		return this.doOperation(false);
	}

}
