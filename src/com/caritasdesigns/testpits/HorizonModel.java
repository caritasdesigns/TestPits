package com.caritasdesigns.testpits;

import android.util.Log;

public class HorizonModel {

	private String name;
	private int id;
	
	public HorizonModel(int id, String horizonName){
		Log.d("HorizonModel","Beginning of Constructor");
		this.id = id;
		this.name = horizonName;
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
