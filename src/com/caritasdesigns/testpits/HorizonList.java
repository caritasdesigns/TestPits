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

public class HorizonList extends Activity {

	private Button addHorizon;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private List<HorizonModel> horizonList;
	private ListView list;
	private HorizonAdapter adapter;
	private String testpitID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		//Set View
		setContentView(R.layout.activity_horizon);
		
		//Get Extras
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
		    return;
		    }
		// Get data via the key
		this.testpitID = extras.getString("testpitID");
		Horizon.setTestpitID(testpitID);
		
		//Open Database
		dbHelper = new DbHelper(HorizonList.this);
		horizonList = this.getHorizonsFromDb();
		adapter = new HorizonAdapter(this, R.layout.horizon_list_item, horizonList);
		list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
	
		list.setClickable(true);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				HorizonModel t = horizonList.get(position);
				Horizon.setMode(Mode.HORIZON_READ_MODE);
				Horizon.setHorizonID(t.getId());
				Intent intent = new Intent(view.getContext(), Horizon.class);
				view.getContext().startActivity(intent);
				Log.d("HorizonAdd","onClick'd with addHorizon: "+ R.id.addHorizon);
			}
			
		});
		
		addHorizon = (Button) findViewById(R.id.addHorizon);
		addHorizon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Horizon.setMode(Mode.HORIZON_CREATE_MODE);
				Intent intent = new Intent(v.getContext(), Horizon.class);
				v.getContext().startActivity(intent);
				Log.d("HorizonAdd","onClick'd with addHorizonButton: "+ R.id.addHorizon);
			}
		});

	//Close Database
		db.close();
		dbHelper.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		horizonList = this.getHorizonsFromDb();

		adapter = new HorizonAdapter(this, R.layout.horizon_list_item, horizonList);
		list.setAdapter(adapter);
		Log.d("HorizonList","onResume is running" + adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private List<HorizonModel> getHorizonsFromDb(){
		List<HorizonModel> list = new ArrayList<HorizonModel>();
		db = dbHelper.getReadableDatabase();
		String[] columns = new String[]{"_id","testpit_id", "name"};
		
		Cursor cursor = db.query(DbHelper.TABLE_HORIZONS, columns, "testpit_id=" + testpitID, null, null, null, null);
		if(cursor.getCount() != 0){
			while( cursor.moveToNext()) {
				Log.d("HAddToList","Item ID: "+ cursor.getInt(0));
				int id = cursor.getInt(0);
				list.add(new HorizonModel(id, cursor.getString(cursor.getColumnIndex("name"))));
			}
		}
		return list;
	}

}
