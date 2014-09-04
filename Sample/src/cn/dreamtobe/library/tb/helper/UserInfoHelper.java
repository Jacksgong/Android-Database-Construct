package cn.dreamtobe.library.tb.helper;

import android.database.sqlite.SQLiteDatabase;
import cn.dreamtobe.library.db.provider.custom.CustomTableHelper;
import cn.dreamtobe.library.tb.columns.UserInfoFields;

/**
 * 
 * @author Jacks gong
 * @data 2014-9-4 上午10:14:11
 * @Describe Table Helper
 */
public class UserInfoHelper extends CustomTableHelper {

	public final static String TABLE_NAME = "user_info";

	private final static class ClassHolder {
		private final static UserInfoHelper INSTANCE = new UserInfoHelper();
	}

	public static UserInfoHelper getImpl() {
		return ClassHolder.INSTANCE;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		final String create = getCustomCreatePre() + UserInfoFields.NAME + " TEXT," + UserInfoFields.SEX + " TEXT," + UserInfoFields.AGE + " TEXT);";
		db.execSQL(create);
	}

}
