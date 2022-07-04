package com.example.navigationview;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	final static String DATABASENAME = "user.db";

	public DatabaseHelper(Context context) {
		super(context, DATABASENAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE adminuser " + "(" + "adminid  TEXT NOT NULL,"
				+ "adminpwd  TEXT NOT NULL," + "name  TEXT,"+ "uid  TEXT," + "picture  BLOB,"
				+ "PRIMARY KEY (adminid)" + ");";
        String sql2 = "INSERT INTO adminuser VALUES ('123', '123',NULL,NULL, NULL);";
		String sql3 = "CREATE TABLE loginrecord " + "("  +"id INTEGER PRIMARY KEY AUTOINCREMENT," + "loginid  TEXT NOT NULL"  +");";
		db.execSQL(sql);
		db.execSQL(sql3);
		db.execSQL(sql2);



	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
