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


public class HorizonAdapter extends ArrayAdapter<HorizonModel> {
	private Context context;
	private int layoutResourceID;
	private List<HorizonModel> horizons = null;
	
	
	public HorizonAdapter(Context context, int layoutResourceID, List<HorizonModel> horizons){
		super(context, layoutResourceID, horizons);
		Log.d("horizonAdapter","Beginning of Constructor");
		this.layoutResourceID = layoutResourceID;
        this.context = context;
        this.horizons = horizons;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("horizonAdapter","Beginning of getView");
	    horizonHolder holder;
		View row = convertView;
		HorizonModel horizon = horizons.get(position);
		
	    if(row == null)
	    {
	    	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	        row = inflater.inflate(layoutResourceID, null);		    
	        holder = new horizonHolder();
	        holder.horizonName = (TextView)row.findViewById(R.id.horizonName);
	        
		    holder.horizonName.setText(horizon.getName());
		    row.setTag(holder);
	    }
	    else
	    {
	    	holder = (horizonHolder)row.getTag();
	    }

	    return row;
	}
	    
	static class horizonHolder
	{
		public TextView horizonName = null;
	}
}
