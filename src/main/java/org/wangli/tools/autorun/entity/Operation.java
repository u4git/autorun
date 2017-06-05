package org.wangli.tools.autorun.entity;

import java.util.List;

public class Operation {

	private String id;

	private List<String> hostIds;

	private String command;

	private int execNum;

	private boolean rebootBefore;

	private boolean rebootAfter;

	private long waitBefore;

	private long waitAfter;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getHostIds() {
		return hostIds;
	}

	public void setHostIds(List<String> hostIds) {
		this.hostIds = hostIds;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getExecNum() {
		return execNum;
	}

	public void setExecNum(int execNum) {
		this.execNum = execNum;
	}

	public boolean isRebootBefore() {
		return rebootBefore;
	}

	public void setRebootBefore(boolean rebootBefore) {
		this.rebootBefore = rebootBefore;
	}

	public boolean isRebootAfter() {
		return rebootAfter;
	}

	public void setRebootAfter(boolean rebootAfter) {
		this.rebootAfter = rebootAfter;
	}

	public long getWaitBefore() {
		return waitBefore;
	}

	public void setWaitBefore(long waitBefore) {
		this.waitBefore = waitBefore;
	}

	public long getWaitAfter() {
		return waitAfter;
	}

	public void setWaitAfter(long waitAfter) {
		this.waitAfter = waitAfter;
	}

}
