package org.wangli.tools.autorun.config;

public class XmlConfigConst {

	/*
	 * Tag
	 */
	public static final String TAG_HOSTS = "hosts";
	public static final String TAG_HOSTS_HOST = "host";
	public static final String TAG_HOSTS_HOST_ID = "id";
	public static final String TAG_HOSTS_HOST_HOSTNAME = "hostname";
	public static final String TAG_HOSTS_HOST_PORT = "port";
	public static final String TAG_HOSTS_HOST_USERNAME = "username";
	public static final String TAG_HOSTS_HOST_PASSWORD = "password";
	public static final String TAG_HOSTS_HOST_RECONNUM = "recon-num";
	public static final String TAG_HOSTS_HOST_RECONWAIT = "recon-wait";
	public static final String TAG_HOSTS_HOST_SYSTEM = "system";
	public static final String TAG_HOSTS_HOST_SYSTEM_CLASS = "class";
	public static final String TAG_HOSTS_HOST_WAITTIMEAFTERREBOOT = "waitTime-afterReboot";

	public static final String TAG_OPERATIONS = "operations";
	public static final String TAG_OPERATIONS_OPERATION = "operation";
	public static final String TAG_OPERATIONS_OPERATION_ID = "id";
	public static final String TAG_OPERATIONS_OPERATION_HOSTIDS = "host-ids";
	public static final String TAG_OPERATIONS_OPERATION_HOSTIDS_HOSTID = "host-id";
	public static final String TAG_OPERATIONS_OPERATION_COMMAND = "command";
	public static final String TAG_OPERATIONS_OPERATION_EXECNUM = "exec-num";
	public static final String TAG_OPERATIONS_OPERATION_REBOOTBEFORE = "reboot-before";
	public static final String TAG_OPERATIONS_OPERATION_REBOOTAFTER = "reboot-after";
	public static final String TAG_OPERATIONS_OPERATION_WAITBEFORE = "wait-before";
	public static final String TAG_OPERATIONS_OPERATION_WAITAFTER = "wait-after";

	public static final String TAG_EXECUTIONS = "executions";
	public static final String TAG_EXECUTIONS_EXECUTION = "execution";
	public static final String TAG_EXECUTIONS_EXECUTION_ID = "id";
	public static final String TAG_EXECUTIONS_EXECUTION_OPERATIONS = "operations";
	public static final String TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION = "operation";
	public static final String TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_ID = "id";
	public static final String TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_PARAMETERS = "parameters";
	public static final String TAG_EXECUTIONS_EXECUTION_OPERATIONS_OPERATION_ISPARALLEL = "isParallel";
	public static final String TAG_EXECUTIONS_EXECUTION_EXECNUM = "exec-num";
	public static final String TAG_EXECUTIONS_EXECUTION_TIMEDIRSTART = "time-dir-start";
	public static final String TAG_EXECUTIONS_EXECUTION_REBOOTBEFORE = "reboot-before";
	public static final String TAG_EXECUTIONS_EXECUTION_REBOOTAFTER = "reboot-after";

	public static final String TAG_SYSTEM = "system";
	public static final String TAG_SYSTEM_SYSCLASS = "sys-class";
	public static final String TAG_SYSTEM_ERRORREGEXS = "error-regexs";
	public static final String TAG_SYSTEM_ERRORREGEXS_ISENABLED = "isEnabled";
	public static final String TAG_SYSTEM_ERRORREGEXS_ERRORREGEX = "error-regex";

	/*
	 * Value
	 */
	public static final String VALUE_TRUE = "true";

}
