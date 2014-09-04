package cn.dreamtobe.library.db.sample;

import java.util.HashMap;

import cn.dreamtobe.library.db.provider.BaseContentProvider;
import cn.dreamtobe.library.db.provider.BaseTableHelper;
import cn.dreamtobe.library.tb.helper.UserInfoHelper;

/**
 * 
 * @author Jacks gong
 * @data 2014-9-4 上午10:02:42
 * @Describe Database
 */
public class SampleDB extends BaseContentProvider {

	public final static String DATABASE_NAME = "sample.db";

	public final static String AUTHORITY = "sampledb";

	public final static int DATABASE_VERSION = 1;

	public SampleDB() {
		super(AUTHORITY);
	}

	@Override
	protected HashMap<String, BaseTableHelper> createAllTableHelper() {
		final HashMap<String, BaseTableHelper> hashMap = new HashMap<String, BaseTableHelper>();

		final UserInfoHelper userInfoHelper = UserInfoHelper.getImpl();
		hashMap.put(userInfoHelper.getTableName(), userInfoHelper);

		return hashMap;
	}

	@Override
	protected String getDatabaseName() {
		return DATABASE_NAME;
	}

	@Override
	protected int getDatabaseVersion() {
		return DATABASE_VERSION;
	}

}
