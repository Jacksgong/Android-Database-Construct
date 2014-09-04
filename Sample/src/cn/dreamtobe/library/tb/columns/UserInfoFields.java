package cn.dreamtobe.library.tb.columns;

import android.database.Cursor;
import cn.dreamtobe.library.db.provider.BaseTableFields;

/**
 * 
 * @author Jacks gong
 * @data 2014-9-4 上午10:07:07
 * @Describe Table fields
 */
public class UserInfoFields extends BaseTableFields {

	public final static String NAME = "name";
	public final static String SEX = "sex";
	public final static String AGE = "age";

	public UserInfoFields() {

	}

	public UserInfoFields(final Cursor c) {
		super(c);
	}

	public void setName(final String name) {
		put(NAME, name);
	}

	public String getName() {
		return (String) get(NAME);
	}

	public void setSex(final String sex) {
		put(SEX, sex);
	}

	public String getSex() {
		return (String) get(SEX);
	}

	public void setAge(final String age) {
		put(AGE, age);
	}

	public String getAge() {
		return (String) get(AGE);
	}

	@Override
	public void put(Cursor c) {
		if (c == null || c.isClosed() || c.isAfterLast()) {
			return;
		}

		set_Id(c.getInt(c.getColumnIndexOrThrow(_ID)));
		setName(c.getString(c.getColumnIndexOrThrow(NAME)));
		setSex(c.getString(c.getColumnIndexOrThrow(SEX)));
		setAge(c.getString(c.getColumnIndexOrThrow(AGE)));
	}

}
