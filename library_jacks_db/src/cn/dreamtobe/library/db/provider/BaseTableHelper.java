package cn.dreamtobe.library.db.provider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 
 * @author Jacks gong
 * @data 2014-7-20 下午3:56:47
 * @Describe
 */
public abstract class BaseTableHelper {

	public final static String CREATE_TABLE_SQL_PRE = "CREATE TABLE IF NOT EXISTS ";

	public abstract String getPrimaryKey();

	public abstract String getTableName();

	public abstract String getDefaultSortOrder();

	public Uri getContentUri(final String authority) {
		return Uri.parse(BaseContentProvider.getContentAuthoritySlash(authority) + getTableName());
	}

	public String getContentType(final String authority) {
		return "vnd.android.cursor.dir/" + authority + "." + getTableName();
	}

	public String getEntryContentType(final String authority) {
		return "vnd.android.cursor.item/" + authority + "." + getTableName();
	}

	public abstract void onDataBaseCreate(final SQLiteDatabase db);

	public abstract void onDatabaseUpgrade(final int oldVersion, final int newVersion, SQLiteDatabase db);

	protected String getCustomCreatePre() {
		return CREATE_TABLE_SQL_PRE + getTableName() + " (" + getPrimaryKey() + " INTEGER PRIMARY KEY AUTOINCREMENT," + BaseTableFields.CREATE_AT + " DATETIME DEFAULT(DATETIME('now', 'localtime')),";
	}
	
	

}
