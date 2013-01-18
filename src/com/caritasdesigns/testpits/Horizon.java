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

public class Horizon extends Activity{

	private static Mode horizonMode;
	private Button button, insertHorizon;
	private EditText horizonName;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String horizonID, testpitID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		switch(horizonMode){
			case HORIZON_CREATE_MODE:
				setContentView(R.layout.horizon_add);
				insertHorizon = (Button) findViewById(R.id.insertHorizon);
				horizonName = (EditText) findViewById(R.id.horizonName);
				horizonName.setOnKeyListener(this.createOnKeyListener(horizonName));
				insertHorizon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						insertHorizon();
					}
				});
				break;
			case HORIZON_UPDATE_MODE:
			case HORIZON_READ_MODE:
				setContentView(R.layout.horizon);
				//Setup View
				horizonName = (EditText) findViewById(R.id.horizonName);
				horizonName.setOnKeyListener(this.createOnKeyListener(horizonName));
				button = (Button) findViewById(R.id.addUpdateHorizon);
				
				//Prepopulate the fields
				this.prepopUpdateFields();
				this.horizonReadMode();		

				button.setOnClickListener(new OnClickListener() {
			
					@Override
					public void onClick(View v) {
						switch(horizonMode){
							case HORIZON_CREATE_MODE:
								insertHorizon();
								break;
							case HORIZON_UPDATE_MODE:
								updateHorizon();
								horizonReadMode();
								break;
							case HORIZON_READ_MODE:
								horizonUpdateMode();
								break;
							default:
								Log.d("addUpdatehorizon","onClick'd issue with Mode: "+ horizonMode);
								break;
						}
					}
				});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static void setMode(Mode mode) {
		horizonMode = mode;
	}

	private void insertHorizon(){
		//Open Database
		dbHelper = new DbHelper(Horizon.this);
		db = dbHelper.getWritableDatabase();
		Log.d("inserthorizon","method inserthorizon is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.H_ORDER, horizonName.getText().toString());
		values.put(DbHelper.H_TESTPITID, testpitID);
		//Insert into Database
		db.insert(DbHelper.TABLE_HORIZONS, null, values);
		//Close Database
		dbHelper.close();
		db.close();
		finish();
	}

	private void updateHorizon(){
		//Open Database
		dbHelper = new DbHelper(Horizon.this);
		db = dbHelper.getWritableDatabase();
		Log.d("inserthorizon","method inserthorizon is called");
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.H_ORDER, horizonName.getText().toString());
		
		//Insert into Database
		db.update(DbHelper.TABLE_HORIZONS, values, DbHelper.H_ID+"="+horizonID, null);
		//Close Database
		dbHelper.close();
		db.close();
	}

	public static void setHorizonID(long id)
	{
		horizonID = String.valueOf(id);

	}
	public static void setTestpitID(String id)
	{
		testpitID = id;
	}
	private void prepopUpdateFields()
	{
		dbHelper = new DbHelper(Horizon.this);
		db = dbHelper.getReadableDatabase();
		
		String[] columns = new String[]{"name"};
		Cursor cursor = db.query(DbHelper.TABLE_HORIZONS, columns, DbHelper.H_ID+"="+horizonID, null, null, null, null);
		Log.d("PrePopVals","Count: "+cursor.getCount());
		if(cursor.getCount()!=0){
			cursor.moveToFirst();
			horizonName.setText(cursor.getString(cursor.getColumnIndex("name")));
		}
		
		Log.d("PrePopVals","Name: "+horizonName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	private void horizonReadMode()
	{		
		button.setText("Edit");

		horizonMode = Mode.HORIZON_READ_MODE;
		this.horizonName.setFocusable(false);
		this.horizonName.setFocusableInTouchMode(false);
		this.horizonName.setEnabled(false);
	}
	
	private void horizonUpdateMode()
	{
		button.setText("Okay");
		horizonMode = Mode.HORIZON_UPDATE_MODE;

		this.horizonName.setEnabled(true);
		this.horizonName.setFocusable(true);
		this.horizonName.setFocusableInTouchMode(true);
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
