package com.caritasdesigns.testpits;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;


public class GestureListener extends GestureDetector.SimpleOnGestureListener {
	
	public enum GestureMode {
		LEFT_SWIPE, RIGHT_SWIPE, UP_SWIPE, DOWN_SWIPE
	}
	
	private static final int SWIPE_MIN_DISTANCE = 150;
	private static final int SWIPE_MAX_OFF_PATH = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private static GestureMode gestureMode = null;



	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		float dX = e2.getX()-e1.getX();
		float dY = e1.getY()-e2.getY();
		
		if (Math.abs(dY)<SWIPE_MAX_OFF_PATH &&Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&Math.abs(dX)>=SWIPE_MIN_DISTANCE ) 
		{
			if (dX>0) 
			{
//				Toast.makeText, "Right Swipe", Toast.LENGTH_SHORT).show();
				Log.d("Gesture","Right Swipe");
				setGestureMode(GestureMode.RIGHT_SWIPE);
			}
			else 
			{
				Log.d("Gesture","Left Swipe");
				setGestureMode(GestureMode.LEFT_SWIPE);
			}
			return true;
		}
		else if (Math.abs(dX)<SWIPE_MAX_OFF_PATH &&Math.abs(velocityY)>=SWIPE_THRESHOLD_VELOCITY &&Math.abs(dY)>=SWIPE_MIN_DISTANCE ) 
		{
			if (dY>0) 
			{
				Log.d("Gesture","Up Swipe");
				setGestureMode(GestureMode.UP_SWIPE);

			}
			else 
			{
				Log.d("Gesture","Down Swipe");
				setGestureMode(GestureMode.DOWN_SWIPE);

			}
		
			return true;
		}
		
		return false;
	}
//	@Override
//	public boolean onSingleTapUp(MotionEvent ev) {
//		Log.d("onSingleTapUp",ev.toString());
//		return true;
//	}
//	@Override
//	public void onShowPress(MotionEvent ev) {
//		Log.d("onShowPress",ev.toString());
//	}	
//	@Override
//	public void onLongPress(MotionEvent ev) {
//		Log.d("onLongPress",ev.toString());
//	}
//	@Override
//	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//		Log.d("onScroll",e1.toString());
//		return true;
//	}
//	@Override
//	public boolean onDown(MotionEvent ev) {
//		Log.d("onDownd",ev.toString());
//		return true;
//	}


	public static GestureMode getGestureMode() {
		return gestureMode;
	}


	public static void setGestureMode(GestureMode gestureMode) {
		GestureListener.gestureMode = gestureMode;
	}
}
