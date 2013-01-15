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

public class ProjectAdapter extends ArrayAdapter<ProjectModel> {
	private Context context;
	private int layoutResourceID;
	private List<ProjectModel> projects = null;
	
	
	public ProjectAdapter(Context context, int layoutResourceID, List<ProjectModel> projects){
		super(context, layoutResourceID, projects);
		Log.d("ProjectAdapter","Beginning of Constructor");
		this.layoutResourceID = layoutResourceID;
        this.context = context;
        this.projects = projects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("ProjectAdapter","Beginning of getView");
	    ProjectHolder holder;
		View row = convertView;
		ProjectModel project = projects.get(position);
		
	    if(row == null)
	    {
	    	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	        row = inflater.inflate(layoutResourceID, null);		    
	        holder = new ProjectHolder();
	        holder.projectName = (TextView)row.findViewById(R.id.projectName);
	        holder.client = (TextView)row.findViewById(R.id.client);
	        
		    holder.projectName.setText(project.getName());
		    holder.client.setText(project.getClient());
		    row.setTag(holder);
	    }
	    else
	    {
	    	holder = (ProjectHolder)row.getTag();
	    }

	    return row;
	}
	    
	static class ProjectHolder
	{
		public TextView projectName = null;
		public TextView client = null;
	}
}
