package com.caritasdesigns.testpits;

import com.caritasdesigns.testpits.GestureListener.GestureMode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageViewer extends Activity{

	private ImageGalleryModel imageGalleryModel;
	private int currentPosition;
	private ImageView imageView;
	private GestureDetector gDetector;
	private GestureListener gListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.image_viewer);
		gListener = new GestureListener();
		gDetector = new GestureDetector(this, gListener);
		
		imageView = (ImageView)findViewById(R.id.image);

		
		//Get Extras
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
		    return;
		    }
		// Get data via the key
		this.imageGalleryModel = (ImageGalleryModel) extras.getSerializable("imageGalleryModel");
		this.currentPosition = extras.getInt("currentPosition");
		
		imageView.setImageBitmap(this.loadBitmapAtPosition(currentPosition));
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	  if (gDetector.onTouchEvent(event)){
			Log.d("Gesture","True");
			if(gListener.getGestureMode() == GestureMode.RIGHT_SWIPE)
			{
				this.handleRightSwipe();
			}
			else if(gListener.getGestureMode() == GestureMode.LEFT_SWIPE)
			{
				this.handleLeftSwipe();
			}
			
			return true;	
	  }
	  else
	  {
	    return false;
	  }
	}
	
	private void handleRightSwipe() {
		if(imageGalleryModel.getImageList().size()>1)
		{
			currentPosition--;
			Log.d("Gesture","CurrentPosition = " + currentPosition);
			currentPosition = currentPosition % imageGalleryModel.getImageList().size();
			if (currentPosition < 0) {
				currentPosition += imageGalleryModel.getImageList().size();
			}
			Log.d("Gesture","CurrentPosition after Mod = " + currentPosition);
			imageView.setImageBitmap(this.loadBitmapAtPosition(currentPosition));
		}
	}
	
	private void handleLeftSwipe() {
		if(imageGalleryModel.getImageList().size()>1)
		{
			currentPosition++;
			currentPosition = currentPosition % imageGalleryModel.getImageList().size();
			imageView.setImageBitmap(this.loadBitmapAtPosition(currentPosition));
		}
	}

	private Bitmap loadBitmapAtPosition(int position){
		
		Bitmap bitmap = BitmapFactory.decodeFile("/data/data/com.caritasdesigns.testpits/app_Images/" + this.imageGalleryModel.getModelAtPosition(position).getPath());
		Log.d("ImageAdapter","load bitmap: " + bitmap);
		return bitmap;
	}

	
	
	/**
	 *  OnGestureListener Methods
	 */
//	@Override
//	public boolean onDown(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
//			float arg3) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onLongPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
//			float arg3) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
