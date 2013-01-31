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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

public class Project extends Activity{

	private static Mode projectMode;
	private Button button, viewTestpits, addTestpit, viewMWs, addMW, clearLocation, setLocation, mapLocation, takePicture;
	private LinearLayout testpitButtonGroup, MWButtonGroup, pictureButtonGroup;
	private EditText projectName, client;
	private GridView imageGallery;
	private List<ImageModel> imageList;
	private ImageAdapter adapter;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private static String projectID;
	private final static int  cameraData = 0;
	
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
		takePicture = (Button) findViewById(R.id.takePicture);
		imageGallery = (GridView) findViewById(R.id.pImageGallery);
		
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
				this.imageList = loadImagesFromDB();
				adapter = new ImageAdapter(this, imageList);
				imageGallery.setAdapter(adapter);
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
		takePicture.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//dispatchTakePictureIntent();
				startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), cameraData);
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
			File pPicture = new File(internalStorageDir, imageFileName);
			
			//Create the image directory if id does not exist
			if (!internalStorageDir.exists()) 
			{
				internalStorageDir.mkdirs();
			}
			
		    try
		    {
		    	//Write the image to the file
		    	FileOutputStream fos = new FileOutputStream(pPicture);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
		    	Log.d("FileName",pPicture.toString());
		    }
		    catch(IOException e)
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
		    }
		    
		    //Insert the image into the DB
			insertImageDB(imageFileName);
			this.imageList = loadImagesFromDB();
			adapter = new ImageAdapter(this, imageList);
			imageGallery.setAdapter(adapter);
		}
	}
	
	
	private void insertImageDB(String fileName){
		//Open Database
		dbHelper = new DbHelper(Project.this);
		db = dbHelper.getWritableDatabase();
		Log.d("insertpicture","method insertImageDB is called");
		
		//Create Content Values
		ContentValues values = new ContentValues();
		values.put(DbHelper.PIC_TYPE, "P");
		values.put(DbHelper.PIC_TYPEID, projectID);
		values.put(DbHelper.PIC_LOCATION, fileName);
		//TODO: Insert into Database  (returns -1 if error, else horizonID)
		db.insert(DbHelper.TABLE_PICTURES, null, values);
		//Close Database
		dbHelper.close();
		db.close();
	}
	
	private List<ImageModel> loadImagesFromDB(){
		List<ImageModel> list = new ArrayList<ImageModel>();
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{DbHelper.PIC_ID,DbHelper.PIC_TYPE, DbHelper.PIC_TYPEID, DbHelper.PIC_LOCATION};
		Cursor cursor = db.query(DbHelper.TABLE_PICTURES, columns, DbHelper.PIC_TYPE+"='P' AND "+DbHelper.PIC_TYPEID+"="+ projectID, null, null, null, null);
		
		if(cursor.getCount() != 0){
			while( cursor.moveToNext()) {
				list.add(new ImageModel(cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION))));
				Log.d("ImageAdapter","getimage:" + cursor.getString(cursor.getColumnIndex(DbHelper.PIC_LOCATION)));
			}
		}
		return list;
	}
}
