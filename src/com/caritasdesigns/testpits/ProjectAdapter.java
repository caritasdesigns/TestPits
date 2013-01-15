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

public class ProjectAdapter extends ArrayAdapter<Project> {
	private Context context;
	private int layoutResourceID;
	private List<Project> projects = null;
	
	
	public ProjectAdapter(Context context, int layoutResourceID, List<Project> projects){
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
		Project project = projects.get(position);
		
	    if(row == null)
	    {
	    	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	        row = inflater.inflate(layoutResourceID, null);		    
	        holder = new ProjectHolder();
	        holder.pid = (TextView)row.findViewById(R.id.projectId);
	        holder.projectName = (TextView)row.findViewById(R.id.projectName);
	        holder.client = (TextView)row.findViewById(R.id.client);
	        
		    holder.pid.setText(Integer.toString(project.getId()));
		    holder.projectName.setText(project.getName());
		    holder.client.setText(project.getClient());
	        row.setId(project.getId());
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
		public TextView pid = null;
		public TextView projectName = null;
		public TextView client = null;
	}
}
