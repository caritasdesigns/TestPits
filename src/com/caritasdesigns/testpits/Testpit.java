package com.caritasdesigns.testpits;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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

public class Testpit extends Activity{

	private static Mode testpitMode;
	private Button button;
	private EditText testpitName;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String testpitID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testpit);

		//Setup View
		button = (Button) findViewById(R.id.addUpdateTestpit);
		testpitName = (EditText) findViewById(R.id.testpitName);
		
		testpitName.setOnKeyListener(this.createOnKeyListener(testpitName));
		
		//Prepopulate the fields
		this.prepopUpdateFields();
		this.testpitReadMode();

		button.setOnClickListener(new OnClickListener() {
			
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
		Log.d("inserttestpit","method inserttestpit is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.TP_NAME, testpitName.getText().toString());
		
		//Insert into Database
		db.insert(DbHelper.TABLE_TESTPITS, null, values);
		//Close Database
		dbHelper.close();
		db.close();
		finish();
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

	public static void setTestpitID(long id)
	{
		testpitID = String.valueOf(id);

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
			testpitName.setText(cursor.getString(cursor.getColumnIndex("name")));
		}
		
		Log.d("PrePopVals","Name: "+testpitName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	private void testpitReadMode()
	{		
		button.setText("Edit");

		testpitMode = Mode.TESTPIT_READ_MODE;
		this.testpitName.setFocusable(false);
		this.testpitName.setFocusableInTouchMode(false);
		this.testpitName.setEnabled(false);
	}
	
	private void testpitUpdateMode()
	{
		button.setText("Okay");
		testpitMode = Mode.TESTPIT_UPDATE_MODE;

		this.testpitName.setEnabled(true);
		this.testpitName.setFocusable(true);
		this.testpitName.setFocusableInTouchMode(true);
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
}
