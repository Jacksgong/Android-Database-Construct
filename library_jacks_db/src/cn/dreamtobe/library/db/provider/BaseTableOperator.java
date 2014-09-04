package cn.dreamtobe.library.db.provider;

import java.util.List;

import cn.dreamtobe.library.db.provider.BaseTableFields.SQLString;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Table Operator
 * 
 * @author Jacks gong
 */
public abstract class BaseTableOperator<FIELDS extends BaseTableFields, HELPER extends BaseTableHelper> {

	private Context mContext;

	private HELPER mHelper;

	public BaseTableOperator(Context context, HELPER helper) {
		mContext = context;
		mHelper = helper;
	}

	/**
	 * insert
	 * 
	 * @param f
	 * @return
	 */
	public long insert(FIELDS f) {
		ContentValues cv = new ContentValues();
		f.setValues(cv);
		Uri uri = getContentResolver().insert(getUri(), cv);
		if (uri != null) {
			return ContentUris.parseId(uri);
		}
		return -1;
	}

	/**
	 * update
	 * 
	 * @param t
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public long update(ContentValues cv, String selection, String[] selectionArgs) {
		return getContentResolver().update(getUri(), cv, selection, selectionArgs);
	}

	public long update(final Object primaryValue, final FIELDS t) {
		if (t == null) {
			return -1;
		}

		ContentValues cv = new ContentValues();
		t.setValues(cv);
		return update(cv, getTableHelper().getPrimaryKey() + "=?", new String[] { String.valueOf(primaryValue) });
	}

	/**
	 * delete
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public long delete(String selection, String[] selectionArgs) {
		return getContentResolver().delete(getUri(), selection, selectionArgs);
	}

	public long delete(final Object primaryValue) {
		if (primaryValue == null) {
			return -1;
		}

		return delete(getTableHelper().getPrimaryKey() + "=?", new String[] { String.valueOf(primaryValue) });
	}

	/**
	 * search
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderby
	 * @return
	 */
	public List<FIELDS> query(String selection, String[] selectionArgs, String orderby) {
		return query(selection, selectionArgs, orderby, 0);
	}

	/**
	 * search
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderby
	 * @param limit
	 * @return
	 */
	public List<FIELDS> query(String selection, String[] selectionArgs, String orderby, int limit) {
		if (limit > 0) {
			if (orderby == null) {
				orderby = getTableHelper().getDefaultSortOrder();
			}
			orderby += (" limit " + limit);
		}
		Cursor c = getContentResolver().query(getUri(), null, selection, selectionArgs, orderby);

		List<FIELDS> list = null;
		try {
			list = createColumns(c);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return list;
	}

	/**
	 * search
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public FIELDS query(String selection, String[] selectionArgs) {
		List<FIELDS> list = query(selection, selectionArgs, null, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public FIELDS query(final String primaryKey) {
		if (TextUtils.isEmpty(primaryKey)) {
			return null;
		}
		return query(getTableHelper().getPrimaryKey() + "=?", new String[] { primaryKey });
	}

	/**
	 * get Count
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int getCount(String selection, String[] selectionArgs, int limit) {
		int count = 0;
		String[] projection = { " count(*)" };
		String orderBy = null;
		if (limit > 0) {
			orderBy = getTableHelper().getDefaultSortOrder() + " limit " + limit;
		}

		Cursor c = getContentResolver().query(getUri(), projection, selection, selectionArgs, orderBy);
		if (c != null && c.moveToFirst()) {
			count = c.getInt(0);
		}
		c.close();

		return count;
	}

	/**
	 * get all count
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int getCount(String selection, String[] selectionArgs) {
		return getCount(selection, selectionArgs, -1);
	}

	/**
	 * is exist
	 * 
	 * @param primaryKey
	 * @return
	 */
	public boolean isExist(final String primaryKey) {
		if (TextUtils.isEmpty(primaryKey)) {
			return false;
		}

		return getCount(getTableHelper().getPrimaryKey() + "=?", new String[] { primaryKey }, 1) > 0;
	}

	protected Context getContext() {
		return this.mContext;
	}

	public HELPER getTableHelper() {
		return this.mHelper;
	}

	public ContentResolver getContentResolver() {
		return this.mContext.getContentResolver();
	}

	public abstract Uri getUri();

	protected abstract List<FIELDS> createColumns(final Cursor c);

	public FIELDS get(FIELDS condition) {
		if (condition == null) {
			return null;
		}
		final SQLString s = condition.getSql();
		if (s == null || !s.isValid()) {
			return null;
		}
		return query(s.selection, s.selectionArgs);
	}

	public long update(FIELDS updateData, FIELDS condition) {
		if (updateData == null) {
			return -1;
		}

		if (condition == null) {
			return -1;
		}

		final SQLString s = condition.getSql();
		if (s == null || !s.isValid()) {
			return -1;
		}

		return update(updateData.setValues(new ContentValues()), s.selection, s.selectionArgs);

	}
}
