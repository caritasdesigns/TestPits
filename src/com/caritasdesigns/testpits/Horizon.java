package com.caritasdesigns.testpits;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Horizon extends Activity{

	private static Mode horizonMode;
	private Button button, takePicture;
	private LinearLayout pictureButtonGroup;
	private EditText horizonName;
	private String currentImagePath; 
	private ImageView imageView;
	private String images;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String horizonID, testpitID, projectID;
	private final static int  cameraData = 0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.horizon);
		//Get Extras
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
		    return;
		    }
		// Get data via the key
		testpitID = extras.getString("testpitID");
		Horizon.setTestpitID(testpitID);
		horizonID = extras.getString("horizonID");
		Horizon.setHorizonID(horizonID);
		projectID = extras.getString("projectID");
		Horizon.setProjectID(projectID);
		
		
		
		this.horizonName = (EditText) findViewById(R.id.horizonName);
		this.horizonName.setOnKeyListener(this.createOnKeyListener(horizonName));
		this.button = (Button) findViewById(R.id.addUpdateHorizon);
		this.pictureButtonGroup = (LinearLayout) findViewById(R.id.pictureButtonGroup);
		this.takePicture = (Button) findViewById(R.id.takePicture);
		this.imageView = (ImageView) findViewById(R.id.imageView1);
		
		switch(horizonMode){
			case HORIZON_CREATE_MODE:
				horizonCreateMode();
				break;
			case HORIZON_UPDATE_MODE:
			case HORIZON_READ_MODE:
				//Prepopulate the fields
				this.prepopUpdateFields();
				this.horizonReadMode();		
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
		//Insert into Database  (returns -1 if error, else horizonID)
		horizonID = Long.toString(db.insert(DbHelper.TABLE_HORIZONS, null, values));
		//Close Database
		dbHelper.close();
		db.close();
        hideKeyboard(horizonName);
		this.horizonReadMode();
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

	public static void setHorizonID(String id)
	{
		horizonID = id;

	}
	public static void setProjectID(String id)
	{
		projectID = id;
	}
	public static void setTestpitID(String id)
	{
		testpitID = id;
	}
	private void prepopUpdateFields()
	{
		dbHelper = new DbHelper(Horizon.this);
		db = dbHelper.getReadableDatabase();
		
		String[] columns = new String[]{DbHelper.H_ID,DbHelper.H_ORDER};
		Cursor cursor = db.query(DbHelper.TABLE_HORIZONS, columns, DbHelper.H_ID+"="+horizonID, null, null, null, null);
		Log.d("PrePopVals","Query: t="+DbHelper.TABLE_HORIZONS+" id="+horizonID);
		Log.d("PrePopVals","Count: "+cursor.getCount());
		if(cursor.getCount()!=0){
			cursor.moveToFirst();
			horizonName.setText(cursor.getString(cursor.getColumnIndex(DbHelper.H_ORDER)));
		}
		
		Log.d("PrePopVals","Name: "+horizonName.getText().toString());

		//Close Database
		dbHelper.close();
		db.close();	
	}
	
	private void horizonCreateMode()
	{
		horizonMode = Mode.HORIZON_CREATE_MODE;
		this.setButtonVisibility();
		this.button.setText("Add Horizon");
	}
	
	private void horizonReadMode()
	{		
		button.setText("Edit");
		horizonMode = Mode.HORIZON_READ_MODE;
		this.setButtonVisibility();
		this.horizonName.setFocusable(false);
		this.horizonName.setFocusableInTouchMode(false);
		this.horizonName.setEnabled(false);
	}
	
	private void horizonUpdateMode()
	{
		button.setText("Okay");
		horizonMode = Mode.HORIZON_UPDATE_MODE;
		this.setButtonVisibility();
		this.horizonName.setEnabled(true);
		this.horizonName.setFocusable(true);
		this.horizonName.setFocusableInTouchMode(true);
	}
	
	private void setButtonVisibility(){
		Log.d("setButtonVisibility","with horizonMode: "+ horizonMode);
		switch(horizonMode){
			case HORIZON_CREATE_MODE:
				this.pictureButtonGroup.setVisibility(View.GONE);
				break;
			case HORIZON_UPDATE_MODE:
			case HORIZON_READ_MODE:
				this.pictureButtonGroup.setVisibility(View.VISIBLE);
				break;
			default:
				Log.d("setButtonVisibility","issue with Mode: "+ horizonMode);
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
	
	private void populateGallery(){
		
	}
	
	
	private void setOnClickListeners(){
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
		
		takePicture.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//dispatchTakePictureIntent();
				startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), cameraData);
			}
			
		});

//		private void dispatchTakePictureIntent(int actionCode) {
//		    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		    startActivityForResult(takePictureIntent, actionCode);
//		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			Bitmap bmp = (Bitmap) extras.get("data");
		    String imageFileName = "H_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png";
			
			ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
			
			
			File internalStorageDir = contextWrapper.getDir("Images", Context.MODE_PRIVATE);
			File hPicture = new File(internalStorageDir, imageFileName);
			
			//Create the image directory if id does not exist
			if (!internalStorageDir.exists()) 
			{
				internalStorageDir.mkdirs();
			}
			
		    try
		    {
		    	//Write the image to the file
		    	FileOutputStream fos = new FileOutputStream(hPicture);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
		    	Log.d("FileName",hPicture.toString());
		    }
		    catch(IOException e)
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
		    }
		    
		    //Insert the image into the DB
			insertImageDB(imageFileName);
		}
	}
	
	
	private void insertImageDB(String fileName){
		//Open Database
		dbHelper = new DbHelper(Horizon.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertpicture","method insertImageDB is called");
		
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.PIC_TYPE, "H");
		values.put(DbHelper.PIC_TYPEID, horizonID);
		values.put(DbHelper.PIC_LOCATION, fileName);
		//TODO: Insert into Database  (returns -1 if error, else horizonID)
		db.insert(DbHelper.TABLE_PICTURES, null, values);
		//Close Database
		dbHelper.close();
		db.close();
	}
	
	private String loadImagesFromDB(){
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{DbHelper.PIC_ID,DbHelper.PIC_TYPE, DbHelper.PIC_TYPEID, DbHelper.PIC_LOCATION};
		Cursor cursor = db.query(DbHelper.TABLE_PICTURES, columns, DbHelper.PIC_TYPE+"='H' AND "+DbHelper.PIC_TYPEID+"="+this.horizonID, null, null, null, null);
		if(cursor.getCount() != 0){
			cursor.moveToFirst();
			images = cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION));
/*			while( cursor.moveToNext()) {
				images.add(cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION)));
			}*/
		}
		return images;
	}
	
}
