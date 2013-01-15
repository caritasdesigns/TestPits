package com.caritasdesigns.testpits;

import android.util.Log;

public class Project {

	private String name, client;
	private int id;
	
	public Project(int id, String projectName, String projectClient){
		Log.d("Project","Beginning of Constructor");
		this.id = id;
		this.name = projectName;
		this.client = projectClient;
	}
	
	
	//Setter Methods
	public void setId(int id){
		this.id = id;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setClient(String client){
		this.client = client;
	}

	
	//Getter Methods
	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
	public String getClient(){
		return this.client;
	}
}
