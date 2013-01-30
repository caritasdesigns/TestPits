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

public class TestpitList extends Activity {

	private Button addTestpit;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private List<TestpitModel> testpitList;
	private ListView list;
	private TestpitAdapter adapter;
	private String projectID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		//Set View
		setContentView(R.layout.activity_testpit);
		
		//Get Extras
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
		    return;
		    }
		// Get data via the key
		this.projectID = extras.getString("projectID");
		Testpit.setProjectID(projectID);
		
		//Open Database
		dbHelper = new DbHelper(TestpitList.this);
		testpitList = this.getTestpitsFromDb();
		adapter = new TestpitAdapter(this, R.layout.testpit_list_item, testpitList);
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
	
		list.setClickable(true);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				TestpitModel t = testpitList.get(position);
				Testpit.setMode(Mode.TESTPIT_READ_MODE);
				Intent intent = new Intent(view.getContext(), Testpit.class);
				intent.putExtra("projectID", projectID);
				intent.putExtra("testpitID", Integer.toString(t.getId()));
				view.getContext().startActivity(intent);
				Log.d("TestpitAdd","onClick'd with readTestpit: "+ t.getId());
			}
			
		});
		
		addTestpit = (Button) findViewById(R.id.addTestpit);
		addTestpit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Testpit.setMode(Mode.TESTPIT_CREATE_MODE);
				Intent intent = new Intent(v.getContext(), Testpit.class);
				intent.putExtra("projectID", projectID);
				v.getContext().startActivity(intent);
				Log.d("TestpitAdd","onClick'd with addTestpitButton: "+ R.id.addTestpit);
			}
		});

	//Close Database
		db.close();
		dbHelper.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		testpitList = this.getTestpitsFromDb();

		adapter = new TestpitAdapter(this, R.layout.testpit_list_item, testpitList);
		list.setAdapter(adapter);
		Log.d("TestpitList","onResume is running" + adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private List<TestpitModel> getTestpitsFromDb(){
		List<TestpitModel> list = new ArrayList<TestpitModel>();
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{DbHelper.TP_ID,DbHelper.TP_PROJECTID, DbHelper.TP_NAME};
		
		Cursor cursor = db.query(DbHelper.TABLE_TESTPITS, columns, DbHelper.TP_PROJECTID+"="+this.projectID, null, null, null, null);
		if(cursor.getCount() != 0){
			while( cursor.moveToNext()) {
				int id = cursor.getInt(0);
				list.add(new TestpitModel(id, cursor.getString(cursor.getColumnIndex(DbHelper.TP_NAME))));
			}
		}
		return list;
	}

}
