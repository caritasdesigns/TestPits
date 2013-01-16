package com.caritasdesigns.testpits;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class TestpitAdapter extends ArrayAdapter<TestpitModel> {
	private Context context;
	private int layoutResourceID;
	private List<TestpitModel> testpits = null;
	
	
	public TestpitAdapter(Context context, int layoutResourceID, List<TestpitModel> testpits){
		super(context, layoutResourceID, testpits);
		Log.d("testpitAdapter","Beginning of Constructor");
		this.layoutResourceID = layoutResourceID;
        this.context = context;
        this.testpits = testpits;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("testpitAdapter","Beginning of getView");
	    testpitHolder holder;
		View row = convertView;
		TestpitModel testpit = testpits.get(position);
		
	    if(row == null)
	    {
	    	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	        row = inflater.inflate(layoutResourceID, null);		    
	        holder = new testpitHolder();
	        holder.testpitName = (TextView)row.findViewById(R.id.testpitName);
	        
		    holder.testpitName.setText(testpit.getName());
		    row.setTag(holder);
	    }
	    else
	    {
	    	holder = (testpitHolder)row.getTag();
	    }

	    return row;
	}
	    
	static class testpitHolder
	{
		public TextView testpitName = null;
	}
}
