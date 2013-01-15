package com.caritasdesigns.testpits;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ProjectUpdate extends Activity{

	private static Mode projectMode;
	private Button addUpdateProject;
	private EditText projectName;
	private EditText client;
	private EditText pid;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String projectID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Set View
		setContentView(R.layout.project_update);

		addUpdateProject = (Button) findViewById(R.id.addUpdateProject);
		projectName = (EditText) findViewById(R.id.projectName);
		client = (EditText) findViewById(R.id.client);
		pid = (EditText) findViewById(R.id.projectId);

		if(projectMode==Mode.PROJECT_UPDATE_MODE)
		{
			this.prepopUpdateFields();
		}
		
		addUpdateProject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switch(projectMode){
					case PROJECT_ADD_MODE:
						insertProject();
					case PROJECT_UPDATE_MODE:
						updateProject();
					default:
						Log.d("addUpdateProject","onClick'd issue with Mode: "+ projectMode);
				}
			}
			
			
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static void setMode(Mode mode) {
		projectMode = mode;
	}

	private void insertProject(){
		//Open Database
		dbHelper = new DbHelper(ProjectUpdate.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertProject","method insertProject is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.C_NAME, projectName.getText().toString());
		values.put(DbHelper.C_CLIENT, client.getText().toString());
		
		//Insert into Database
		db.insert(DbHelper.TABLE, null, values);
		//Close Database
		dbHelper.close();
		db.close();
		finish();
	}

	private void updateProject(){
		//Open Database
		dbHelper = new DbHelper(ProjectUpdate.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertProject","method insertProject is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.C_NAME, projectName.getText().toString());
		values.put(DbHelper.C_CLIENT, client.getText().toString());
		
		//Insert into Database
		db.update(DbHelper.TABLE, values, DbHelper.C_ID+"="+projectID, null);
		//Close Database
		dbHelper.close();
		db.close();
		finish();
	}
	
	public static void setProjectID(long id)
	{
		projectID = String.valueOf(id);

	}
	
	private void prepopUpdateFields()
	{
		dbHelper = new DbHelper(ProjectUpdate.this);
		db = dbHelper.getReadableDatabase();
		int theid;
		String sql = "SELECT * FROM projects WHERE " + DbHelper.C_ID + " = " + projectID;
		Log.d("PrePopVals","PID: "+this.pid);
		Log.d("PrePopVals","SQL: "+sql);
		Cursor cursor = db.rawQuery(sql, null);
		
		String[] columns = new String[]{DbHelper.C_ID, "name","client"};
//		Cursor cursor = db.query(DbHelper.TABLE, columns, DbHelper.C_ID+"="+projectID, null, null, null, null);
		Log.d("PrePopVals","Count: "+cursor.getCount());
		if(cursor.getCount()!=0){
			while( cursor.moveToNext()) {
				theid = cursor.getInt(0);
				Log.d("PrePopValsWhileLoop","theid: "+theid+"  || projectIde: "+projectID);		
				if(Integer.toString(theid) == projectID){
					projectName.setText(cursor.getString(cursor.getColumnIndex("name")));
					client.setText(cursor.getString(cursor.getColumnIndex("client")));
				}
			}
		}
		
		Log.d("PrePopVals","Client:  "+client.getText().toString()+"Name: "+projectName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	

}
