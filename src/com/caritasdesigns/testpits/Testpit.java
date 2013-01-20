package com.caritasdesigns.testpits;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

public class Testpit extends Activity{

	private static Mode testpitMode;
	private Button button, addHorizon, viewHorizons, setLocation, mapLocation, clearLocation;
	private EditText testpitName;
	private LinearLayout horizonButtonGroup;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String testpitID, projectID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testpit);
		//Get Extras
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		// Get data via the key
		projectID = extras.getString("projectID");
		Testpit.setProjectID(projectID);
		testpitID = extras.getString("testpitID");
		setTestpitID(testpitID);
		
		this.testpitName = (EditText) findViewById(R.id.testpitName);
		this.testpitName.setOnKeyListener(this.createOnKeyListener(testpitName));
		this.button = (Button) findViewById(R.id.addUpdateTestpit);
		this.setLocation = (Button) findViewById(R.id.setLocation);
		this.mapLocation = (Button) findViewById(R.id.mapLocation);
		this.clearLocation = (Button) findViewById(R.id.clearLocation);
		this.horizonButtonGroup = (LinearLayout) findViewById(R.id.horizonButtonGroup);
		
		switch(testpitMode){
			case TESTPIT_CREATE_MODE:
				testpitCreateMode();
				break;
			case TESTPIT_UPDATE_MODE:
			case TESTPIT_READ_MODE:
				//Prepopulate the fields
				this.prepopUpdateFields();
				this.testpitReadMode();		
		}
		this.setOnClickListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static void setMode(Mode mode) {
		testpitMode = mode;
	}

	private void insertTestpit(){
		//Open Database
		dbHelper = new DbHelper(Testpit.this);
		db = dbHelper.getWritableDatabase();
		Log.d("inserttestpit","method inserttestpit is called with testpitName: " + testpitName.getText().toString() + " and projectID: " + projectID);
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.TP_NAME, testpitName.getText().toString());
		values.put(DbHelper.TP_PROJECTID, projectID);
		//Insert into Database  (returns -1 if error, else testpitID)
		testpitID = Long.toString(db.insert(DbHelper.TABLE_TESTPITS, null, values));
		//Close Database
		dbHelper.close();
		db.close();
        hideKeyboard(testpitName);
		this.testpitReadMode();
	}

	private void updateTestpit(){
		//Open Database
		dbHelper = new DbHelper(Testpit.this);
		db = dbHelper.getWritableDatabase();
		Log.d("inserttestpit","method inserttestpit is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.TP_NAME, testpitName.getText().toString());
		
		//Insert into Database
		db.update(DbHelper.TABLE_TESTPITS, values, DbHelper.TP_ID+"="+testpitID, null);
		//Close Database
		dbHelper.close();
		db.close();
	}

	public static void setTestpitID(String id)
	{
		testpitID = id;
	}
	public static void setProjectID(String id)
	{
		projectID = id;
	}
	
	private void prepopUpdateFields()
	{
		dbHelper = new DbHelper(Testpit.this);
		db = dbHelper.getReadableDatabase();
		
		String[] columns = new String[]{"name"};
		Cursor cursor = db.query(DbHelper.TABLE_TESTPITS, columns, DbHelper.TP_ID+"="+testpitID, null, null, null, null);
		Log.d("PrePopVals","Count: "+cursor.getCount());
		if(cursor.getCount()!=0){
			cursor.moveToFirst();
			testpitName.setText(cursor.getString(cursor.getColumnIndex(DbHelper.TP_NAME)));
		}
		
		Log.d("PrePopVals","Name: "+testpitName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	private void testpitCreateMode()
	{	
		testpitMode = Mode.TESTPIT_CREATE_MODE;
		this.setButtonVisibility();
		this.button.setText("Add Testpit");
		this.setLocation.setText("Set Location");
	}
	private void testpitReadMode()
	{		
		button.setText("Edit");

		testpitMode = Mode.TESTPIT_READ_MODE;
		this.setButtonVisibility();
		this.testpitName.setFocusable(false);
		this.testpitName.setFocusableInTouchMode(false);
		this.testpitName.setEnabled(false);
	}
	
	private void testpitUpdateMode()
	{
		button.setText("Okay");
		testpitMode = Mode.TESTPIT_UPDATE_MODE;
		this.setButtonVisibility();
		this.testpitName.setEnabled(true);
		this.testpitName.setFocusable(true);
		this.testpitName.setFocusableInTouchMode(true);
	}
	
	private void setButtonVisibility(){
		Log.d("setButtonVisibility","with testpitMode: "+ testpitMode);
		switch(testpitMode){
			case TESTPIT_CREATE_MODE:
				this.clearLocation.setVisibility(View.GONE);
				this.mapLocation.setVisibility(View.GONE);
				this.horizonButtonGroup.setVisibility(View.GONE);
				break;
			case TESTPIT_UPDATE_MODE:
			case TESTPIT_READ_MODE:
				this.clearLocation.setVisibility(View.VISIBLE);
				this.mapLocation.setVisibility(View.VISIBLE);
				this.horizonButtonGroup.setVisibility(View.VISIBLE);
				break;
			default:
				Log.d("addUpdateProject","onClick'd issue with Mode: "+ testpitMode);
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
		this.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(testpitMode){
					case TESTPIT_CREATE_MODE:
						insertTestpit();
						break;
					case TESTPIT_UPDATE_MODE:
						updateTestpit();
						testpitReadMode();
						break;
					case TESTPIT_READ_MODE:
						testpitUpdateMode();
						break;
					default:
						Log.d("addUpdatetestpit","onClick'd issue with Mode: "+ testpitMode);
						break;
				}
			}
		});
		//View Horizons is Clicked
		viewHorizons = (Button) findViewById(R.id.viewHorizons);
		viewHorizons.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), HorizonList.class);
				intent.putExtra("testpitID", testpitID);
				Log.d("putExtra","added intent.putExtra: 'testpitID' = "+ testpitID);
				v.getContext().startActivity(intent);
			}
		});
		//Add Horizon is Clicked
		addHorizon = (Button) findViewById(R.id.addHorizon);
		addHorizon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Horizon.class);
				intent.putExtra("testpitID", testpitID);
				Log.d("putExtra","added intent.putExtra: 'testpitID' = "+ testpitID);
				Horizon.setMode(Mode.HORIZON_CREATE_MODE);
				v.getContext().startActivity(intent);
			}
		});
	}
}
