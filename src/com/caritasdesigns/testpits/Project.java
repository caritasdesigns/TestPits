package com.caritasdesigns.testpits;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Project extends Activity{

	private static Mode projectMode;
	private Button button, viewTestpits, addTestpit, viewMWs, addMW, clearLocation, setLocation, mapLocation, viewPictures, addPicture;
	private LinearLayout testpitButtonGroup, MWButtonGroup, pictureButtonGroup;
	private EditText projectName, client;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String projectID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project);
		clearLocation = (Button) findViewById(R.id.clearLocation);
		setLocation = (Button) findViewById(R.id.setLocation);
		mapLocation = (Button) findViewById(R.id.mapLocation);
		testpitButtonGroup = (LinearLayout) findViewById(R.id.testpitsButtonGroup);
		MWButtonGroup = (LinearLayout) findViewById(R.id.MWButtonGroup);
		pictureButtonGroup = (LinearLayout) findViewById(R.id.pictureButtonGroup);
		button = (Button) findViewById(R.id.addUpdateProject);
		projectName = (EditText) findViewById(R.id.projectName);
		client = (EditText) findViewById(R.id.client);
		projectName.setOnKeyListener(this.createOnKeyListener(projectName));
		client.setOnKeyListener(this.createOnKeyListener(client));		
		switch(projectMode){
			case PROJECT_CREATE_MODE:
				//Hide some fields
				this.projectCreateMode();
				break;
			case PROJECT_UPDATE_MODE:
			case PROJECT_READ_MODE:
				//Prepopulate the fields
				this.prepopUpdateFields();
				//Set the Mode
				this.projectReadMode();		
				break;
			default:
				Log.d("ProjectLoadView","onClick'd issue with Mode: "+ projectMode);
			break;
		}
		//Set OnClickListeners
		this.setOnClickListeners();
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
		dbHelper = new DbHelper(Project.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertProject","method insertProject is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.P_NAME, projectName.getText().toString());
		values.put(DbHelper.P_CLIENT, client.getText().toString());
		
		//Insert into Database  (returns -1 if error, else projectID)
		projectID = Long.toString(db.insert(DbHelper.TABLE_PROJECTS, null, values));
		//Close Database
		dbHelper.close();
		db.close();
        hideKeyboard(projectName);
        hideKeyboard(client);
		this.projectReadMode();
	}

	private void updateProject(){
		//Open Database
		dbHelper = new DbHelper(Project.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertProject","method insertProject is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.P_NAME, projectName.getText().toString());
		values.put(DbHelper.P_CLIENT, client.getText().toString());
		
		//Insert into Database
		db.update(DbHelper.TABLE_PROJECTS, values, DbHelper.P_ID+"="+projectID, null);
		//Close Database
		dbHelper.close();
		db.close();	
	}

	public static void setProjectID(long id)
	{
		projectID = String.valueOf(id);

	}
	
	private void prepopUpdateFields()
	{
		dbHelper = new DbHelper(Project.this);
		db = dbHelper.getReadableDatabase();
		
		String[] columns = new String[]{"name","client"};
		Cursor cursor = db.query(DbHelper.TABLE_PROJECTS, columns, DbHelper.P_ID+"="+projectID, null, null, null, null);
		Log.d("PrePopVals","Count: "+cursor.getCount());
		if(cursor.getCount()!=0){
			cursor.moveToFirst();
			projectName.setText(cursor.getString(cursor.getColumnIndex("name")));
			client.setText(cursor.getString(cursor.getColumnIndex("client")));
		}
		
		Log.d("PrePopVals","Client:  "+client.getText().toString()+"Name: "+projectName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	private void projectCreateMode()
	{	
		projectMode = Mode.PROJECT_CREATE_MODE;	
		this.button.setText("Add Project");
		this.setLocation.setText("Set Location");
		this.setButtonVisibility();
	}
	
	private void projectReadMode()
	{		
		this.button.setText("Edit");

		projectMode = Mode.PROJECT_READ_MODE;
		this.setButtonVisibility();
		this.client.setFocusable(false);
		this.client.setFocusableInTouchMode(false);
		this.client.setEnabled(false);
		this.projectName.setFocusable(false);
		this.projectName.setFocusableInTouchMode(false);
		this.projectName.setEnabled(false);
	}
	
	private void projectUpdateMode()
	{
		button.setText("Okay");
		projectMode = Mode.PROJECT_UPDATE_MODE;
		this.setButtonVisibility();
		this.client.setEnabled(true);
		this.client.setFocusable(true);
		this.client.setFocusableInTouchMode(true);
		this.projectName.setEnabled(true);
		this.projectName.setFocusable(true);
		this.projectName.setFocusableInTouchMode(true);
	}
	
	private void setButtonVisibility(){
		switch(projectMode){
			case PROJECT_CREATE_MODE:
				this.clearLocation.setVisibility(View.GONE);
				this.mapLocation.setVisibility(View.GONE);
				this.testpitButtonGroup.setVisibility(View.GONE);
				this.MWButtonGroup.setVisibility(View.GONE);
				this.pictureButtonGroup.setVisibility(View.GONE);
				break;
			case PROJECT_UPDATE_MODE:
			case PROJECT_READ_MODE:
				this.clearLocation.setVisibility(View.VISIBLE);
				this.mapLocation.setVisibility(View.VISIBLE);
				this.testpitButtonGroup.setVisibility(View.VISIBLE);
				this.MWButtonGroup.setVisibility(View.VISIBLE);
				this.pictureButtonGroup.setVisibility(View.VISIBLE);
				break;
			default:
				Log.d("addUpdateProject","onClick'd issue with Mode: "+ projectMode);
				break;
		}
	}
	
	private OnKeyListener createOnKeyListener(final EditText editText)
	{
		return new OnKeyListener()
		{
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                    hideKeyboard(editText);
		                    return true;
		                default:
		                    break;
		            }
		        }
		        return false;
		    }
		};
	}
	
	private void hideKeyboard(EditText editText)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private void setOnClickListeners(){
		//Listen for the button click.
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				switch(projectMode){
					case PROJECT_CREATE_MODE:
						insertProject();
						break;
					case PROJECT_UPDATE_MODE:
						updateProject();
						projectReadMode();
						break;
					case PROJECT_READ_MODE:
						projectUpdateMode();
						break;
					default:
						Log.d("addUpdateProject","onClick'd issue with Mode: "+ projectMode);
						break;
				}
			}
		});
		//Set OnClick Listener for "View Testpits" button
		viewTestpits = (Button) findViewById(R.id.viewTestpits);
		viewTestpits.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), TestpitList.class);
				intent.putExtra("projectID", projectID);
				Log.d("putExtra","added intent.putExtra: 'projectID' = "+ projectID);
				v.getContext().startActivity(intent);
				Log.d("viewTestpit","onClick'd with viewTestpitButton: "+ R.id.viewTestpits);
			}
		});
		//Set OnClick Listener for "Add Testpit" button
		addTestpit = (Button) findViewById(R.id.addTestpit);
		addTestpit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Testpit.class);
				intent.putExtra("projectID", projectID);
				Log.d("putExtra","added intent.putExtra: 'projectID' = "+ projectID);
				Testpit.setMode(Mode.TESTPIT_CREATE_MODE);
				v.getContext().startActivity(intent);
				Log.d("viewTestpit","onClick'd with viewTestpitButton: "+ R.id.addTestpit);
			}
		});
		//Set OnClick Listener for "View Monitor Wells" button
		viewMWs = (Button) findViewById(R.id.viewMWs);
		viewMWs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), TestpitList.class);
				intent.putExtra("MWID", projectID);
				v.getContext().startActivity(intent);
				Log.d("viewTestpit","onClick'd with viewTestpitButton: "+ R.id.viewTestpits);
			}
		});
		//Set OnClick Listener for "Add Monitor Well" button
		addMW = (Button) findViewById(R.id.addMW);
		addMW.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Testpit.class);
				intent.putExtra("MWID", projectID);
				Testpit.setMode(Mode.TESTPIT_CREATE_MODE);
				v.getContext().startActivity(intent);
				Log.d("viewTestpit","onClick'd with viewTestpitButton: "+ R.id.addTestpit);
			}
		});
		
		//Set OnClick Listener for "Add Pictures" button
		addPicture = (Button) findViewById(R.id.addPicture);
	}
}
