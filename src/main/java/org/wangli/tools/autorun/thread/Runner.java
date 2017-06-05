package org.wangli.tools.autorun.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Callable;

import org.wangli.tools.autorun.entity.Host;
import org.wangli.tools.autorun.entity.Operation;
import org.wangli.tools.autorun.log.Logger;
import org.wangli.tools.autorun.util.Const;

public class Runner implements Callable<Boolean> {

	// Host
	private Host host;

	// Operation
	private Operation operation;

	// Parameters
	private String parameters;

	// 当前Execution的结果文件夹
	private String rsltPath;

	// 是否创建新的连接
	private boolean useNewConn;

	public Runner(Host host, Operation operation, String parameters, String rsltPath, boolean useNewConn) {
		this.host = host;
		this.operation = operation;
		this.parameters = parameters;
		this.rsltPath = rsltPath;
		this.useNewConn = useNewConn;
	}

	public Boolean call() throws Exception {
		boolean rslt = false;
		try {
			String cmd = this.operation.getCommand();
			if (this.parameters != null) {
				cmd = cmd + " " + this.parameters;
			}
			int execNum = this.operation.getExecNum();
			for (int i = 1; i <= execNum; ++i) {
				if (this.operation.isRebootBefore()) {
					this.host.reboot();
				}

				Thread.sleep(operation.getWaitBefore());

				List<String> ret = this.host.execCmd(cmd, useNewConn);
				// 结果文件路径
				String rsltFile = this.rsltPath + File.separator + this.operation.getId()
						+ Const.SEPARATOR_OPERATION_HOST_EXECNUM + this.host.getId()
						+ Const.SEPARATOR_OPERATION_HOST_EXECNUM + i;
				/*
				 * 写结果文件
				 */
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(rsltFile));
					// 写命令
					bw.write(cmd);
					bw.newLine();
					bw.write(Const.SEPARATOR_COMMAND_RETURN);
					bw.newLine();
					// 写返回信息
					for (int j = 0; j < ret.size(); ++j) {
						bw.write(ret.get(j));
						bw.newLine();
					}
					bw.flush();
				} catch (Exception e) {
					Logger.logErrInfo(this.getClass(),
							"Error while writing result of command " + cmd + " to file " + rsltFile + ".", e);
					// 抛出异常
					throw e;
				} finally {
					if (bw != null) {
						bw.close();
					}
				}

				Thread.sleep(operation.getWaitAfter());

				if (this.operation.isRebootAfter()) {
					this.host.reboot();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		rslt = true;
		return rslt;
	}

}
