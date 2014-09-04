package cn.dreamtobe.library.db.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

/**
 * 描述:抽象ContentProvider，实现了数据库创建功能
 * 
 * @author Jacks gong
 * @since 2014-6-20 上午11:47:26
 */
public abstract class BaseContentProvider extends ContentProvider {

	private DatabaseHelper mDatabaseHelper;

	// DB Name
	protected String mAuthority;

	public static UriMatcher sUriMatcher;

	protected HashMap<String, BaseTableHelper> mAllTableHelper;

	protected SparseArray<String> mCodeType;
	protected SparseArray<BaseTableHelper> mCodeTBHelper;

	public BaseContentProvider(final String authority) {
		this.mAuthority = authority;
		this.mAllTableHelper = createAllTableHelper();

		initUriMatcher(mAuthority, mAllTableHelper);
	}

	public final String getContentAuthoritySlash() {
		return getContentAuthoritySlash(mAuthority);
	}

	public static final String getContentAuthoritySlash(final String authority) {
		return "content://" + authority + "/";
	}

	private void initUriMatcher(final String authority, final HashMap<String, BaseTableHelper> tableHelpers) {
		if (TextUtils.isEmpty(authority) || tableHelpers == null || tableHelpers.size() <= 0) {
			return;
		}

		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mCodeType = new SparseArray<String>();
		mCodeTBHelper = new SparseArray<BaseTableHelper>();

		int code = 0;

		for (Iterator it = tableHelpers.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String tableName = (String) entry.getKey();
			BaseTableHelper tableHelper = (BaseTableHelper) entry.getValue();

			mCodeType.put(code, tableHelper.getContentType(authority));
			mCodeTBHelper.put(code, tableHelper);
			sUriMatcher.addURI(authority, tableName, code++);

			mCodeType.put(code, tableHelper.getEntryContentType(authority));
			mCodeTBHelper.put(code, tableHelper);
			sUriMatcher.addURI(authority, tableName + "/#", code++);

		}

	}

	@Override
	public String getType(Uri uri) {
		return mCodeType.get(sUriMatcher.match(uri));
	}

	protected abstract HashMap<String, BaseTableHelper> createAllTableHelper();

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return mDatabaseHelper != null;
	}

	/**
	 * 获取可读数据库
	 * 
	 * @return
	 */
	protected SQLiteDatabase getReadableDatabase() {
		return mDatabaseHelper.getReadableDatabase();
	}

	/**
	 * 获取可读写数据库
	 * 
	 * @return
	 */
	protected SQLiteDatabase getWritableDatabase() {
		return mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * 返回数据库名称
	 * 
	 * @return
	 */
	protected abstract String getDatabaseName();

	/**
	 * 返回数据库版本号
	 * 
	 * @return
	 */
	protected abstract int getDatabaseVersion();

	/**
	 * 数据库创建成功，可以在这个回调里去创建数据表
	 */
	protected void onDatabaseCreate(SQLiteDatabase db) {
		if (mAllTableHelper == null || mAllTableHelper.size() <= 0) {
			return;
		}

		for (Iterator it = mAllTableHelper.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String tableName = (String) entry.getKey();
			BaseTableHelper tableHelper = (BaseTableHelper) entry.getValue();

			tableHelper.onDataBaseCreate(db);
		}
	}

	/**
	 * 数据库升级
	 * 
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	protected void onDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (mAllTableHelper == null || mAllTableHelper.size() <= 0) {
			return;
		}

		for (Iterator it = mAllTableHelper.keySet().iterator(); it.hasNext();) {
			String tableName = (String) it.next();
			BaseTableHelper tableHelper = mAllTableHelper.get(tableName);

			tableHelper.onDatabaseUpgrade(oldVersion, newVersion, db);
		}
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, BaseContentProvider.this.getDatabaseName(), null, getDatabaseVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			onDatabaseCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onDatabaseUpgrade(db, oldVersion, newVersion);
		}

		// 4.0以上系统在数据库从高降到低时，会强制抛出异常，通过重写这个方法，可以解决问题
		@SuppressLint("Override")
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// 虽然没调用到，但要保留本函数
		}

	}

	// ===========
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		String tableName = null;
		final int matcher = sUriMatcher.match(uri);

		final BaseTableHelper tableHelper = mCodeTBHelper.get(matcher);
		final String type = mCodeType.get(matcher);

		Cursor c = null;
		if (tableHelper == null || type == null) {
			throw new IllegalArgumentException("Unknown Uri: " + uri + " matcher" + matcher);
		}

		tableName = tableHelper.getTableName();

		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = tableHelper.getDefaultSortOrder();
		} else {
			orderBy = sortOrder;
		}

		if (type.equals(tableHelper.getEntryContentType(mAuthority))) {
			if (TextUtils.isEmpty(selection)) {
				selection = getEntrySelect(uri, tableHelper.getPrimaryKey());
			} else {
				selection = selection + " AND " + getEntrySelect(uri, tableHelper.getPrimaryKey());
			}
		}

		SQLiteDatabase db = getReadableDatabase();
		c = db.query(tableName, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int matcher = sUriMatcher.match(uri);
		ContentValues initValues = values != null ? new ContentValues(values) : new ContentValues();
		SQLiteDatabase db = getWritableDatabase();
		String tableName;
		long rowId;

		final BaseTableHelper tableHelper = mCodeTBHelper.get(matcher);

		do {
			if (tableHelper == null) {
				break;
			}

			tableName = tableHelper.getTableName();
			rowId = db.insert(tableName, null, initValues);

			if (rowId > 0) {
				Uri songUri = ContentUris.withAppendedId(tableHelper.getContentUri(mAuthority), rowId);
				getContext().getContentResolver().notifyChange(songUri, null);
				return songUri;
			}

		} while (false);

		throw new IllegalArgumentException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int matcher = sUriMatcher.match(uri);
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;

		final BaseTableHelper tableHelper = mCodeTBHelper.get(matcher);

		if (tableHelper == null) {
			throw new IllegalArgumentException("Unknown Uri: " + uri + " Matcher : " + matcher);
		}

		count = db.delete(tableHelper.getTableName(), selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		ContentValues initValues = values == null ? new ContentValues() : new ContentValues(values);
		int matcher = sUriMatcher.match(uri);

		final BaseTableHelper tableHelper = mCodeTBHelper.get(matcher);

		if (tableHelper == null) {
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}

		count = db.update(tableHelper.getTableName(), initValues, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	protected String getEntrySelect(final Uri uri, final Object primaryKey) {
		if (uri == null || primaryKey == null) {
			return "";
		}
		return primaryKey + "=" + ContentUris.parseId(uri);
	}

}
