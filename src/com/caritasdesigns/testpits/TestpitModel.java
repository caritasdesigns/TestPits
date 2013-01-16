package com.caritasdesigns.testpits;

import android.util.Log;

public class TestpitModel {

	private String name;
	private int id;
	
	public TestpitModel(int id, String testpitName){
		Log.d("TestpitModel","Beginning of Constructor");
		this.id = id;
		this.name = testpitName;
	}
	
	
	//Setter Methods
	public void setId(int id){
		this.id = id;
	}
	public void setName(String name){
		this.name = name;
	}
	
	//Getter Methods
	public int getId(){
		return this.id;
	}
	public String getName(){
		return this.name;
	}
}
