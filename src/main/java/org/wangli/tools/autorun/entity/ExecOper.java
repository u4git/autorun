package org.wangli.tools.autorun.entity;

public class ExecOper {

	// id
	private String id;

	// parameters
	private String parameters;

	// isParallel
	private boolean isParallel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public boolean isParallel() {
		return isParallel;
	}

	public void setParallel(boolean isParallel) {
		this.isParallel = isParallel;
	}

}
