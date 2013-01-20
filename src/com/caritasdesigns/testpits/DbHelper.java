package com.caritasdesigns.testpits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = DbHelper.class.getSimpleName();
	
	
	public static final String DB_NAME = "testpits.db";
	public static final int DB_VERSION = 1;

	//Setup Tables
	public static final String TABLE_PROJECTS = "projects";
	public static final String TABLE_TESTPITS = "testpits";
	public static final String TABLE_HORIZONS = "tp_horizons";
	public static final String TABLE_MONITORWELLS = "monitor_wells";
	public static final String TABLE_MEASUREMENTS = "mw_measurements";
	
	//TABLE 'projects' Variables	
	public static final String P_ID = "_id"; //Special for ID
	public static final String P_NAME = "name";
	public static final String P_CLIENT = "client";

	//TABLE 'testpits' Variables
	public static final String TP_ID = "_id"; //Special for ID
	public static final String TP_PROJECTID = "projects_id";
	public static final String TP_NAME = "name";
	public static final String TP_DATE_C = "date_created";
	public static final String TP_DATE_M = "date_modified";
	public static final String TP_DESCRIBED_BY = "described_by";
	public static final String TP_CONFIRMATION_NUMBER = "confirmation_number";
	public static final String TP_SLOPE = "slope";
	public static final String TP_VEGETATION = "vegetation";
	public static final String TP_LANDUSE = "land_use";
	public static final String TP_MWID = "monitor_wells_id";

	//TABLE 'tp_horizons' Variables
	public static final String H_ID = "_id"; //Special for ID
	public static final String H_TESTPITID = "testpits_id";
	public static final String H_ORDER= "h_order";
	public static final String H_DEPTH_S = "start_depth";
	public static final String H_DEPTH_E = "end_depth";
	public static final String H_COLOR = "color";
	public static final String H_MOTTLES = "mottles";
	public static final String H_TEXTURE = "texture";
	public static final String H_STRUCTURE_G = "structure_g";
	public static final String H_STRUCTURE_SH = "structure_sh";
	public static final String H_STRUCTURE_S = "structure_s";
	public static final String H_CONSISTENCE = "consistence";
	public static final String H_BOUNDARY_D = "boundary_d";
	public static final String H_BOUNDARY_T = "boundary_t";
	public static final String H_ROOTS = "roots";
	public static final String H_WATER = "water";
	public static final String H_SOIL_RIBBON = "soil_ribbon";
	public static final String H_COMMENTS = "comments";

	//TABLE 'monitor_wells' Variables
	public static final String MW_ID = "_id"; //Special for ID
	public static final String MW_PROJECTID = "projects_id";
	public static final String MW_NAME = "name";
	public static final String MW_DATE_INSTALLED = "date_installed";
	public static final String MW_INSTALLED_BY = "installed_by";
	public static final String MW_TWCL = "casing_length";
	public static final String MW_AGCL = "casing_stub";
	

	//TABLE 'mw_measurements' Variables
	public static final String M_ID = "_id"; //Special for ID
	public static final String M_MWID = "monitors_well_id";
	public static final String M_DATE = "date";
	public static final String M_MEASUREMENT = "measurement";
	public static final String M_MEASURED_BY = "measured_by";

	
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
				"create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL)",
				TABLE_HORIZONS, H_ID, H_TESTPITID, H_ORDER
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
