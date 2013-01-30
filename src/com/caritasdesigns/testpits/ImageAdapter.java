package com.caritasdesigns.testpits;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<ImageModel> images;
	
	public ImageAdapter(Context context, List<ImageModel> images){
		this.mContext = context;
		this.images = images;
		Log.d("ImageAdapter","constructor is called: " + images);
	}
	
	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {  // if it's not recycled, initialize some attributes
		    imageView = new ImageView(mContext);
		    imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
		    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		    imageView.setPadding(8, 8, 8, 8);
		} else {
		    imageView = (ImageView) convertView;
		}
		Bitmap bitmap = this.loadBitmapAtPosition(position);
		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	private Bitmap loadBitmapAtPosition(int position){
		
		Bitmap bitmap = BitmapFactory.decodeFile("/data/data/com.caritasdesigns.testpits/app_Images/" + this.images.get(position).getPath());
		Log.d("ImageAdapter","load bitmap: " + bitmap);
		return bitmap;
	}
	
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}
	
}
