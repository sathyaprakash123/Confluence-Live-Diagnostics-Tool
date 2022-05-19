package com.ConfDiagnostic.Meta;

public class PieChartData {
	
	private String id;
	private String name;
	private Integer yaxis;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getYaxis() {
		return yaxis;
	}
	public void setYaxis(Integer yaxis) {
		this.yaxis = yaxis;
	}
	@Override
	public String toString() {
		return "PieChartData [id=" + id + ", name=" + name + ", yaxis=" + yaxis + "]";
	}
	
	

}
