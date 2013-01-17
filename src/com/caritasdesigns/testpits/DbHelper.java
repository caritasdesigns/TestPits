package com.caritasdesigns.testpits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = DbHelper.class.getSimpleName();
	
	
	public static final String DB_NAME = "testpits.db";
	public static final int DB_VERSION = 1;
	//TABLE 'projects' Variables
	public static final String TABLE_PROJECTS = "projects";
	public static final String P_ID = "_id"; //Special for ID
	public static final String P_NAME = "name";
	public static final String P_CLIENT = "client";

	//TABLE 'testpits' Variables
	public static final String TABLE_TESTPITS = "testpits";
	public static final String TP_ID = "_id"; //Special for ID
	public static final String TP_PROJECTID = "project_id";
	public static final String TP_NAME = "name";

	//TABLE 'horizons' Variables
	public static final String TABLE_HORIZONS = "horizons";
	public static final String H_ID = "_id"; //Special for ID
	public static final String H_TESTPITID = "testpit_id";
	public static final String H_NAME = "name";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//CREATE TABLE "projects"
		String projects = String.format(
				"create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, UNIQUE (%s))",
				TABLE_PROJECTS, P_ID, P_NAME, P_CLIENT, P_NAME
		);
		db.execSQL(projects);
		
		//CREATE TABLE "testpits"
		String testpits = String.format(
				"create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s TEXT)",
				TABLE_TESTPITS, TP_ID, TP_PROJECTID, TP_NAME
		);
		db.execSQL(testpits);
		
		//CREATE TABLE "horizons"
		String horizons = String.format(
				"create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s TEXT)",
				TABLE_HORIZONS, H_ID, H_TESTPITID, H_NAME
		);
		db.execSQL(horizons);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_PROJECTS);
		db.execSQL("drop table if exists " + TABLE_TESTPITS);
		db.execSQL("drop table if exists " + TABLE_HORIZONS);
		this.onCreate(db);
	}
	
	public void resetDB(SQLiteDatabase db){
		db.execSQL("drop table if exists " + TABLE_PROJECTS);
		db.execSQL("drop table if exists " + TABLE_TESTPITS);
		db.execSQL("drop table if exists " + TABLE_HORIZONS);
		this.onCreate(db);
	}

}
