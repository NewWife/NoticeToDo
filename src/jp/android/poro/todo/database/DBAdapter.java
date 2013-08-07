package jp.android.poro.todo.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * databaseの処理はすべてここ
 * @author akira
 *
 */
public class DBAdapter {

	static final String DATABASE_NAME = "TODO.db";
	static final int DATABASE_VERSION = 1; // databaseの構成を変えたらversionも変えないとダメ！！！！！
	public static final String TABLE_NAME = "TODO";

	public static final String COL_ID = "_id";
	public static final String COL_STATE = "state";
	public static final String COL_COLOR = "color";
	public static final String COL_TITLE = "title";
	public static final String COL_CONTENT = "content";
	public static final String COL_MAKE_DATE = "makeDate";
	public static final String COL_CHANGE_DATE = "changeDate";
	public static final String COL_REMINDER = "reminder";
	public static final String COL_PLACE = "place";
	public static final String COL_PHOTO_PATH = "photo_path";
	public static final String COL_DRAWING_PATH = "drawing_path";
	public static final String COL_VOICE_PATH = "voice_path";

//	private StateManagement mStateMan;

	protected final Context mContext;
	protected DatabaseHelper dbHelper;
	protected SQLiteDatabase db;

	public DBAdapter(Context mContext) {
		this.mContext = mContext;
		dbHelper = new DatabaseHelper(this.mContext);
//		mStateMan = new StateManagement();
	}

	/**
	 * SQLiteOpenHelper
	 * 		REAL 浮動小数点数　　http://www.dbonline.jp/sqlite/type/index1.html
	 *		INTEGER 符号付整数
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context mContext) {
			super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			String rdbms = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ COL_STATE + " INTEGER,"
					+ COL_COLOR + " INTEGER,"
					+ COL_TITLE + " TEXT NOT NULL,"
					+ COL_CONTENT + " TEXT,"
					+ COL_MAKE_DATE + " TEXT,"
					+ COL_CHANGE_DATE + " TEXT,"
					+ COL_REMINDER + " INTEGER,"
					+ COL_PLACE + " INTEGER,"
					+ COL_PHOTO_PATH + " TEXT,"
					+ COL_DRAWING_PATH + " TEXT,"
					+ COL_VOICE_PATH + " TEXT);";
			db.execSQL(rdbms);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
	/**
	* Adapter Methods
	*/
	public DBAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}
	public void close() {
		dbHelper.close();
	}
	/**
	 *	for App Methods
	 *
	 */
	public boolean deleteAllData() {
		return db.delete(TABLE_NAME, null, null) > 0;
	}

	public boolean deleteData(int id) {
		return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
	}

	public Cursor getAllData() {
		return db.query(TABLE_NAME, null, null, null, null, null, null);
		/*
		 * 1・・・テーブル名 2・・・取得する列名（カラム名、フィールド名） 3・・・レコードの条件 4・・・同上　　　　　　　 5・・・group
		 * by句 6・・・Having句 7・・・order by句 8・・・limit句（取得するレコードの上限数）
		 */
	}
	public Cursor readDB(int id) throws Exception {
		String[] columns = new String[] {COL_ID, COL_STATE, COL_COLOR,
				COL_TITLE, COL_CONTENT, COL_MAKE_DATE,
				COL_CHANGE_DATE, COL_REMINDER};
		Cursor c = db.query(TABLE_NAME, columns, "_id=" + id, null,
				null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		return c;
	}
	/*
	 * SELECT COUNT(*) from table;
	 */
	public int countRecord() {
		Cursor c = db.query(TABLE_NAME, new String[] {COL_ID }, null, null,
				null, null, null, null);
		int count = c.getCount();
		c.close();
		return count;
	}
	public String readTitle(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_TITLE }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		String str = c.getString(0);
		c.close();
		return str;
	}
	public String readContent(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_CONTENT }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		String str = c.getString(0);
		c.close();
		return str;
	}
	public int readColor(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_COLOR }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		int color = c.getInt(0);
		c.close();
		return color;
	}
	// update database zone
	/**
	 * 変更時はchangeDate　methodを呼び出して
	 * 日付を変更する
	 */
	private boolean ChangeDate(int _id) {
		ContentValues cv = new ContentValues();
		cv.put(COL_CHANGE_DATE, getDate());
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		return true;
	}
	public boolean changeTitle(int _id, String after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_TITLE, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		ChangeDate(_id);	//change date
		return true;
	}
	public boolean changeContent(int _id, String after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_CONTENT, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		ChangeDate(_id);	//change date
		return true;
	}
	public boolean changeColor(int _id, int after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_COLOR, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		return true;
	}
	public boolean changeReminder(int _id, int after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_REMINDER, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		return true;
	}
	public boolean changeState(int _id, int after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_STATE, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
//		if(after == mStateMan.getStateArchive())
//			changeReminder(_id, mStateMan.getStateArchive());
		return true;
	}
	/**
	 * change place id
	 *
	 * @param _id		1~5
	 * @param after
	 * @return
	 */
	public boolean changePlace(int _id, int after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_PLACE, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		return true;
	}
	/**
	 * change photo path
	 * @param _id
	 * @param after
	 * @return
	 */
	public boolean changePhotoPath(int _id, String after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_PHOTO_PATH, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		ChangeDate(_id);
		return true;
	}
	/**
	 * change drawing path
	 * @param _id
	 * @param after
	 * @return
	 */
	public boolean changeDrawingPath(int _id, String after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_DRAWING_PATH, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		ChangeDate(_id);	//change date
		return true;
	}
	/**
	 * change voice path
	 * @param _id
	 * @param after
	 * @return
	 */
	public boolean changeVoicePath(int _id, String after) {
		ContentValues cv = new ContentValues();
		cv.put(COL_VOICE_PATH, after);
		db.update(TABLE_NAME, cv, "_id = " + _id, null);
		ChangeDate(_id);	//change date
		return true;
	}
	///////// update database zone ////////
	public long saveToDo(String title) {
		ContentValues values = new ContentValues();
		values.put(COL_STATE, 1);
		values.put(COL_COLOR, 1);
		values.put(COL_TITLE, title);
		values.put(COL_CONTENT, "");
		values.put(COL_MAKE_DATE, getDate());
		values.put(COL_CHANGE_DATE, getDate());
		values.put(COL_REMINDER, 1);
		values.put(COL_PLACE, 5);
		/////////path//////////////
		values.put(COL_PHOTO_PATH, "none");
		values.put(COL_DRAWING_PATH, "none");
		values.put(COL_VOICE_PATH, "none");

//		db.insertOrThrow(TABLE_NAME, null, values);
		return db.insert(TABLE_NAME, null, values);
	}
	private String getDate(){
		Calendar calender = Calendar.getInstance();
		DateFormat dff = new SimpleDateFormat("yy/MM/dd(E)", Locale.US);
		return dff.format(calender.getTime());
	}


	/*
	 * Add by @ 新妻
	 */
	public String readPhotoPath(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_PHOTO_PATH }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		String str = c.getString(0);
		c.close();
		return str;
	}
	public String readDrawingPath(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_DRAWING_PATH }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		String str = c.getString(0);
		c.close();
		return str;
	}
	public String readVoicePath(int id) throws Exception {
		Cursor c = db.query(TABLE_NAME, new String[] { COL_VOICE_PATH }, "_id=" + id,
				null, null, null, null);
		if (c.getCount() == 0)
			throw new Exception();
		c.moveToFirst();
		String str = c.getString(0);
		c.close();
		return str;
	}
}