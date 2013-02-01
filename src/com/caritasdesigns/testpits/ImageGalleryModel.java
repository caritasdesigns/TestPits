package com.caritasdesigns.testpits;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageGalleryModel implements Serializable {

	private ArrayList<ImageModel> imageList;
	
	public ImageGalleryModel(ArrayList<ImageModel> imageList)
	{
		this.imageList = imageList;
	}
	
	
	public ArrayList<ImageModel> getImageList()
	{
		return imageList;
	}

	public void setImageList(ArrayList<ImageModel> imageList)
	{
		this.imageList = imageList;

	}
	
	public ImageModel getModelAtPosition(int position)
	{
		return imageList.get(position);
	}
}
