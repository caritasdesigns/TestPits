package com.caritasdesigns.testpits;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ProjectList extends Activity {

	private Button addProject;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private List<Project> projectList;
	private ListView list;
	private ProjectAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		//Set View
		setContentView(R.layout.activity_project);
		
		//Open Database
		dbHelper = new DbHelper(ProjectList.this);
//		db = dbHelper.getWritableDatabase();
//		dbHelper.resetDB(db);
		projectList = this.getProjectsFromDb();
		adapter = new ProjectAdapter(this, R.layout.project_list_item, projectList);
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
	
		list.setClickable(true);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Project o = projectList.get(position);
				ProjectUpdate.setMode(Mode.PROJECT_UPDATE_MODE);
				ProjectUpdate.setProjectID(o.getId());
				Intent intent = new Intent(view.getContext(), ProjectUpdate.class);
				view.getContext().startActivity(intent);
				Log.d("ProjectAdd","onClick'd with addProjectButton: "+ R.id.addProject);	
			}
			
		});
		
		addProject = (Button) findViewById(R.id.addProject);
		addProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectUpdate.setMode(Mode.PROJECT_ADD_MODE);
				Intent intent = new Intent(v.getContext(), ProjectUpdate.class);
				v.getContext().startActivity(intent);
				Log.d("ProjectAdd","onClick'd with addProjectButton: "+ R.id.addProject);
			}
			
			
		});

	//Close Database
		db.close();
		dbHelper.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		projectList = this.getProjectsFromDb();

		adapter = new ProjectAdapter(this, R.layout.project_list_item, projectList);
		list.setAdapter(adapter);
		Log.d("ProjectList","onResume is running" + adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private List<Project> getProjectsFromDb(){
		List<Project> list = new ArrayList<Project>();
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{"_id","name","client"};
		
		Cursor cursor = db.query(DbHelper.TABLE, columns, null, null, null, null, null);
		if(cursor.getCount() != 0){
			while( cursor.moveToNext()) {
				Log.d("AddToList","Item ID: "+ cursor.getInt(0));
				int id = cursor.getInt(0);
				list.add(new Project(id, cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("client"))));
//				list.add(new Project(cursor.getInt(0), cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("client"))));
			}
		}
		return list;
	}

}
