package com.caritasdesigns.testpits;


public class ImageModel {
	private String path;
	
	public ImageModel(String path){
		this.path = path;
	}
	
	
	//Setter Methods
	public void setPath(String path){
		this.path = path;
	}
	
	//Getter Methods
	public String getPath(){
		return this.path;
	}
}
