package cn.dreamtobe.library.db.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * 描述:基本字段
 * 
 * @author Jacks gong
 * @since 2014-7-20 下午12:14:43
 */
public abstract class BaseDBColumns implements BaseColumns, Serializable {

	protected HashMap<String, Object> mColumnValues = new HashMap<String, Object>();
	/**
	 * 创建时间
	 */
	public static final String CREATE_AT = "createAt";

	/**
	 * 修改时间
	 */
	public static final String MODIFIED_AT = "modifiedAt";

	public BaseDBColumns() {

	}

	public BaseDBColumns(final Cursor c) {
		put(c);
	}

	public BaseDBColumns(final HashMap<String, Object> values) {
		this.mColumnValues = values;
	}

	public void set_Id(final int _id) {
		put(_ID, _id);
	}

	public int get_Id() {
		return get(_ID) == null ? -1 : (Integer) get(_ID);
	}

	public SQLString getSql() {

		List<Object> selectionArgsList = new ArrayList<Object>();
		String selection = "";
		for (Iterator iterator = mColumnValues.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Entry) iterator.next();
			final String key = (String) entry.getKey();
			final Object value = entry.getValue();

			selection += (key + "=? AND ");
			selectionArgsList.add(value);
		}
		if (selection.length() > 0) {
			selection = selection.substring(0, selection.length() - " AND ".length());
		}

		String[] selectionArgs = null;
		if (selectionArgsList.size() > 0) {
			selectionArgs = new String[selectionArgsList.size()];
			int i = 0;
			for (Object o : selectionArgsList) {
				selectionArgs[i++] = String.valueOf(o);
			}
		}

		return new SQLString(selection, selectionArgs);

	}

	public static class SQLString {

		public SQLString(final String selection, final String[] selectionArgs) {
			this.selection = selection;
			this.selectionArgs = selectionArgs;
		}

		public String selection;
		public String[] selectionArgs;

		public boolean isValid() {
			return !TextUtils.isEmpty(selection) && selectionArgs != null && selectionArgs.length > 0;
		}
	}

	public ContentValues setValues(final ContentValues cv) {
		if (cv == null) {
			return null;
		}

		for (Iterator iterator = mColumnValues.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Entry) iterator.next();
			final String key = (String) entry.getKey();
			final Object value = entry.getValue();
			if (value instanceof String) {
				String string = value.toString();
				cv.put(key, string);
			} else if (value instanceof Long) {
				Long l = ((Long) value).longValue();
				cv.put(key, l);
			} else if (value instanceof Integer) {
				Integer i = ((Integer) value).intValue();
				cv.put(key, i);
			} else if (value instanceof Short) {
				Short s = ((Short) value).shortValue();
				cv.put(key, s);
			} else if (value instanceof Byte) {
				Byte b = ((Byte) value).byteValue();
				cv.put(key, b);
			} else if (value instanceof Double) {
				Double d = ((Double) value).doubleValue();
				cv.put(key, d);
			} else if (value instanceof Float) {
				Float f = ((Float) value).floatValue();
				cv.put(key, f);
			} else if (value instanceof Boolean) {
				Boolean bo = ((Boolean) value).booleanValue();
				cv.put(key, bo);
			} else if (value instanceof byte[]) {
				cv.put(key, (byte[]) value);
			}

		}
		return cv;
	}

	public void put(final String key, final Object value) {
		mColumnValues.put(key, value);
	}

	public Object get(final String key) {
		return mColumnValues.get(key);
	}

	public void clear() {
		mColumnValues.clear();
	}

	public void remove(final String key) {
		mColumnValues.remove(key);
	}

//
//	public List<BaseDBColumns> toList(final Cursor c) {
//		if (c == null || c.isClosed() || c.isAfterLast()) {
//			return null;
//		}
//
//		List<BaseDBColumns> list = new ArrayList<BaseDBColumns>();
//
//		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//			list.add(create(c));
//		}
//
//		return list;
//	}

//	public abstract BaseDBColumns create(final Cursor c);

	public abstract void put(final Cursor c);

//	public static final Parcelable.Creator<BaseDBColumns> CREATOR = new Parcelable.Creator<BaseDBColumns>() {
//		@SuppressWarnings({ "deprecation", "unchecked" })
//		public BaseDBColumns createFromParcel(Parcel in) {
//			HashMap<String, Object> values = in.readHashMap(null);
//			return new BaseDBColumns(values);
//		}
//
//		public BaseDBColumns[] newArray(int size) {
//			return new BaseDBColumns[size];
//		}
//	};
//
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeMap(mColumnValues);
//
//	};

}
