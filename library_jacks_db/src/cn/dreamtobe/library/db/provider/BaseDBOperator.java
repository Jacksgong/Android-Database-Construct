package cn.dreamtobe.library.db.provider;

import java.util.List;

import cn.dreamtobe.library.db.provider.BaseDBColumns.SQLString;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 数据库表操作
 * 
 * @author Jacks gong
 */
public abstract class BaseDBOperator<T extends BaseDBColumns, HELPER extends BaseTBHelper> {

	private Context mContext;

	private HELPER mHelper;

	public BaseDBOperator(Context context, HELPER helper) {
		mContext = context;
		mHelper = helper;
	}

	/**
	 * 新增
	 * 
	 * @param t
	 * @return
	 */
	public long insert(T t) {
		ContentValues cv = new ContentValues();
		t.setValues(cv);
		Uri uri = getContentResolver().insert(getUri(), cv);
		if (uri != null) {
			return ContentUris.parseId(uri);
		}
		return -1;
	}

	/**
	 * 更新
	 * 
	 * @param t
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public long update(ContentValues cv, String selection, String[] selectionArgs) {
		return getContentResolver().update(getUri(), cv, selection, selectionArgs);
	}

	public long update(final Object primaryValue, final T t) {
		if (t == null) {
			return -1;
		}

		ContentValues cv = new ContentValues();
		t.setValues(cv);
		return update(cv, getTBHelper().getPrimaryKey() + "=?", new String[] { String.valueOf(primaryValue) });
	}

	/**
	 * 删除
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

		return delete(getTBHelper().getPrimaryKey() + "=?", new String[] { String.valueOf(primaryValue) });
	}

	/**
	 * 查询
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderby
	 * @return
	 */
	public List<T> query(String selection, String[] selectionArgs, String orderby) {
		return query(selection, selectionArgs, orderby, 0);
	}

	/**
	 * 查询
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @param orderby
	 * @param limit
	 * @return
	 */
	public List<T> query(String selection, String[] selectionArgs, String orderby, int limit) {
		if (limit > 0) {
			if (orderby == null) {
				orderby = getTBHelper().getDefaultSortOrder();
			}
			orderby += (" limit " + limit);
		}
		Cursor c = getContentResolver().query(getUri(), null, selection, selectionArgs, orderby);

		List<T> list = null;
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
	 * 查询
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public T query(String selection, String[] selectionArgs) {
		List<T> list = query(selection, selectionArgs, null, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public T query(final String primaryKey) {
		if (TextUtils.isEmpty(primaryKey)) {
			return null;
		}
		return query(getTBHelper().getPrimaryKey() + "=?", new String[] { primaryKey });
	}

	/**
	 * 获取数量
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
			orderBy = getTBHelper().getDefaultSortOrder() + " limit " + limit;
		}

		Cursor c = getContentResolver().query(getUri(), projection, selection, selectionArgs, orderBy);
		if (c != null && c.moveToFirst()) {
			count = c.getInt(0);
		}
		c.close();

		return count;
	}

	public int getCount(String selection, String[] selectionArgs) {
		return getCount(selection, selectionArgs, -1);
	}

	public boolean hasColumn(final String primaryKey) {
		if (TextUtils.isEmpty(primaryKey)) {
			return false;
		}

		return getCount(getTBHelper().getPrimaryKey() + "=?", new String[] { primaryKey }, 1) > 0;
	}

	protected Context getContext() {
		return this.mContext;
	}

	public HELPER getTBHelper() {
		return this.mHelper;
	}

	public ContentResolver getContentResolver() {
		return this.mContext.getContentResolver();
	}

	public abstract Uri getUri();

	protected abstract List<T> createColumns(final Cursor c);

	public T get(T condition) {
		if (condition == null) {
			return null;
		}
		final SQLString s = condition.getSql();
		if (s == null || !s.isValid()) {
			return null;
		}
		return query(s.selection, s.selectionArgs);
	}

	public long update(T updateData, T condition) {
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
