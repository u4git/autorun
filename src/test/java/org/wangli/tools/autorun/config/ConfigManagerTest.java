package org.wangli.tools.autorun.config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.wangli.tools.autorun.entity.ExecOper;
import org.wangli.tools.autorun.entity.Execution;
import org.wangli.tools.autorun.entity.Host;
import org.wangli.tools.autorun.entity.Operation;

public class ConfigManagerTest {

	private String sp1 = "====================";

	private String enter = "\n";

	private String tab = "\t";

	@Test
	public void testLoadConfig() throws Exception {
		ConfigManager.loadXmlConfig();

		/*
		 * hosts
		 */
		String hostsHead = "Hosts" + this.enter + this.sp1 + this.enter;
		System.out.println(hostsHead);

		Map<String, Host> hosts = ConfigManager.getHosts();
		Set<String> hostKeys = hosts.keySet();
		Iterator<String> hostIds = hostKeys.iterator();
		while (hostIds.hasNext()) {
			String hostId = hostIds.next();
			Host host = hosts.get(hostId);
			String hostInfo = host.getId() + this.enter;
			hostInfo = hostInfo + host.getHostname() + this.tab + host.getPort() + this.enter;
			hostInfo = hostInfo + host.getUsername() + this.tab + host.getPassword() + this.enter;
			hostInfo = hostInfo + host.getSystem().getClass().getName() + this.enter;
			hostInfo = hostInfo + host.getWaitTimeAfterReboot() + this.enter;
			System.out.println(hostInfo);
		}
		System.out.println();

		/*
		 * operations
		 */
		String operationsHead = "Operations" + this.enter + this.sp1 + this.enter;
		System.out.println(operationsHead);

		Map<String, Operation> operations = ConfigManager.getOperations();
		Set<String> operationKeys = operations.keySet();
		Iterator<String> operationIds = operationKeys.iterator();
		while (operationIds.hasNext()) {
			String operationId = operationIds.next();
			Operation operation = operations.get(operationId);
			String operationInfo = operation.getId() + this.enter;

			List<String> opHostIds = operation.getHostIds();
			for (int i = 0; i < opHostIds.size(); ++i) {
				String opHostId = opHostIds.get(i);
				operationInfo = operationInfo + opHostId + this.tab;
			}
			operationInfo = operationInfo + this.enter;

			operationInfo = operationInfo + operation.getCommand() + this.enter;
			operationInfo = operationInfo + operation.getExecNum() + this.enter;
			operationInfo = operationInfo + operation.isRebootBefore() + this.tab + operation.isRebootAfter()
					+ this.enter;
			operationInfo = operationInfo + operation.getWaitAfter() + this.enter;

			System.out.println(operationInfo);
		}
		System.out.println();

		/*
		 * executions
		 */
		String executionHead = "Executions" + this.enter + this.sp1 + this.enter;
		System.out.println(executionHead);

		List<Execution> executions = ConfigManager.getExecutions();
		for (int i = 0; i < executions.size(); ++i) {
			Execution execution = executions.get(i);
			String executionInfo = execution.getId() + this.enter;

			List<ExecOper> execOpers = execution.getExecOpers();
			for (int j = 0; j < execOpers.size(); ++j) {
				ExecOper execOper = execOpers.get(j);
				String execOperId = execOper.getId();
				boolean isParallel = execOper.isParallel();
				String execOperParam = execOper.getParameters();
				executionInfo = executionInfo + execOperId + "(" + execOperParam + ")/" + isParallel + "/" + this.tab;
			}
			executionInfo = executionInfo + this.enter;
			executionInfo = executionInfo + execution.getExecNum() + this.enter;
			executionInfo = executionInfo + execution.getTimeDirStart() + this.enter;
			executionInfo = executionInfo + execution.isRebootBefore() + this.tab + execution.isRebootAfter()
					+ this.enter;

			System.out.println(executionInfo);
		}

		/*
		 * system
		 */
		String systemHead = "System" + this.enter + this.sp1 + this.enter;
		System.out.println(systemHead);
		String systemInfo = ConfigManager.getSystem().getClass().getName() + this.enter;
		systemInfo = ConfigManager.isErrRegexsEnabled() + this.enter;
		List<Pattern> errPtns = ConfigManager.getErrPtns();
		for (int i = 0; i < errPtns.size(); i++) {
			Pattern errPtn = errPtns.get(i);
			String errRegex = errPtn.toString();
			systemInfo = systemInfo + errRegex + this.enter;
		}
		System.out.println(systemInfo);
	}

	@Test
	public void testErrPtns() throws Exception {
		ConfigManager.loadXmlConfig();

		List<String> rslts = new LinkedList<String>();
		rslts.add("[Stage 240:============================================>           (8 + 2) / 10]");
		rslts.add("                                                                                ");
		rslts.add(
				"Exception in thread \"main\" org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://master:9000/SparkBench/PageRank/Output already exists");
		rslts.add("	at org.apache.hadoop.mapred.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:132)");
		rslts.add(
				"[Stage 0:=================>                                        (3 + 7) / 10]16/12/23 00:45:06 ERROR TaskSchedulerImpl: Lost executor 0 on host253: worker lost");
		rslts.add(
				"16/12/23 00:45:18 WARN TaskSetManager: Lost task 3.0 in stage 0.0 (TID 2, host253): ExecutorLostFailure (executor 0 exited caused by one of the running tasks) Reason: worker lost");

		List<Pattern> errPtns = ConfigManager.getErrPtns();

		for (int i = 0; i < rslts.size(); i++) {
			String rslt = rslts.get(i);

			String rsltInfo = rslt + this.enter;

			for (int j = 0; j < errPtns.size(); j++) {
				Pattern errPtn = errPtns.get(j);
				Matcher errMch = errPtn.matcher(rslt);
				boolean matched = false;
				if (errMch.matches()) {
					matched = true;
				}
				rsltInfo = rsltInfo + this.tab + errPtn.pattern() + this.tab + matched + this.enter;
			}

			System.out.println(rsltInfo);
		}
	}

	@Test
	public void testErrPtns2() throws Exception {
		List<String> rslts = new LinkedList<String>();
		rslts.add("[Stage 240:============================================>           (8 + 2) / 10]");
		rslts.add("                                                                                ");
		rslts.add(
				"Exception in thread \"main\" org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://master:9000/SparkBench/PageRank/Output already exists");
		rslts.add("	at org.apache.hadoop.mapred.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:132)");

		List<Pattern> errPtns = ConfigManager.getErrPtns();
		errPtns.add(Pattern.compile("\\s*Exception in thread\\s+\\\"\\s*[a-zA-Z0-9]+\\s*\\\"\\s+.*"));
		errPtns.add(Pattern.compile("\\s+at\\s+[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+.*"));

		for (int i = 0; i < rslts.size(); i++) {
			String rslt = rslts.get(i);

			String rsltInfo = rslt + this.enter;

			for (int j = 0; j < errPtns.size(); j++) {
				Pattern errPtn = errPtns.get(j);
				Matcher errMch = errPtn.matcher(rslt);
				boolean matched = false;
				if (errMch.matches()) {
					matched = true;
				}
				rsltInfo = rsltInfo + this.tab + errPtn.pattern() + this.tab + matched + this.enter;
			}

			System.out.println(rsltInfo);
		}
	}

}
