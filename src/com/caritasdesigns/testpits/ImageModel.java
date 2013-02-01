package com.caritasdesigns.testpits;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


public class ImageModel implements Serializable{
	private String path;
	
	
	public ImageModel(String path){
		this.path = path;
	}
	
	public ImageModel(Parcel source)
	{
		path = source.readString();
	}
	
	
	//Setter Methods
	public void setPath(String path){
		this.path = path;
	}
	
	//Getter Methods
	public String getPath(){
		return this.path;
	}


//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}


//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//
//		dest.writeString(path);
//	}
}
