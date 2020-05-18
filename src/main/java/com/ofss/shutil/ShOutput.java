package com.ofss.shutil;

import java.util.UUID;

public class ShOutput {

	int result;
	String output;
	UUID uuid;
	
	public ShOutput() {
		super();
	}

	public ShOutput(int result, String output, UUID uuid) {
		super();
		this.result = result;
		this.output = output;
		this.uuid = uuid;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid2) {
		this.uuid = uuid2;
	}
	
	
}
