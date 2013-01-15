package com.caritasdesigns.testpits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = DbHelper.class.getSimpleName();
	
	
	public static final String DB_NAME = "testpits.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "projects";
	public static final String C_ID = "_id"; //Special for ID
	public static final String C_NAME = "name";
	public static final String C_CLIENT = "client";
	

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format(
				"create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, UNIQUE (%s))",
				TABLE, C_ID, C_NAME, C_CLIENT, C_NAME
		);
		
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE);
		this.onCreate(db);
	}
	
	public void resetDB(SQLiteDatabase db){
		db.execSQL("drop table if exists " + TABLE);
		this.onCreate(db);
	}

}
