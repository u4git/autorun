package org.wangli.tools.autorun.entity;

import java.util.List;

public class Execution {

	private String id;

	private List<ExecOper> execOpers;

	private int execNum;

	private int timeDirStart = 1;

	private boolean rebootBefore;

	private boolean rebootAfter;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ExecOper> getExecOpers() {
		return execOpers;
	}

	public void setExecOpers(List<ExecOper> execOpers) {
		this.execOpers = execOpers;
	}

	public int getExecNum() {
		return execNum;
	}

	public void setExecNum(int execNum) {
		this.execNum = execNum;
	}

	public int getTimeDirStart() {
		return timeDirStart;
	}

	public void setTimeDirStart(int timeDirStart) {
		this.timeDirStart = timeDirStart;
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

}
