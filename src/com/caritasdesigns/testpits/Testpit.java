package com.caritasdesigns.testpits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class Testpit extends Activity{

	private static Mode testpitMode;
	private Button button, addHorizon, viewHorizons, setLocation, mapLocation, clearLocation, takePicture;
	private EditText testpitName;
	private LinearLayout horizonButtonGroup, pictureButtonGroup;
	private GridView imageGallery;
	private ImageGalleryModel imageGalleryModel;
	private ImageAdapter adapter;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String testpitID, projectID;
	private final static int  cameraData = 0;
	
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
		this.pictureButtonGroup = (LinearLayout) findViewById(R.id.pictureButtonGroup);
		this.takePicture = (Button) findViewById(R.id.takePicture);
		this.imageGallery = (GridView) findViewById(R.id.tpImageGallery);
		
		switch(testpitMode){
			case TESTPIT_CREATE_MODE:
				testpitCreateMode();
				break;
			case TESTPIT_UPDATE_MODE:
			case TESTPIT_READ_MODE:
				//Prepopulate the fields
				this.prepopUpdateFields();
				this.testpitReadMode();
				//populate it with the list of images
				if(imageGalleryModel == null)
				{
					imageGalleryModel = new ImageGalleryModel(loadImagesFromDB());
				}
				else
				{
					this.imageGalleryModel.setImageList(loadImagesFromDB());
				}
				
				adapter = new ImageAdapter(this, imageGalleryModel.getImageList());
				imageGallery.setAdapter(adapter);
				break;
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
				this.pictureButtonGroup.setVisibility(View.GONE);
				break;
			case TESTPIT_UPDATE_MODE:
			case TESTPIT_READ_MODE:
				this.clearLocation.setVisibility(View.VISIBLE);
				this.mapLocation.setVisibility(View.VISIBLE);
				this.horizonButtonGroup.setVisibility(View.VISIBLE);
				this.pictureButtonGroup.setVisibility(View.VISIBLE);
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
		takePicture.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//dispatchTakePictureIntent();
				startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), cameraData);
			}
			
		});
		imageGallery.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> av, View view, int position, long id) {
				Intent intent = new Intent(getBaseContext(), ImageViewer.class);
				intent.putExtra("imageGalleryModel", imageGalleryModel);
				intent.putExtra("currentPosition", position);
				view.getContext().startActivity(intent);
			}
			
		});
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
			File tpPicture = new File(internalStorageDir, imageFileName);
			
			//Create the image directory if id does not exist
			if (!internalStorageDir.exists()) 
			{
				internalStorageDir.mkdirs();
			}
			
		    try
		    {
		    	//Write the image to the file
		    	FileOutputStream fos = new FileOutputStream(tpPicture);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
		    	Log.d("FileName",tpPicture.toString());
		    }
		    catch(IOException e)
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
		    }
		    
		    //Insert the image into the DB
			insertImageDB(imageFileName);
			this.imageGalleryModel.setImageList(loadImagesFromDB());
			adapter = new ImageAdapter(this, imageGalleryModel.getImageList());
			imageGallery.setAdapter(adapter);
		}
	}
	
	
	private void insertImageDB(String fileName){
		//Open Database
		dbHelper = new DbHelper(Testpit.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertpicture","method insertImageDB is called");
		
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.PIC_TYPE, "TP");
		values.put(DbHelper.PIC_TYPEID, testpitID);
		values.put(DbHelper.PIC_LOCATION, fileName);
		//TODO: Insert into Database  (returns -1 if error, else horizonID)
		db.insert(DbHelper.TABLE_PICTURES, null, values);
		//Close Database
		dbHelper.close();
		db.close();
	}
	
	private ArrayList<ImageModel> loadImagesFromDB(){
		ArrayList<ImageModel> list = new ArrayList<ImageModel>();
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{DbHelper.PIC_ID,DbHelper.PIC_TYPE, DbHelper.PIC_TYPEID, DbHelper.PIC_LOCATION};
		Cursor cursor = db.query(DbHelper.TABLE_PICTURES, columns, DbHelper.PIC_TYPE+"='TP' AND "+DbHelper.PIC_TYPEID+"="+ testpitID, null, null, null, null);
		
		if(cursor.getCount() != 0){
			while( cursor.moveToNext()) {
				list.add(new ImageModel(cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION))));
				Log.d("ImageAdapter","getimage:" + cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION)));
			}
		}
		return list;
	}
}
