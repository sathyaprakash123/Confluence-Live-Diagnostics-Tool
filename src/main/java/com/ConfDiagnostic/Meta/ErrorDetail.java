package com.ConfDiagnostic.Meta;

import java.util.ArrayList;

public class ErrorDetail {
	
	private String name;
	private ArrayList<String> timestamps;
	private ArrayList<String> stacktrace;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(ArrayList<String> timestamps) {
		this.timestamps = timestamps;
	}
	public ArrayList<String> getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(ArrayList<String> stacktrace) {
		this.stacktrace = stacktrace;
	}

}
