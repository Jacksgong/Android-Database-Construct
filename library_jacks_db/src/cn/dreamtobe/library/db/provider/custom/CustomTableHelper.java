package cn.dreamtobe.library.db.provider.custom;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cn.dreamtobe.library.db.provider.BaseTableHelper;

/**
 * 
 * @author Jacks gong
 * @data 2014-9-4 上午10:12:54
 * @Describe Custom Table Helper
 */
public abstract class CustomTableHelper extends BaseTableHelper {

	@Override
	public String getPrimaryKey() {
		return BaseColumns._ID;
	}

	public String getDefaultSortOrder() {
		return getPrimaryKey() + " DESC";
	}

	@Override
	public void onDatabaseUpgrade(int oldVersion, int newVersion, SQLiteDatabase db) {

	}
}
