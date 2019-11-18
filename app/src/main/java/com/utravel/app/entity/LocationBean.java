package com.utravel.app.entity;

public class LocationBean {
	
    public int id;
	public String name;
	public int code;
	public int parent_id;
	@Override
	public String toString() {
		return "LocationBean [id=" + id + ", name=" + name + ", code=" + code
				+ ", parent_id=" + parent_id + "]";
	}
	
	
}
