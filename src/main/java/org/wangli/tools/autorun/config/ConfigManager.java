package org.wangli.tools.autorun.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.wangli.tools.autorun.entity.ExecOper;
import org.wangli.tools.autorun.entity.Execution;
import org.wangli.tools.autorun.entity.Host;
import org.wangli.tools.autorun.entity.Operation;
import org.wangli.tools.autorun.system.ISystem;

public class ConfigManager {

	private static String configXmlPath = "autorun.xml";

	// 所有主机
	private static Map<String, Host> hosts = new HashMap<String, Host>();

	// 所有操作
	private static Map<String, Operation> operations = new HashMap<String, Operation>();

	// 所有执行
	private static List<Execution> executions = new LinkedList<Execution>();

	// 系统类
	private static ISystem system;

	// 是否使用异常信息正则表达式，当发现错误时立即返回
	private static boolean errRegexsEnabled = false;

	// 异常信息正则表达式
	private static List<Pattern> errPtns = new LinkedList<Pattern>();

	/**
	 * 加载配置信息
	 * 
	 * @throws Exception
	 */
	public static void loadXmlConfig() throws Exception {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document document = saxBuilder.build(configXmlPath);
		Element rootElem = document.getRootElement();

		/*
		 * hosts
		 */
		Element hostsElem = rootElem.getChild(XmlConfigConst.TAG_HOSTS);
		List<Element> hostElems = hostsElem.getChildren(XmlConfigConst.TAG_HOSTS_HOST);
		for (int i = 0; i < hostElems.size(); ++i) {
			Element hostElem = hostElems.get(i);
			// id
			String id = hostElem.getAttributeValue(XmlConfigConst.TAG_HOSTS_HOST_ID).trim();
			// hostname
			String hostname = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_HOSTNAME);
			// port
			String port_s = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_PORT);
			int port = Integer.valueOf(port_s);
			// username
			String username = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_USERNAME);
			// password
			String password = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_PASSWORD);
			// recon-num
			String reconNum_s = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_RECONNUM);
			int reconNum = Integer.valueOf(reconNum_s);
			// recon-wait
			String reconWait_s = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_RECONWAIT);
			long reconWait = Long.valueOf(reconWait_s) * 1000;
			// system
			Element systemElem = hostElem.getChild(XmlConfigConst.TAG_HOSTS_HOST_SYSTEM);
			String sysClass = systemElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_SYSTEM_CLASS);
			ISystem system = (ISystem) Class.forName(sysClass).newInstance();

			// waitTime-afterReboot
			String waitTimeAfterReboot_s = hostElem.getChildTextTrim(XmlConfigConst.TAG_HOSTS_HOST_WAITTIMEAFTERREBOOT);
			int waitTimeAfterReboot = Integer.valueOf(waitTimeAfterReboot_s);

			Host host = new Host();
			host.setId(id);
			host.setHostname(hostname);
			host.setPort(port);
			host.setUsername(username);
			host.setPassword(password);
			host.setReconNum(reconNum);
			host.setReconWait(reconWait);
			host.setSystem(system);
			host.setWaitTimeAfterReboot(waitTimeAfterReboot);

			hosts.put(host.getId(), host);
		}

		/*
		 * operations
		 */
		Element operationsElem = rootElem.getChild(XmlConfigConst.TAG_OPERATIONS);
		List<Element> operationElems = operationsElem.getChildren(XmlConfigConst.TAG_OPERATIONS_OPERATION);
		for (int i = 0; i < operationElems.size(); ++i) {
			Element operationElem = operationElems.get(i);
			// id
			String id = operationElem.getAttributeValue(XmlConfigConst.TAG_OPERATIONS_OPERATION_ID).trim();
			// host-ids
			Element hostIdsElem = operationElem.getChild(XmlConfigConst.TAG_OPERATIONS_OPERATION_HOSTIDS);
			List<Element> hostIdElems = hostIdsElem.getChildren(XmlConfigConst.TAG_OPERATIONS_OPERATION_HOSTIDS_HOSTID);
			List<String> hostIds = new LinkedList<String>();
			for (int j = 0; j < hostIdElems.size(); ++j) {
				Element hostIdElem = hostIdElems.get(j);
				String hostId = hostIdElem.getTextTrim();
				hostIds.add(hostId);
			}
			// command
			String command = operationElem.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_COMMAND);
			// exec-num
			String execNum_s = operationElem.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_EXECNUM);
			int execNum = Integer.valueOf(execNum_s);
			// reboot-before
			String rebootBefore_s = operationElem
					.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_REBOOTBEFORE);
			boolean rebootBefore = false;
			if (XmlConfigConst.VALUE_TRUE.equalsIgnoreCase(rebootBefore_s)) {
				rebootBefore = true;
			}
			// reboot-after
			String rebootAfter_s = operationElem.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_REBOOTAFTER);
			boolean rebootAfter = false;
			if (XmlConfigConst.VALUE_TRUE.equalsIgnoreCase(rebootAfter_s)) {
				rebootAfter = true;
			}
			// wait-before
			String waitBefore_s = operationElem.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_WAITBEFORE);
			long waitBefore = Integer.valueOf(waitBefore_s) * 1000;
			// wait-after
			String waitAfter_s = operationElem.getChildTextTrim(XmlConfigConst.TAG_OPERATIONS_OPERATION_WAITAFTER);
			long waitAfter = Integer.valueOf(waitAfter_s) * 1000;

			Operation operation = new Operation();
			operation.setId(id);
			operation.setHostIds(hostIds);
			operation.setCommand(command);
			operation.setExecNum(execNum);
			operation.setRebootBefore(rebootBefore);
			operation.setRebootAfter(rebootAfter);
			operation.setWaitBefore(waitBefore);
			operation.setWaitAfter(waitAfter);

			operations.put(operation.getId(), operation);
		}

		/*
		 * executions
		 */
		Element executionsElem = rootElem.getChild(XmlConfigConst.TAG_EXECUTIONS);
		List<Element> executionElems = executionsElem.getChildren(XmlConfigConst.TAG_EXECUTIONS_EXECUTION);
		for (int i = 0; i < executionElems.size(); ++i) {
			Element executionElem = executionElems.get(i);
			// id
			String id = executionElem.getAttributeValue(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_ID).trim();
			// operations
			Element execOpersElem = executionElem.getChild(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_OPERATIONS);
			List<Element> execOperElems = execOpersElem
					.getChildren(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION);
			List<ExecOper> execOpers = new LinkedList<ExecOper>();
			for (int j = 0; j < execOperElems.size(); ++j) {
				Element execOperElem = execOperElems.get(j);
				// isParallel
				String isParallel_s = execOperElem
						.getAttributeValue(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_ISPARALLEL)
						.trim();
				boolean isParallel = false;
				if (XmlConfigConst.VALUE_TRUE.equalsIgnoreCase(isParallel_s)) {
					isParallel = true;
				}
				// id
				Element execOperIdElem = execOperElem
						.getChild(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_ID);
				String execOperId = execOperIdElem.getTextTrim();
				// parameters
				Element execOperParamElem = execOperElem
						.getChild(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_PARAMETERS);
				String execOperParams = execOperParamElem.getTextTrim();

				ExecOper execOper = new ExecOper();
				execOper.setParallel(isParallel);
				execOper.setId(execOperId);
				execOper.setParameters(execOperParams);

				execOpers.add(execOper);
			}
			// exec-num
			String execNum_s = executionElem.getChildTextTrim(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_EXECNUM);
			int execNum = Integer.valueOf(execNum_s);
			// time-dir-start
			String timeDirStart_s = executionElem
					.getChildTextTrim(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_TIMEDIRSTART);
			int timeDirStart = Integer.valueOf(timeDirStart_s);
			// reboot-before
			String rebootBefore_s = executionElem
					.getChildTextTrim(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_REBOOTBEFORE);
			boolean rebootBefore = false;
			if (XmlConfigConst.VALUE_TRUE.equalsIgnoreCase(rebootBefore_s)) {
				rebootBefore = true;
			}
			// reboot-after
			String rebootAfter_s = executionElem.getChildTextTrim(XmlConfigConst.TAG_EXECUTIONS_EXECUTION_REBOOTAFTER);
			boolean rebootAfter = false;
			if (XmlConfigConst.VALUE_TRUE.equalsIgnoreCase(rebootAfter_s)) {
				rebootAfter = true;
			}

			Execution execution = new Execution();
			execution.setId(id);
			execution.setExecOpers(execOpers);
			execution.setExecNum(execNum);
			execution.setTimeDirStart(timeDirStart);
			execution.setRebootBefore(rebootBefore);
			execution.setRebootAfter(rebootAfter);

			executions.add(execution);
		}

		/*
		 * System
		 */
		Element systemElem = rootElem.getChild(XmlConfigConst.TAG_SYSTEM);
		// sys-class
		String sysClass = systemElem.getChildTextTrim(XmlConfigConst.TAG_SYSTEM_SYSCLASS);
		system = (ISystem) Class.forName(sysClass).newInstance();

		Element errRegexsElem = systemElem.getChild(XmlConfigConst.TAG_SYSTEM_ERRORREGEXS);
		// isEnabled
		String isEnabled_s = errRegexsElem.getAttributeValue(XmlConfigConst.TAG_SYSTEM_ERRORREGEXS_ISENABLED).trim();
		if (XmlConfigConst.VALUE_TRUE.equals(isEnabled_s)) {
			errRegexsEnabled = true;
		}
		// error-regexs
		List<Element> errRegexElems = errRegexsElem.getChildren(XmlConfigConst.TAG_SYSTEM_ERRORREGEXS_ERRORREGEX);
		for (int i = 0; i < errRegexElems.size(); i++) {
			Element errRegexElem = errRegexElems.get(i);
			String errRegex = errRegexElem.getTextTrim();
			Pattern errPtn = Pattern.compile(errRegex);
			errPtns.add(errPtn);
		}
	}

	public static Map<String, Host> getHosts() {
		return hosts;
	}

	public static Map<String, Operation> getOperations() {
		return operations;
	}

	public static List<Execution> getExecutions() {
		return executions;
	}

	public static ISystem getSystem() {
		return system;
	}

	public static boolean isErrRegexsEnabled() {
		return errRegexsEnabled;
	}

	public static List<Pattern> getErrPtns() {
		return errPtns;
	}

}
