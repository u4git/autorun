package org.wangli.tools.autorun.system;

public interface ISystem {

	/**
	 * 获取PING命令
	 * 
	 * @param hostname
	 * @param num
	 * @return
	 */
	public String getPingCmd(String hostname, int num);

	/**
	 * 获取PING命令结果的格式
	 * 
	 * @return
	 */
	public String getPingRsltPattern();

	/**
	 * 获取重启命令
	 * 
	 * @return
	 */
	public String getRebootCmd();

}
